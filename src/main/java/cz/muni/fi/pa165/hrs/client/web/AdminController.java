package cz.muni.fi.pa165.hrs.client.web;

import cz.muni.fi.pa165.hrs.client.security.CustomRole;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller which generates admin pages
 * 
 * @author Vladimir Cerven <v.cerven@gmail.com>
 */
@Controller
@PreAuthorize("hasRole('" + CustomRole.ADMIN + "')")
public class AdminController {
    
    /**
     * Returns edit professions page
     * @return professions
     */
    @RequestMapping("/editProfessions")
    public String renderProfessions(){
        
        return "professions";
    }
}
