package cz.muni.fi.pa165.hrs.server.dao.profession;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import java.util.ArrayList;
import java.util.List;

import static cz.muni.fi.pa165.hrs.server.jcr.JcrType.*;
import static cz.muni.fi.pa165.hrs.server.jcr.TransformationUtils.nodeToProfession;

/**
 * Implementation of ProfessionDAO interface using JSR 170 implementation Apache JackRabbit
 *
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
@Repository
public class JackRabbitProfessionDAO implements ProfessionDAO {

    /** Logger */
    private static Logger logger = Logger.getLogger(JackRabbitProfessionDAO.class);

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
    public void create(Profession profession) {
        if(profession == null) {
            throw new IllegalArgumentException("Profession is null");
        }
        if (profession.getUuid() != null) {
            throw new IllegalArgumentException("Professions uuid is already set");
        }
        if (profession.getName() == null) {
            throw new IllegalArgumentException("Professions name is null");
        }

        try {
            if (logger.isInfoEnabled()) {
                logger.info("Creating new " + NODE_PROFESSION.toString() + " node for profession '" + profession.getName() + "'");
            }
            
            Node newProfessionNode = session.getRootNode().getNode(NODE_HRS.toString()).getNode(NODE_PROFESSIONS.toString()).addNode(NODE_PROFESSION.toString());
            newProfessionNode.addMixin("mix:referenceable");
            newProfessionNode.setProperty(PROP_NAME.toString(), profession.getName());
            session.save();
            profession.setUuid(newProfessionNode.getIdentifier());
        } catch (RepositoryException ex) {
            logger.error("Failed to create " + NODE_PROFESSION + " node for value '"
                    + profession.getName() + "'. " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void edit(Profession profession) {
        if(profession == null) {
            throw new IllegalArgumentException("Profession is null");
        }
        if (profession.getUuid() == null) {
            throw new IllegalArgumentException("Professions uuid is null");
        }
        if (profession.getName() == null) {
            throw new IllegalArgumentException("Professions name is null");
        }

        try {
            if (logger.isInfoEnabled()) {
                logger.info("Updating " + NODE_PROFESSION + " node for profession '" + profession.getName()
                        + "' with UUID " + profession.getUuid() + "'");
            }
            
            session.getNodeByIdentifier(profession.getUuid()).getProperty(PROP_NAME.toString()).setValue(profession.getName());
            session.save();
        } catch (RepositoryException ex) {
            logger.error("Failed to update " + NODE_PROFESSION + " node for profession '" + profession.getName()
                    + "' and UUID '" + profession.getUuid() + "'. " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void remove(String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Profession UUID is null");
        }

        try {
            if (logger.isInfoEnabled()) {
                logger.info("Removing " + NODE_PROFESSION + " node for profession UUID '" + uuid);
            }
            session.getNodeByIdentifier(uuid).remove();
            session.save();
        } catch (RepositoryException ex) {
            logger.error("Failed to remove " + NODE_PROFESSION + " node for profession UUID '" + uuid + "'. " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Profession findByUuid(String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID is null");
        }

        Node actualNode = null;
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Getting " + NODE_PROFESSION + " node using UUID " + uuid);
            }
            actualNode = session.getNodeByIdentifier(uuid);
        } catch (RepositoryException ex) {
            logger.error("Failed to retrieve " + NODE_PROFESSION + " node for UUID '" + uuid + "'. " + ex.getMessage());
        }
        return (actualNode == null) ? null : nodeToProfession(actualNode);
    }

    @Override
    public Profession findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }

        Profession result = null;
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Getting " + NODE_PROFESSION + " node using " + PROP_NAME + " attribute with value '" + name + "'");
            }

            String query = "SELECT * FROM [nt:base] WHERE name()='" + NODE_PROFESSION.toString() + "' AND [" + PROP_NAME.toString() + "]=$name";
            Query q = session.getWorkspace().getQueryManager().createQuery(query, Query.JCR_SQL2);
            q.bindValue("name", session.getValueFactory().createValue(name));

            NodeIterator nit = q.execute().getNodes();
            if (nit.hasNext()) {
                result = nodeToProfession((Node) nit.next());
            }
        } catch (RepositoryException ex) {
            logger.error("Failed to get " + NODE_PROFESSION + " node for attribute " + PROP_NAME
                    + " and value " + name + ". " + ex.getMessage());
        }
        
        return result;
    }

    @Override
    public List<Profession> findAll() {
        List<Profession> result = new ArrayList<Profession>();
        NodeIterator nit = null;

        try {
            if (logger.isInfoEnabled()) {
                logger.info("Getting all " + NODE_PROFESSION + " nodes.");
            }
            nit = session.getRootNode().getNode(NODE_HRS.toString()).getNode(NODE_PROFESSIONS.toString()).getNodes();
        } catch (RepositoryException ex) {
            logger.error("Failed to get all " + NODE_PROFESSION + " nodes. " + ex.getMessage());
            throw new RuntimeException(ex);
        }

        while ((nit != null) && nit.hasNext()) {
            Node actual = (Node) nit.next();
            result.add(nodeToProfession(actual));
        }

        return result;
    }
}