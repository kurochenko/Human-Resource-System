package cz.muni.fi.pa165.hrs.server.jcr;


import cz.muni.fi.pa165.hrs.server.system.Utils;
import org.apache.log4j.Logger;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.InputStream;

/**
 * Custom JCR session factory for JackRabbit purposes serves for creating and destroying of JCR Session
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
public class JcrSessionFactory {

    /** Logger */
    private static Logger logger = Logger.getLogger(JcrSessionFactory.class);

    /** JCR repository */
    private Repository repository;

    /** Credentials used for logging into JCR repository */
    private Credentials credentials;

    /** JCR session created by logging into repository */
    private Session session;

    /** InputStream of CND file with custom node types */
    private InputStream cndNoteTypes;


    /**
     * Constructor, sets private attributes
     * @param repository JCR repository
     * @param credentials Credentials used for logging into JCR repository
     * @param cndNodeTypes InputStream of CND file with custom node types
     */
    public JcrSessionFactory(Repository repository, Credentials credentials, InputStream cndNodeTypes) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository is null");
        }
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials are null");
        }
        if (cndNodeTypes == null) {
            throw new IllegalArgumentException("CND file input stream is null");
        }

        this.repository = repository;
        this.credentials = credentials;
        this.cndNoteTypes = cndNodeTypes;
    }

    /**
     * Logs into JCR repository, creates JCR sesssion, imports custom node types and builds repository skeleton structure
     * @return JCR session
     */
    public Session login() {
        if (logger.isInfoEnabled()) {
            logger.info("Logging into repository and creating session");
        }
        try {
            session = repository.login(credentials);
            TransformationUtils.importCustomNodeTypes(session, cndNoteTypes);
            Utils.createRepositorySkeleton(session);
        } catch (RepositoryException ex) {
            logger.error("Failed to log in to Jackrabbit repository. " + ex.getMessage());
        }

        return session;
    }

    /**
     * Destroys JCR session if exists
     */
    public void logout() {
        if (session != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Destroying session " + session.toString());
            }
            session.logout();
        }
    }
}
