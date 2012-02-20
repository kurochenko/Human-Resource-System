package cz.muni.fi.pa165.hrs.client.security;

import cz.muni.fi.pa165.hrs.model.user.User;
import cz.muni.fi.pa165.hrs.server.service.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Wrapper for {@code UserService} which servers for getting user details for Spring authentication
 *
 * @author Andrej Kuročenko <kurochenko@mail.muni.cz>
 */
@Service(value="securityUserServiceImpl")
public class SecurityUserServiceImpl implements UserDetailsService {

    /** Logger */
    private static Logger logger = Logger.getLogger(SecurityUserServiceImpl.class);

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String string) throws UsernameNotFoundException {

        if (logger.isInfoEnabled()) {
            logger.info("Retrieving user by username '" + string + "'");
        }
        User user = userService.findByEmail(string);
        
        if (user == null) {
            if (logger.isInfoEnabled()) {
                logger.info("User with username '" + string + "' does not exist");
            }
            throw new UsernameNotFoundException("No such user: " + string);
        }
        
        SecurityUser securityUser = new SecurityUser();
        securityUser.setUser(user);
        
        return securityUser;
    }
}
