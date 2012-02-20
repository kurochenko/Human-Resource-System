package cz.muni.fi.pa165.hrs.client.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Custom authentication filter which saves anonymous {@code SecurityUser} class 
 * into security context session on successful login
 *
 * @author      Andrej Kuročenko <kurocenko@mail.muni.cz>
 */
public class CustomAnonymousAuthenticationFilter extends GenericFilterBean implements InitializingBean {
    
    private static Logger log = Logger.getLogger(CustomAnonymousAuthenticationFilter.class);
    
    /** details such as IP address etc. */
    private AuthenticationDetailsSource authenticationDetailsSource = new WebAuthenticationDetailsSource();
    
    /** anonymous user definition */
    private SecurityUser anonymousUser;
    
    /** unique key for authentication provider */
    private String key;

    
    public void setAnonymousUser(SecurityUser anonymousUser) {
        if (anonymousUser == null) {
            throw new IllegalArgumentException("Anonymous user is null");
        }        
        this.anonymousUser = anonymousUser;        
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    public void setAuthenticationDetailsSource(AuthenticationDetailsSource authenticationDetailsSource) {
        if (authenticationDetailsSource == null) {
            throw new IllegalArgumentException("AuthenticationDetailsSource required");
        }
        this.authenticationDetailsSource = authenticationDetailsSource;
    }
    
    @Override
    public void afterPropertiesSet() {
        Assert.notNull(anonymousUser);
        Assert.hasLength(key);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        if (applyAnonymousForThisRequest((HttpServletRequest) req)) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(createAuthentication((HttpServletRequest) req));

                if (log.isDebugEnabled()) {
                    log.debug("Populated SecurityContextHolder with anonymous token: '"
                        + SecurityContextHolder.getContext().getAuthentication() + "'");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("SecurityContextHolder not populated with anonymous token, as it already contained: '"
                        + SecurityContextHolder.getContext().getAuthentication() + "'");
                }
            }
        }

        chain.doFilter(req, res);
    }
    
    /**
     * Creates anonymous authentication token with specified anonymous user
     * @param request servers for building authentication details
     * @return anonymous authentication token with specified anonymous user
     */
    protected Authentication createAuthentication(HttpServletRequest request) {
       
        if (log.isInfoEnabled()) {
            log.debug("Creating authentication token for anonymous user");
        }

        AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken(
                key, 
                anonymousUser,
                anonymousUser.getAuthorities()
        );
        auth.setDetails(authenticationDetailsSource.buildDetails(request));

        return auth;
    }   
    
    /**
     * Enables subclasses to determine whether or not an anonymous authentication token should be setup for
     * this request. This is useful if anonymous authentication should be allowed only for specific IP subnet ranges
     * etc.
     *
     * @param request to assist the method determine request details
     *
     * @return <code>true</code> if the anonymous token should be setup for this request (provided that the request
     *         doesn't already have some other <code>Authentication</code> inside it), or <code>false</code> if no
     *         anonymous token should be setup for this request
     */
    protected boolean applyAnonymousForThisRequest(HttpServletRequest request) {
        return true;
    }
}
