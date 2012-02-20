package cz.muni.fi.pa165.hrs.server.jcr;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.model.user.User;
import cz.muni.fi.pa165.hrs.model.user.UserDTO;
import cz.muni.fi.pa165.hrs.server.system.Utils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.log4j.Logger;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static cz.muni.fi.pa165.hrs.server.jcr.JcrType.*;

/**
 * Static methods for transformation of Repository objects to HRS specified objects
 * or conversion between similar object types
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
public class TransformationUtils {

    private static Logger logger = Logger.getLogger(TransformationUtils.class);

    /**
     * Transforms JCR {@code Node} into {@code Profession} object
     * @param node JCR {@code Node}
     * @return {@code Profession} object
     */
    public static Profession nodeToProfession(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Node is null");
        }

        Profession profession = null;
        try {
            profession = new Profession();
            profession.setUuid(node.getIdentifier());
            profession.setName(node.getProperty(PROP_NAME.toString()).getString());
        } catch (RepositoryException ex) {
            logger.error("Failed to transform JCR Node to Profession. " + ex.getMessage());
        }

        return profession;
    }

    /**
     * Transforms JCR {@code Node} into {@code User} object
     * @param node JCR {@code Node}
     * @return {@code User} object
     */
    public static User nodeToUser(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Node is null");
        }

        User user = null;
        try {
            user = new User();
            user.setUuid(node.getIdentifier());
            user.setPassword(node.getProperty(PROP_PASSWORD.toString()).getString());
            user.setEmail(node.getProperty(PROP_EMAIL.toString()).getString());
            user.setName(node.getProperty(PROP_NAME.toString()).getString());
            user.setPrivileged(node.getProperty(PROP_PRIVILEGED.toString()).getBoolean());
            user.setProfession(nodeToProfession(node.getSession().getNodeByIdentifier(node.getProperty(PROP_PROFESSION.toString()).getString())));
        } catch (RepositoryException ex) {
            logger.error("Failed to transform JCR Node to User. " + ex.getMessage());
        }

        return user;
    }

    /**
     * Transforms JCR {@code Node} into {@code UserDTO} object
     * @param node JCR {@code Node}
     * @return {@code UserDTO} object
     */
    public static UserDTO nodeToUserDTO(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Node is null");
        }

        UserDTO user = null;
        try {
            user = new UserDTO();
            user.setUuid(node.getIdentifier());
            user.setName(node.getProperty(PROP_NAME.toString()).getString());
            user.setProfession(nodeToProfession(node.getSession().getNodeByIdentifier(node.getProperty(PROP_PROFESSION.toString()).getString())));

        } catch (RepositoryException ex) {
            logger.error("Failed to transform JCR Node to UserDTO. " + ex.getMessage());
        }

        return user;
    }

    /**
     * Imports custom node types into repository
     * @param session JCR session
     * @param cndFile CND file with node types definition
     */
    public static void importCustomNodeTypes(Session session, InputStream cndFile) {
        if (logger.isInfoEnabled()) {
            logger.info("Importing custom node types into repository");
        }
        NodeType[] nodeTypes = new NodeType[0];
        try {
            nodeTypes = CndImporter.registerNodeTypes(new BufferedReader(new InputStreamReader(cndFile)), session);
        } catch (ParseException e) {
            logger.error("Failed to parse CND file with repository custom node types. " + e.getMessage());
        } catch (RepositoryException e) {
            logger.error("Failed to import custom node types into repository. " + e.getMessage());
        } catch (IOException e) {
            logger.error("IO Exception while importing custom node types into repository. " + e.getMessage());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Registered node types: ");
            for (NodeType nt : nodeTypes) {
                logger.debug("\t" + nt.getName());
            }
        }
    }

    /**
     * Converts file to JCR binary content and stores to new node
     * @param userNode note to which children node will be created with binary content
     *                 * @param cvIStream CV file input stream
     * @param fileType type of file stored to node
     */
    public static void convertFileStreamToNode(Node userNode, InputStream cvIStream, AllowedFileType fileType) {
        if (userNode == null) {
            throw new IllegalArgumentException("User node is null");
        }
        if (cvIStream == null) {
            throw new IllegalArgumentException("CV file input stream is null");
        }
        if (fileType == null) {
            throw new IllegalArgumentException("File type is null");
        }

        try {
            Node cvNode = userNode.addNode(fileType.getNodeType().toString(), "nt:file");
            cvNode.addMixin(MIXIN_HASCONTENT.toString());
            Node content = cvNode.addNode("jcr:content", "nt:resource");
            content.setProperty("jcr:data", userNode.getSession().getValueFactory().createBinary(cvIStream));
            content.setProperty("jcr:mimeType", fileType.getMimeType());
            cvNode.setProperty(PROP_CONTENT.toString(), Utils.extractText(content.getProperty("jcr:data").getBinary().getStream(), fileType));
        } catch (RepositoryException e) {
            logger.error("Failed to convert " + fileType.toString() + " file to binary node. " + e.getMessage());
        }
    }

    /**
     * Transforms {@code User} object to {@code UserDTO} object 
     * @param user user object which will be transformed to DTO
     * @return user DTO object
     */
    public static UserDTO userToDTO(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Converting User '" + user.getName() + " (" + user.getEmail() + ")' to UserDTO");
        }
        UserDTO result = new UserDTO();
        result.setUuid(user.getUuid());
        result.setName(user.getName());
        result.setProfession(user.getProfession());

        return result;
    }
}
