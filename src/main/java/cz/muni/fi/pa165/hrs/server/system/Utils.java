package cz.muni.fi.pa165.hrs.server.system;

import cz.muni.fi.pa165.hrs.server.jcr.AllowedFileType;
import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static cz.muni.fi.pa165.hrs.server.jcr.JcrType.*;

/**
 * System application utility methods
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
public class Utils {

    /** Hash algorithm used to hash user passwords */
    private static final String PASSWD_HASH_ALG = "SHA1";

    /** Logger */
    private static Logger logger = Logger.getLogger(Utils.class);

    /**
     * Creates {@code SHA1} hash of password
     * @param password password in plain text to hash
     * @return hash of password
     */
    public static String passwordHashSHA1(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password is null");
        }

        if (logger.isInfoEnabled()) {
            logger.info("Hashing password using SHA1 hash");
        }
        
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(PASSWD_HASH_ALG);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Unknown algorithm '" + PASSWD_HASH_ALG + "'. " + e.getMessage());
        }
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<byteData.length;i++) {
            String hex=Integer.toHexString(0xff & byteData[i]);
            if(hex.length()==1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString();
    }

    /**
     * Creates repository skeleton structure
     * @param session JCR session
     */
    public static void createRepositorySkeleton(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Session is null");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Creating repository skeleton structure");
        }
        try {
            if (!session.getRootNode().hasNode(NODE_HRS.toString())) {
                Node hrs = session.getRootNode().addNode(NODE_HRS.toString());
                hrs.addNode(NODE_PROFESSIONS.toString());
                hrs.addNode(NODE_USERS.toString());
                session.save();
            }
        } catch (RepositoryException e) {
            logger.error("Failed to create repository skeleton structure. " + e.getMessage());
        }
    }

    /**
     * Destroys repository skeleton if exists by removing root node of skeleton (not root node of repository)
     * @param session JCR session
     */
    public static void destroyRepositorySkeleton(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Session is null");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Destroying repository skeleton structure");
        }

        try {
            if (session.getRootNode().hasNode(NODE_HRS.toString())) {
                session.getRootNode().getNode(NODE_HRS.toString()).remove();
                session.save();
            }
        } catch (RepositoryException e) {
            logger.error("Failed to destroy repository skeleton structure. " + e.getMessage());
        }
    }

    /**
     * Extracts text from user files
     *
     * @param inputStream input stream of binary file
     * @param fileType type of binary file
     * @return extracted text
     */
    public static String extractText(InputStream inputStream, AllowedFileType fileType) {
        if (inputStream == null) {
            throw new IllegalArgumentException("File input stream is null");
        }
        if (fileType == null) {
            throw new IllegalArgumentException("File type is null");
        }

        ContentHandler textHandler = new BodyContentHandler();

        try {
            if (logger.isInfoEnabled()) {
                logger.info("Parsing text from users CV file of type " + fileType.toString());
            }
            fileType.getParser().parse(inputStream, textHandler, new Metadata(),  new ParseContext());
        } catch (IOException e) {
            logger.error("Failed to extract text from " + fileType.toString() + " file." + e.getMessage());
        } catch (SAXException e) {
            logger.error("Failed to extract text from " + fileType.toString() + " file." + e.getMessage());
        } catch (TikaException e) {
            logger.error("Failed to extract text from " + fileType.toString() + " file." + e.getMessage());
        }

        return textHandler.toString().trim();
    }
}
