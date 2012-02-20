package cz.muni.fi.pa165.hrs.client.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Helper class for delegating spring message bundle to generated JavaScript code
 *
 * @author Andrej Kuročenko <kurochenko@mail.muni.cz>
 */
@Component
public class MessageSourceWrapper {

    private static Logger logger = Logger.getLogger(MessageSourceWrapper.class);

    /** Message source bundle */
    @Autowired
    private MessageSource messageSource;

    /** Servlet request for retrieving current application Locale */
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LocaleResolver localeResolver;


    /**
     * Gets localized label for given <code>key</code>
     * @param key key under which is label searched
     * @return localized label according to logged user locale
     */
    public String getLabel(String key) {
        if (messageSource == null) {
            throw new IllegalStateException("Message source is null");
        }

        Locale locale = localeResolver.resolveLocale(request);

        if (logger.isDebugEnabled()) {
            logger.debug("Getting label for key: " + key + " in locale " + locale.getLanguage());
        }
        
        String result = "[ERR: Label for key '" + key + "' was not found]";
        try {
            result = messageSource.getMessage(key, new Object[0], locale);
        } catch (NoSuchMessageException ex) {
            logger.warn("Label for key '" + key + "' was not found");
        }

        return result;
    }
}
