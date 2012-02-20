package cz.muni.fi.pa165.hrs.server.jcr;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.model.user.User;
import cz.muni.fi.pa165.hrs.model.user.UserDTO;
import cz.muni.fi.pa165.hrs.server.dao.profession.JackRabbitProfessionDAO;
import cz.muni.fi.pa165.hrs.server.dao.profession.ProfessionDAO;
import cz.muni.fi.pa165.hrs.server.dao.user.JackRabbitUserDAO;
import cz.muni.fi.pa165.hrs.server.dao.user.UserDAO;
import cz.muni.fi.pa165.hrs.server.service.profession.ProfessionService;
import cz.muni.fi.pa165.hrs.server.service.profession.ProfessionServiceImpl;
import cz.muni.fi.pa165.hrs.server.service.user.UserService;
import cz.muni.fi.pa165.hrs.server.service.user.UserServiceImpl;
import cz.muni.fi.pa165.hrs.server.system.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static cz.muni.fi.pa165.hrs.server.jcr.JcrType.*;
import static org.junit.Assert.*;

/**
 * 
 * @author Andrej Kuročenko <kurochenko@mail.muni.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/test/spring/*.xml"})
public class TransformationUtilsTest {

    @Autowired
    @Qualifier("testJcrSession")
    private Session session;

    @Qualifier("testCvPdf")
    @Autowired
    private File pdfFile;

    @Qualifier("testCvOdt")
    @Autowired
    private File odtFile;

    private InputStream pdfInputStream;
    private InputStream odtInputStream;

    private UserService userService = new UserServiceImpl();
    private ProfessionService professionService = new ProfessionServiceImpl();

    private UserDAO userDAO = new JackRabbitUserDAO();
    private ProfessionDAO professionDAO = new JackRabbitProfessionDAO();
    
    private User user;
    private Profession profession;

    @Before
    public void setUp() throws FileNotFoundException {
        Utils.destroyRepositorySkeleton(session);
        Utils.createRepositorySkeleton(session);
        userDAO.setSession(session);
        professionDAO.setSession(session);
        userService.setUserDAO(userDAO);
        userService.setProfessionDAO(professionDAO);
        professionService.setProfessionDAO(professionDAO);

        pdfInputStream = new FileInputStream(pdfFile);
        odtInputStream = new FileInputStream(odtFile);

        profession = new Profession();
        profession.setName("Boss");

        user = new User();
        user.setName("User Name");
        user.setEmail("user@email.tld");
        user.setPassword("h4x0r_p@ssw0rd");
        user.setProfession(profession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUserToUserDTONull() {
        TransformationUtils.userToDTO(null);
    }

    @Test
    public void testUserToUserDTONullId() {
        UserDTO result = TransformationUtils.userToDTO(user);
        
        assertEquals("User Name", result.getName());
        assertNull(result.getProfession().getUuid());
        assertEquals("Boss", result.getProfession().getName());
        assertEquals(profession, result.getProfession());
        assertNull(result.getUuid());
    }

    @Test
    public void testUserToUserDTO() {
        profession.setUuid("some-profession-uuid");
        user.setUuid("some-user-uuid");

        UserDTO result = TransformationUtils.userToDTO(user);

        assertEquals("some-user-uuid", result.getUuid());
        assertEquals("User Name", result.getName());
        assertEquals("some-profession-uuid", result.getProfession().getUuid());
        assertEquals("Boss", result.getProfession().getName());
        assertEquals(profession, result.getProfession());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertNodeToProfessionNull() {
        TransformationUtils.nodeToProfession(null);
    }

    @Test
    public void testConvertNodeToProfession() throws RepositoryException {
        professionService.create(profession);
        
        Node professionNode = session.getNodeByIdentifier(profession.getUuid());
        Profession transformed = TransformationUtils.nodeToProfession(professionNode);

        assertEquals(professionNode.getIdentifier(), transformed.getUuid());
        assertEquals(professionNode.getProperty(PROP_NAME.toString()).getString(), transformed.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertNodeToUserNull() {
        TransformationUtils.nodeToUser(null);
    }

    @Test
    public void testConvertNodeToUser() throws RepositoryException {
        professionService.create(profession);
        userService.create(user);

        Node userNode = session.getNodeByIdentifier(user.getUuid());
        User transformed = TransformationUtils.nodeToUser(userNode);

        assertEquals(userNode.getIdentifier(), transformed.getUuid());
        assertEquals(userNode.getProperty(PROP_PASSWORD.toString()).getString(), transformed.getPassword());
        assertEquals(userNode.getProperty(PROP_EMAIL.toString()).getString(), transformed.getEmail());
        assertEquals(userNode.getProperty(PROP_NAME.toString()).getString(), transformed.getName());
        assertEquals(TransformationUtils.nodeToProfession(userNode.getProperty(PROP_PROFESSION.toString()).getNode()), transformed.getProfession());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertNodeToUserDTONull() {
        TransformationUtils.nodeToUserDTO(null);
    }

    @Test
    public void testConvertNodeToUserDTO() throws RepositoryException {
        professionService.create(profession);
        userService.create(user);

        Node userNode = session.getNodeByIdentifier(user.getUuid());
        UserDTO transformed = TransformationUtils.nodeToUserDTO(userNode);

        assertEquals(userNode.getIdentifier(), transformed.getUuid());
        assertEquals(userNode.getProperty(PROP_NAME.toString()).getString(), transformed.getName());
        assertEquals(TransformationUtils.nodeToProfession(userNode.getProperty(PROP_PROFESSION.toString()).getNode()), transformed.getProfession());
    }

    @Test(expected = IllegalArgumentException.class)
    public void textConvertFileStreamToNodeNullUserNode() {
        TransformationUtils.convertFileStreamToNode(null,pdfInputStream,AllowedFileType.PDF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void textConvertFileStreamToNodeNullStream() throws RepositoryException {
        professionService.create(profession);
        userService.create(user);
        TransformationUtils.convertFileStreamToNode(session.getNodeByIdentifier(user.getUuid()),null,AllowedFileType.PDF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void textConvertFileStreamToNodeNullFileType() throws RepositoryException {
        professionService.create(profession);
        userService.create(user);
        TransformationUtils.convertFileStreamToNode(session.getNodeByIdentifier(user.getUuid()),pdfInputStream,null);
    }

    @Test
    public void textConvertFileStreamToNodePdf() throws RepositoryException, FileNotFoundException {
        professionService.create(profession);
        userService.create(user);

        TransformationUtils.convertFileStreamToNode(session.getNodeByIdentifier(user.getUuid()),pdfInputStream,AllowedFileType.PDF);

        InputStream is = new FileInputStream(pdfFile);
        Node userCvNode =  session.getNodeByIdentifier(user.getUuid()).getNode(AllowedFileType.PDF.getNodeType().toString());

        assertTrue(
                userCvNode.getProperty(PROP_CONTENT.toString())
                .getString()
                .equals(Utils.extractText(is, AllowedFileType.PDF))
        );

        assertTrue(userCvNode.getNode("jcr:content").getProperty("jcr:mimeType").getString().equals(AllowedFileType.PDF.getMimeType()));
        assertNotNull(userCvNode.getNode("jcr:content").getProperty("jcr:data").getBinary().getStream());

    }

    @Test
    public void textConvertFileStreamToNodeOdf() throws RepositoryException, FileNotFoundException {
        professionService.create(profession);
        userService.create(user);

        TransformationUtils.convertFileStreamToNode(session.getNodeByIdentifier(user.getUuid()),odtInputStream,AllowedFileType.ODT);

        InputStream is = new FileInputStream(odtFile);
        Node userCvNode =  session.getNodeByIdentifier(user.getUuid()).getNode(AllowedFileType.ODT.getNodeType().toString());

        assertTrue(
                userCvNode.getProperty(PROP_CONTENT.toString())
                        .getString()
                        .equals(Utils.extractText(is, AllowedFileType.ODT))
        );

        assertTrue(userCvNode.getNode("jcr:content").getProperty("jcr:mimeType").getString().equals(AllowedFileType.ODT.getMimeType()));
        assertNotNull(userCvNode.getNode("jcr:content").getProperty("jcr:data").getBinary().getStream());

    }
}
