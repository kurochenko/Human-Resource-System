package cz.muni.fi.pa165.hrs.client.web;

import cz.muni.fi.pa165.hrs.client.util.MessageSourceWrapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Controller which generates JavaScript code
 *
 * @author Andrej Kuročenko <kurochenko@mail.muni.cz>
 */
@Controller
@RequestMapping("/gen/js")
public class JSGenerationController {

    /** Logger */
    private static Logger logger = Logger.getLogger(JSGenerationController.class);

    /** name of object which is used in JavaScript code to get localized labels */
    private final String MESSAGE_OBJECT_NAME = "messages";

    @Autowired
    private MessageSourceWrapper messageSource;


    @RequestMapping("/hrs.js")
    public String generateHrs(Map<String, Object> map) {

        if (logger.isInfoEnabled()) {
            logger.info("Generating JavaScript file /js/hrs/hrs.js");
        }

        map.put(MESSAGE_OBJECT_NAME, messageSource);
        return "/js/hrs";
    }
    
    @RequestMapping("/edit.js")
    public String generateEdit(Map<String, Object> map) {

        if (logger.isInfoEnabled()) {
            logger.info("Generating JavaScript file /js/hrs/edit.js");
        }

        map.put(MESSAGE_OBJECT_NAME, messageSource);
        return "/js/edit";
    }

    @RequestMapping("/admin.js")
    public String generateAdmin(Map<String, Object> map) {

        if (logger.isInfoEnabled()) {
            logger.info("Generating JavaScript file /js/hrs/admin.js");
        }

        map.put(MESSAGE_OBJECT_NAME, messageSource);
        return "/js/admin";
    }
}
