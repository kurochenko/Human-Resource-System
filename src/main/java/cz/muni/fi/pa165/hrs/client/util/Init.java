package cz.muni.fi.pa165.hrs.client.util;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.server.service.profession.ProfessionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Contains application initialization methods.
 *
 * @author Andrej Kuročenko <kurochenko@mail.muni.cz>
 */
@Component
public class Init {

    /** Logger */
    private static Logger logger = Logger.getLogger(Init.class);

    /** File with predefined professions */
    public static final String INIT_PROFESSIONS_FILE = "/init/professions";

    @Autowired
    @Qualifier("professionServiceImpl")
    private ProfessionService professionService;

    @Autowired
    private HttpServletRequest request;


    /**
     * Initializes professions on application start.
     */
    public void initProfessions() {
        BufferedReader br = null;

        try {
            InputStream is = request.getSession().getServletContext().getClassLoader().getResourceAsStream(INIT_PROFESSIONS_FILE);
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {

                if (professionService.findByName(line.trim()) == null) {

                    if (logger.isInfoEnabled()) {
                        logger.info("Initializing profession '" + line.trim() + "'");
                    }

                    Profession p = new Profession();
                    p.setName(line.trim());
                    professionService.create(p);
                } else if (logger.isInfoEnabled()) {
                    logger.info("Professions are already initialized");
                }

            }
        } catch (IOException e) {
            logger.error("Failed to initialize professions. " + e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error("Failed to close buffered reader of file '" + INIT_PROFESSIONS_FILE + "'. " + e.getMessage(), e);
                }
            }
        }
    }
}
