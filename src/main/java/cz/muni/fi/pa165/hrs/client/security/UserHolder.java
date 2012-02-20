package cz.muni.fi.pa165.hrs.client.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Provides user principal from security context's session.
 *
 * @author      Andrej Kuročenko <kurocenko@mail.muni.cz>
 */
public class UserHolder {
    
    /**
     * Provides user principal from security context's session
     * @return 
     */
    public static SecurityUser getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null) ? null : (SecurityUser) auth.getPrincipal();
    }
}
