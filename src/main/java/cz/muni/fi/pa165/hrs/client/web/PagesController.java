package cz.muni.fi.pa165.hrs.client.web;

import cz.muni.fi.pa165.hrs.client.security.CustomRole;
import cz.muni.fi.pa165.hrs.client.util.Init;
import cz.muni.fi.pa165.hrs.server.service.profession.ProfessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Constoller which generates base pages for users
 * 
 * @author Vladimir Cerven <v.cerven@gmail.com>
 */
@Controller
public class PagesController {

    @Autowired
    @Qualifier("professionServiceImpl")
    private ProfessionService professionService;

    @Autowired
    private Init init;
    
    /**
     * Returns main page
     * @return index
     */
    @RequestMapping("/")
    public String renderIndex() {
        return "index";
    }
    
    /**
     * Returns login page
     * @return login
     */
    @RequestMapping("/login")
    public String renderLogin() {
        return "login";
    }
    
    /**
     * Returns registration page
     * @return register
     */
    @RequestMapping("/registration")
    public String renderReg() {
        if (professionService.findAll().isEmpty()) {
            init.initProfessions();
        }
        return "register";
    }
    
    /**
     * Returns user edit page
     * @return edit
     */
    @RequestMapping("/edit")
    @PreAuthorize("hasRole('" + CustomRole.LOGGED + "')")
    public String renderEdit() {
        return "edit";
    }
    
    /**
     * Returns user delete page
     * @return deleteAccount
     */
    @RequestMapping("/deleteAccount")
    @PreAuthorize("hasRole('" + CustomRole.LOGGED + "')")
    public String renderDelete() {
        return "deleteAccount";
    }
}
