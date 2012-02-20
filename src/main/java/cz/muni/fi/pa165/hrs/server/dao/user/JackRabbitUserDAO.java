package cz.muni.fi.pa165.hrs.server.dao.user;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.model.user.User;
import cz.muni.fi.pa165.hrs.model.user.UserDTO;
import cz.muni.fi.pa165.hrs.server.jcr.AllowedFileType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static cz.muni.fi.pa165.hrs.server.jcr.JcrType.*;
import static cz.muni.fi.pa165.hrs.server.jcr.TransformationUtils.*;

/**
 * Implementation of UserDAO interface using JSR 170 implementation Apache JackRabbit
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
@Repository
public class JackRabbitUserDAO implements UserDAO {

    /** Logger */
    private static Logger logger = Logger.getLogger(JackRabbitUserDAO.class);

    /** JCR session */
    private Session session;


    @Autowired
    @Override
    public void setSession(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Session is null");
        }
        this.session = session;
    }

    @Override
	public void create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (user.getUuid() != null) {
            throw new IllegalArgumentException("Users UUID is not null");
        }
        if (user.getProfession() == null) {
            throw new IllegalArgumentException("Users profession is null");
        }
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Users email is null");
        }
        if (user.getPassword() == null) {
            throw new IllegalArgumentException("Users password is null");
        }

        try {
            if (logger.isInfoEnabled()) {
                logger.info("Creating new user '" + user.getEmail() + "'");
            }
            
            Node newUser = session.getRootNode().getNode(NODE_HRS.toString()).getNode(NODE_USERS.toString()).addNode(NODE_USER.toString());
            newUser.setProperty(PROP_PASSWORD.toString(), user.getPassword());
            newUser.setProperty(PROP_EMAIL.toString(), user.getEmail());
            newUser.setProperty(PROP_NAME.toString(), user.getName());
            newUser.setProperty(PROP_PRIVILEGED.toString(), user.isPrivileged());
            newUser.setProperty(PROP_PROFESSION.toString(), session.getNodeByIdentifier(user.getProfession().getUuid()));

            session.save();

            user.setUuid(findByEmail(user.getEmail()).getUuid());
        } catch (RepositoryException ex) {
            logger.error("Failed to create new user '" + user.getEmail() + "'. " + ex.getMessage());
        }
    }

    @Override
	public void edit(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (user.getUuid() == null) {
            throw new IllegalArgumentException("Users UUID is null");
        }
        if (user.getProfession() == null) {
            throw new IllegalArgumentException("Users profession is null");
        }
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Users email is null");
        }
        if (user.getPassword() == null) {
            throw new IllegalArgumentException("Users password is null");
        }

        try {
            if (logger.isInfoEnabled()) {
                logger.info("Updating user '" + user.getEmail() + "'");
            }

            Node oldUser = session.getNodeByIdentifier(user.getUuid());
            oldUser.getProperty(PROP_PASSWORD.toString()).setValue(user.getPassword());
            oldUser.getProperty(PROP_EMAIL.toString()).setValue(user.getEmail());
            oldUser.getProperty(PROP_NAME.toString()).setValue(user.getName());
            oldUser.setProperty(PROP_PRIVILEGED.toString(), user.isPrivileged());
            oldUser.setProperty(PROP_PROFESSION.toString(), session.getNodeByIdentifier(user.getProfession().getUuid()));
            session.save();
        } catch (RepositoryException ex) {
            logger.error("Failed to update user '" + user.getEmail() + "'. " + ex.getMessage());
        }
	}

    @Override
	public void remove(String uuid) {
		if (uuid == null) {
            throw new IllegalArgumentException("User UUID is null");
        }

        try {
            if (logger.isInfoEnabled()) {
                logger.info("Removing user with UUID '" + uuid + "'");
            }

            session.getNodeByIdentifier(uuid).remove();
            session.save();
        } catch (RepositoryException ex) {
            logger.error("Failed to remove user identified by UUID '" + uuid + "'. " + ex.getMessage());
        }
	}

    @Override
	public User findByUuid(String uuid) {
		if (uuid == null) {
            throw new IllegalArgumentException("No user with given UUID was no  t found");
        }

        Node userNode = null;
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Retrieving user with UUID '" + uuid + "'");
            }
            userNode = session.getNodeByIdentifier(uuid);
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve user identified by UUID '" + uuid + "'. " + e.getMessage());
        }
        return (userNode == null) ? null : nodeToUser(userNode);
	}

    @Override
    public User findByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("User email is null");
        }

        User result = null;
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Retrieving user with email '" + email + "'");
            }

            String query = "SELECT * FROM [nt:base] WHERE name()='" + NODE_USER.toString() + "' AND [" + PROP_EMAIL.toString() + "]=$email";
            Query q = session.getWorkspace().getQueryManager().createQuery(query, Query.JCR_SQL2);
            q.bindValue("email", session.getValueFactory().createValue(email));

            NodeIterator nit = q.execute().getNodes();
            if (nit.hasNext()) {
                result = nodeToUser((Node) nit.next());
            }

        } catch (RepositoryException ex) {
            logger.error("Failed to retrieve user by email '" + email + "'. " + ex.getMessage());
        }

        return result;
    }

    @Override
    public List<UserDTO> findAll() {
		List<UserDTO> result = new ArrayList<UserDTO>();
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Retrieving all users");
            }

            NodeIterator nit = session.getRootNode().getNode(NODE_HRS.toString()).getNode(NODE_USERS.toString()).getNodes();
            while (nit.hasNext()) {
                result.add(nodeToUserDTO((Node) nit.next()));
            }
        } catch (RepositoryException ex) {
            logger.error("Failed to retrieve all users");
        }
        
        return result;
	}

    @Override
	public List<UserDTO> findByProfession(Profession profession) {
        if (profession == null) {
            throw new IllegalArgumentException("Profession is null");
        }
        if (profession.getName() == null) {
            throw new IllegalArgumentException("Profession name is null");
        }

        List<UserDTO> result = new ArrayList<UserDTO>();
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Retrieving user with profession '" + profession.getName() + "'");
            }

            String query = "SELECT * FROM [nt:base] WHERE name()='" + NODE_USER.toString() + "' AND [" + PROP_PROFESSION.toString() + "]=$uuid";
            Query q = session.getWorkspace().getQueryManager().createQuery(query, Query.JCR_SQL2);
            q.bindValue("uuid", session.getValueFactory().createValue(profession.getUuid()));

            NodeIterator nit = q.execute().getNodes();
            while (nit.hasNext()) {
                result.add(nodeToUserDTO((Node) nit.next()));
            }

        } catch (RepositoryException ex) {
            logger.error("Failed to retrieve user by profession '" + profession.getName() + "'. " + ex.getMessage());
        }

        return result;
	}

    @Override
	public List<UserDTO> findFullText(String query) {
		if (query == null) {
            throw new IllegalArgumentException("Query is null");
        }

        List<UserDTO> users = new ArrayList<UserDTO>();
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Retrieving user using fulltext search for query '" + query + "'");
            }
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery("SELECT [hrs:content] FROM [nt:file] as file WHERE CONTAINS(file.[hrs:content], $query)", Query.JCR_SQL2);
            q.bindValue("query", session.getValueFactory().createValue(query));
            NodeIterator nit = q.execute().getNodes();
            while (nit.hasNext()) {
                Node actual = (Node) nit.next();
                UserDTO actualUser = nodeToUserDTO(actual.getParent());
                if (!users.contains(actualUser)) {
                    users.add(actualUser);
                }
            }
            
        } catch (RepositoryException ex) {
            logger.error("Failed to retrieve users by fulltext search for query'" + query + "'. " + ex.getMessage());
        }
        return users;
	}

    @Override
    public void addCv(String userId, InputStream cvStream, AllowedFileType fileType) {
        if (userId == null) {
            throw new IllegalArgumentException("User UUID is null");
        }
        if (cvStream == null) {
            throw new IllegalArgumentException("Input stream of CV file is null");
        }
        if (fileType == null) {
            throw new IllegalArgumentException("CV file type is null");
        }

        try {
            if (logger.isInfoEnabled()) {
                logger.info("Adding CV file (" + fileType.toString() + ") to user '" + userId + "'");
            }
            Node userNode = session.getNodeByIdentifier(userId);
            convertFileStreamToNode(userNode, cvStream, fileType);
            session.save();
        } catch (RepositoryException e) {
            logger.error("Failed to add CV file to user '" + userId + "'");
        }
    }

    @Override
    public InputStream getCv(String userId, AllowedFileType fileType) {
        if (userId == null) {
            throw new IllegalArgumentException("User UUID is null");
        }
        if (fileType == null) {
            throw new IllegalArgumentException("File type is null");
        }


        InputStream result = null;
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Retrieving CV file (" + fileType.toString() + ") from user '" + userId + "'");
            }

            Node contentNode = session.getNodeByIdentifier(userId).getNode(fileType.getNodeType().toString()).getNode("jcr:content");
            result = contentNode.getProperty("jcr:data").getBinary().getStream();
        } catch (RepositoryException e) {
            logger.error("Failed to retrieve CV file from user '" + userId + "'");
        }

        return result;
    }
}
