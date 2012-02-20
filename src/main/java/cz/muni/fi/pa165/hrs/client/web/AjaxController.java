package cz.muni.fi.pa165.hrs.client.web;

import cz.muni.fi.pa165.hrs.client.security.CustomRole;
import cz.muni.fi.pa165.hrs.client.util.MessageSourceWrapper;
import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.model.user.UserDTO;
import cz.muni.fi.pa165.hrs.server.service.profession.ProfessionService;
import cz.muni.fi.pa165.hrs.server.service.user.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controller which generates response for AJAX calls
 *
 * @author Vladimir Cerven <v.cerven@gmail.com>
 */
@Controller
@RequestMapping("/ajax")
public class AjaxController {
    
    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;
    
    @Autowired
    @Qualifier("professionServiceImpl")
    private ProfessionService professionService;

    @Autowired
    private MessageSourceWrapper messageSourceWrapper;
    
    /**
     * Fulltext search for query
     * 
     * @param query search query
     * 
     * @return results
     */
    @RequestMapping(value = "/search/{query}", method = RequestMethod.GET)
    public @ResponseBody List<UserDTO> search(@PathVariable("query") String query){       
        
        return userService.findFulltext(query);
    }
    
    /**
     * Professions in database
     * 
     * @return professions All professions
     */
    @RequestMapping("/professions")
    public @ResponseBody List<Profession> getProfessions(){
        return professionService.findAll();
    }
    
    /**
     * Delete profession with id
     * 
     * @param id 
     */
    /*@RequestMapping(value = "/deleteProfession", method = RequestMethod.GET)
    @PreAuthorize("hasRole('" + CustomRole.LOGGED + "')")
    public @ResponseBody void deleteProfession(@RequestParam("id") String id){
        professionService.remove(id);
    }*/
    
    /**
     * Add profession with name
     * 
     * @param name of new profession
     * @return status of process
     */
    @RequestMapping(value = "/addProfession", method = RequestMethod.GET)
    @PreAuthorize("hasRole('" + CustomRole.LOGGED + "')")
    public @ResponseBody Map<String, String> addProfession(@RequestParam("name") String name){
        
        Map<String, String> result = new HashMap<String, String>();
        
        if(name == null || professionService.findByName(name) != null){
            result.put("status", "error");
            result.put("error", messageSourceWrapper.getLabel("error.label.exists.profession"));
            return result;
        }
        
        Profession profession = new Profession();
        profession.setName(name);
        professionService.create(profession);
        
        result.put("status", "ok");
        result.put("id", profession.getUuid());
        result.put("name", profession.getName());
        return result;
    }
}
