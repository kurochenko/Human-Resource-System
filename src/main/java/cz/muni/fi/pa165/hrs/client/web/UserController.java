package cz.muni.fi.pa165.hrs.client.web;

import cz.muni.fi.pa165.hrs.client.security.CustomRole;
import cz.muni.fi.pa165.hrs.client.security.SecurityUser;
import cz.muni.fi.pa165.hrs.client.util.MessageSourceWrapper;
import cz.muni.fi.pa165.hrs.model.user.User;
import cz.muni.fi.pa165.hrs.server.jcr.AllowedFileType;
import cz.muni.fi.pa165.hrs.server.service.profession.ProfessionService;
import cz.muni.fi.pa165.hrs.server.service.user.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Controller which controll user actions
 * 
 * @author Vladimir Cerven <v.cerven@gmail.com>
 */
@Controller
@RequestMapping("/user")
public class UserController {

    /** Logger */
    private static Logger logger = Logger.getLogger(UserController.class);

    /** Message returned as JSON when all operations processed successfuly */
    public static final String SUCCESS_MSG = "ok";

    /** JSON return message key */
    public static final String RETURN_MSG_KEY = "error";

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Autowired
    @Qualifier("professionServiceImpl")
    private ProfessionService professionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MessageSourceWrapper messageSourceWrapper;
    
    @Autowired
    @Qualifier("loggedUser")
    private SecurityUser user;


    /**
     * Method for validation emails
     * 
     * @param mail
     * 
     * @return true or false
     */
    private boolean checkEmail(String mail) {
        String regexp = "^[a-z0-9_\\+-]+(\\.[a-z0-9_\\+-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*\\.([a-z]{2,4})$";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(mail);
        return matcher.matches();
    }

    /**
     * Add new user in database
     * 
     * @param name
     * @param password
     * @param mail
     * @param profession
     * @param pdf
     * @param odt
     * 
     * @return status
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public @ResponseBody Map<String, String> register(@RequestParam("name") String name, 
            @RequestParam("password") String password,
            @RequestParam("mail") String mail, @RequestParam("profession") String profession,
            @RequestParam("pdf") MultipartFile pdf, @RequestParam("odt") MultipartFile odt) {
        
        Map<String, String> result = new HashMap<String, String>();
        
        if (name == null || name.equals("")) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.empty.name"));
            return result;
        }
        if (password == null || password.length() < 6) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.short.password"));
            return result;
        }
        if (mail == null || !checkEmail(mail)) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.notmatch.email"));
            return result;
        }
        if (profession == null || professionService.findByUuid(profession) == null) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.empty.profession"));
            return result;
        }
        if (userService.findByEmail(mail) != null) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.exists.email"));
            return result;
        }
        if(pdf == null || pdf.isEmpty()){
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.empty.cv.pdf"));
            return result;
        }
        if(odt == null || odt.isEmpty()){
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.empty.cv.odt"));
            return result;
        }
        if (!pdf.getContentType().equals(AllowedFileType.PDF.getMimeType())) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.mimetype.cv.pdf"));
            return result;
        }
        if (!odt.getContentType().equals(AllowedFileType.ODT.getMimeType())) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.mimetype.cv.odt"));
            return result;
        }

        User user = new User();
        user.setName(name);
        user.setPassword(passwordEncoder.encodePassword(password,null));
        user.setEmail(mail);
        user.setProfession(professionService.findByUuid(profession));

        if (userService.findAll().isEmpty()) {
            user.setPrivileged(true);
        }

        userService.create(user);
        
        InputStream pdfInput;
        InputStream odtInput;
                
        try {
            pdfInput = pdf.getInputStream();
            odtInput = odt.getInputStream();
        } catch (IOException ex) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.load.cv"));
            logger.error("Failed to create InputStream from users CV files. " + ex.getMessage(), ex);
            return result;
        }
        
        userService.addCv(user.getUuid(), pdfInput, AllowedFileType.PDF);
        userService.addCv(user.getUuid(), odtInput, AllowedFileType.ODT);
        
        result.put(RETURN_MSG_KEY, SUCCESS_MSG);
        return result;
    }
    
    /**
     * Process link to download CV in PDF
     * 
     * @param id
     * @param response 
     */
    @RequestMapping(value = "/cv/{query}/pdf", method = RequestMethod.GET)
    public void getPdfCv(@PathVariable("query") String id, HttpServletResponse response) {
        offerToDownloadCv(id, AllowedFileType.PDF, response);
    }
    
    /**
     * Process link to download CV in PDF
     * 
     * @param id
     * @param response 
     */
    @RequestMapping(value = "/cv/{query}/odt", method = RequestMethod.GET)
    public void getOdtCv(@PathVariable("query") String id, HttpServletResponse response){
        offerToDownloadCv(id, AllowedFileType.ODT, response);
    }

