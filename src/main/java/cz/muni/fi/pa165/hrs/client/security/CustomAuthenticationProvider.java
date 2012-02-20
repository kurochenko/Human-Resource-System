package cz.muni.fi.pa165.hrs.client.security;

import cz.muni.fi.pa165.hrs.model.user.User;
import cz.muni.fi.pa165.hrs.server.service.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Provides custom authentication of users with given username and password hash
 *
 * @author      Andrej Kuročenko <kurocenko@mail.muni.cz>
 */
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static Logger logger = Logger.getLogger(CustomAuthenticationProvider.class);
    
    /** service for getting user details */
    private UserDetailsService userDetailsService;
    
    /** serves for encoding passwords */
    private PasswordEncoder passwordEncoder;

    
    /**
     * Sets <code>UserDetailsService</code> for getting user according 
     * username
     * @param userDetailsService 
     */
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Sets desirable password encoder
     * @param passwordEncoder 
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public Authentication authenticate(Authentication a) throws AuthenticationException {
        if (passwordEncoder == null) {
            throw new IllegalStateException("No password encoder provided");
        }
        
        String username = String.valueOf(a.getPrincipal());
        String password = String.valueOf(a.getCredentials());
        String hashPasswd = passwordEncoder.encodePassword(password, null);
        
        if (logger.isDebugEnabled()) {
            logger.debug("Authenticating user " + username);
        }
        
        SecurityUser userDetails;

        if (userDetailsService == null) {
            throw new IllegalArgumentException("User details service is null");
        }
         
        userDetails = (SecurityUser) userDetailsService.loadUserByUsername(username);
        
        if (!userDetails.getUsername().equals(username)) {
            throw new UsernameNotFoundException("No such username.");
        }
        if (!userDetails.getPassword().equals(hashPasswd)) {
            throw new BadCredentialsException("Wrong password");
        }

        userDetails.addAuthority(CustomRole.LOGGED);
        if (userDetails.getUser().isPrivileged()) {
            System.out.println("User is privileged, adding role" + CustomRole.ADMIN);
            userDetails.addAuthority(CustomRole.ADMIN);
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails, 
                userDetails.getPassword(), 
                userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<? extends Object> type) {
        return type.equals(UsernamePasswordAuthenticationToken.class);
    }    
}