    /**
     * Returns file to download
     * 
     * @param userId
     * @param fileType
     * @param response 
     */
    private void offerToDownloadCv(String userId, AllowedFileType fileType, HttpServletResponse response) {
        User user = userService.findByUuid(userId);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition","attachment;filename="+user.getName()+"." + fileType.toString());

        ServletOutputStream out = null;
        InputStream in = null;
        try {
            out = response.getOutputStream();
            in = userService.getCv(userId, fileType);

            byte[] outputByte = new byte[4096];
            while(in.read(outputByte, 0, 4096) != -1)
            {
                out.write(outputByte, 0, 4096);
            }

        } catch (IOException e) { 
            logger.error("Failed to read InputStream of users CV. " + e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("Failed to close InputStream of users CV. " + e.getMessage(), e);
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("Failed to close OutputStream of users CV. " + e.getMessage(), e);
                }
            }
        }
    }
    
    /**
     * Change name of logged user
     * 
     * @param name
     * @return status
     */
    @RequestMapping(value = "/changeName", method = RequestMethod.POST)
    @PreAuthorize("hasRole('" + CustomRole.LOGGED + "')")
    public @ResponseBody Map<String, String> changeName(@RequestParam("name") String name) {
        
        Map<String, String> result = new HashMap<String, String>();
        
        if (name == null || name.equals("")) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.empty.name"));
            return result;
        }
        
        User u = user.getUser();
        u.setName(name);
        
        userService.edit(u);
        
        result.put(RETURN_MSG_KEY, SUCCESS_MSG);
        return result;
    }
    
    /**
     * Change password of logged user
     * 
     * @param old
     * @param password
     * @return status
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @PreAuthorize("hasRole('" + CustomRole.LOGGED + "')")
    public @ResponseBody Map<String, String> changePassword(@RequestParam("old") String old,
    @RequestParam("password") String password) {
        
        Map<String, String> result = new HashMap<String, String>();
        
        if (!passwordEncoder.encodePassword(old, null).equals(user.getPassword())) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.notmatch.oldpassword"));
            return result;
        }
        if (password == null || password.length() < 6) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.short.password"));
            return result;
        }
        
        
        User u = user.getUser();
        u.setPassword(passwordEncoder.encodePassword(password,null));
        
        userService.edit(u);
        
        result.put(RETURN_MSG_KEY, SUCCESS_MSG);
        return result;
    }
    
    /**
     * Change profession of logged user
     * 
     * @param profession
     * @return status
     */
    @RequestMapping(value = "/changeProfession", method = RequestMethod.POST)
    @PreAuthorize("hasRole('" + CustomRole.LOGGED + "')")
    public @ResponseBody Map<String, String> changeProfession(@RequestParam("profession") String profession) {
        
        Map<String, String> result = new HashMap<String, String>();
        
        if (profession == null || professionService.findByUuid(profession) == null) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.empty.profession"));
            return result;
        }
        
        User u = user.getUser();
        u.setProfession(professionService.findByUuid(profession));
        
        userService.edit(u);
        
        result.put(RETURN_MSG_KEY, SUCCESS_MSG);
        return result;
    }
    
    /**
     * Upload new CV of logged user
     * 
     * @param pdf
     * @param odt
     * @return status
     */
    @RequestMapping(value = "/changeCV", method = RequestMethod.POST)
    @PreAuthorize("hasRole('" + CustomRole.LOGGED + "')")
    public @ResponseBody Map<String, String> changeCV(@RequestParam("pdf") MultipartFile pdf, 
            @RequestParam("odt") MultipartFile odt) {
        
        Map<String, String> result = new HashMap<String, String>();
        
        if(pdf == null || pdf.isEmpty()){
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.empty.cv.pdf"));
            return result;
        }
        if(odt == null || odt.isEmpty()){
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.empty.cv.odt"));
            return result;
        }
        if (!pdf.getContentType().equals(AllowedFileType.PDF.getMimeType())) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.mimetype.cv.pdf"));
            return result;
        }
        if (!odt.getContentType().equals(AllowedFileType.ODT.getMimeType())) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.mimetype.cv.odt"));
            return result;
        }
        
        InputStream pdfInput;
        InputStream odtInput;
                
        try {
            pdfInput = pdf.getInputStream();
            odtInput = odt.getInputStream();
        } catch (IOException ex) {
            result.put(RETURN_MSG_KEY, messageSourceWrapper.getLabel("error.label.load.cv"));
            logger.error("Failed to create InputStream from users CV files. " + ex.getMessage());
            return result;
        }
        
        userService.addCv(user.getUser().getUuid(), pdfInput, AllowedFileType.PDF);
        userService.addCv(user.getUser().getUuid(), odtInput, AllowedFileType.ODT);
        
        result.put(RETURN_MSG_KEY, SUCCESS_MSG);
        return result;
    }
    
    /**
     * Get details of logged user
     * 
     * @return json with name and profession id
     */
    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    @PreAuthorize("hasRole('" + CustomRole.LOGGED + "')")
    public @ResponseBody Map<String, String> getUser() {
        
        Map<String, String> result = new HashMap<String, String>();
        
        result.put("name", user.getUser().getName());
        result.put("profession", user.getUser().getProfession().getUuid());
        
        return result;
    }
    
    /**
     * Delete logged account
     * 
     * @return status
     */
    @RequestMapping(value = "/deleteAccount", method = RequestMethod.POST)
    @PreAuthorize("hasRole('" + CustomRole.LOGGED + "')")
    public @ResponseBody Map<String, String> deleteMyAccount() {
        
        Map<String, String> result = new HashMap<String, String>();
        
        userService.remove(user.getUser().getUuid());
        
        result.put("error", "ok");
        
        return result;
    }
    
}
