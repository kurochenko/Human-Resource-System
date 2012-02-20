package cz.muni.fi.pa165.hrs.server.dao.user;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.model.user.User;
import cz.muni.fi.pa165.hrs.model.user.UserDTO;
import cz.muni.fi.pa165.hrs.server.dao.profession.JackRabbitProfessionDAO;
import cz.muni.fi.pa165.hrs.server.dao.profession.ProfessionDAO;
import cz.muni.fi.pa165.hrs.server.jcr.AllowedFileType;
import cz.muni.fi.pa165.hrs.server.jcr.TransformationUtils;
import cz.muni.fi.pa165.hrs.server.system.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jcr.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 *
 * @author Andrej Kuročenko <kurochenko@mail.muni.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/test/spring/*.xml"})
public class UserDAOTest {

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

        pdfInputStream = new FileInputStream(pdfFile);
        odtInputStream = new FileInputStream(odtFile);

        profession = new Profession();
        profession.setName("Boss");
        professionDAO.create(profession);
        
        user = new User();
        user.setName("User Name");
        user.setEmail("user@email.tld");
        user.setPassword("h4x0r_p@ssw0rd");
        user.setPrivileged(true);
        user.setProfession(profession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSessionNull() {
        userDAO.setSession(null);
    }
    
    /*
        CREATE
     */

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNull() {
        userDAO.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNotNullId() {
        user.setUuid("falseUuid");
        userDAO.create(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNullEmail() {
        user.setEmail(null);
        userDAO.create(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNullPasswd() {
        user.setPassword(null);
        userDAO.create(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNullProfession() {
        user.setProfession(null);
        userDAO.create(user);
    }

    @Test
    public void testCreateUser() {
        userDAO.create(user);
        assertNotNull(user.getUuid());

        assertEquals(user, userDAO.findByUuid(user.getUuid()));
        assertEquals("User Name", userDAO.findByUuid(user.getUuid()).getName());
        assertEquals("user@email.tld", userDAO.findByUuid(user.getUuid()).getEmail());
        assertEquals("h4x0r_p@ssw0rd", userDAO.findByUuid(user.getUuid()).getPassword());
        assertTrue(userDAO.findByUuid(user.getUuid()).isPrivileged());
        assertEquals(profession, userDAO.findByUuid(user.getUuid()).getProfession());
    }
    
    /*
        UPDATE
     */
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserNull() {
        userDAO.edit(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserNullId() {
        userDAO.create(user);
        
        user.setUuid(null);
        userDAO.edit(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserNullEmail() {
        userDAO.create(user);
        user.setEmail(null);
        userDAO.edit(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserNullPasswd() {
        userDAO.create(user);
        user.setPassword(null);
        userDAO.edit(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserNullProfession() {
        userDAO.create(user);
        user.setProfession(null);
        userDAO.edit(user);
    }

    @Test
    public void testUpdateUser() {
        userDAO.create(user);
        
        Profession p2 = new Profession();
        p2.setName("Lame");
        professionDAO.create(p2);
        
        user.setName("Updated Name");
        user.setEmail("updated@email.xml");
        user.setPassword("uPd4t3D_ps$wD");
        user.setPrivileged(false);
        user.setProfession(p2);
        
        userDAO.edit(user);

        assertEquals(user, userDAO.findByUuid(user.getUuid()));
        assertEquals(user, userDAO.findByUuid(user.getUuid()));
        assertEquals("Updated Name", userDAO.findByUuid(user.getUuid()).getName());
        assertEquals("updated@email.xml", userDAO.findByUuid(user.getUuid()).getEmail());
        assertEquals("uPd4t3D_ps$wD", userDAO.findByUuid(user.getUuid()).getPassword());
        assertFalse(userDAO.findByUuid(user.getUuid()).isPrivileged());
        assertEquals(p2, userDAO.findByUuid(user.getUuid()).getProfession());
    }
    
    /*
        DELETE
     */
    
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserNullUuid() {
        userDAO.remove(null);
    }

    @Test
    public void testDeleteUser() {
        userDAO.create(user);
        userDAO.remove(user.getUuid());
        
        assertTrue(userDAO.findAll().isEmpty());
        assertNull(userDAO.findByUuid(user.getUuid()));
    }
    
    /*
        Get by UUID
     */

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserByUuidNull() {
        userDAO.findByUuid(null);
    }

    @Test
    public void testGetUserByNotExistentUuid() {
        assertNull(userDAO.findByUuid("some-false-uuid"));
    }

    @Test
    public void testGetUserByUuid() {
        userDAO.create(user);
        assertEquals(user, userDAO.findByUuid(user.getUuid()));
    }
    
    /*
        Get by email
     */

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserByEmailNull() {
        userDAO.findByEmail(null);
    }

    @Test
    public void testGetUserByNotExistentEmail() {
        assertNull(userDAO.findByEmail("false@email.tld"));
    }

    @Test
    public void testGetUserByEmail() {
        userDAO.create(user);
        assertEquals(user, userDAO.findByEmail("user@email.tld"));
    }
    
    /*
        Get all users
     */

    @Test
    public void testGetAllUsersEmpty() {
        assertTrue(userDAO.findAll().isEmpty());
    }

    @Test
    public void testGetAllUsers() {
        Profession p2 = new Profession();
        p2.setName("Lame");
        professionDAO.create(p2);
        
        User u2 = new User();
        u2.setName("Second user");
        u2.setEmail("second@user.tld");
        u2.setPassword("s3c0nD_p@ssw0rd");
        u2.setProfession(p2);
        userDAO.create(u2);
        userDAO.create(user);

        UserDTO userDTO = TransformationUtils.userToDTO(user);
        UserDTO u2DTO = TransformationUtils.userToDTO(u2);
        
        assertEquals(2, userDAO.findAll().size());
        assertTrue(userDAO.findAll().contains(userDTO));
        assertTrue(userDAO.findAll().contains(u2DTO));
    }
    
    /*
        Get by profession
     */

    @Test(expected = IllegalArgumentException.class)
    public void testGetUsersByProfessionNull() {
        userDAO.findByProfession(null);
    }

    @Test
    public void testGetUsersByProfessionEmpty() {
        assertTrue(userDAO.findByProfession(profession).isEmpty());
    }

    @Test
    public void testGetUsersByProfession() {
        userDAO.create(user);
        UserDTO userDTO = TransformationUtils.userToDTO(user);
        
        assertEquals(1, userDAO.findByProfession(profession).size());
        assertTrue(userDAO.findByProfession(profession).contains(userDTO));

        Profession p2 = new Profession();
        p2.setName("Lame");
        professionDAO.create(p2);
        
        User u2 = new User();
        u2.setName("Second user");
        u2.setEmail("second@user.tld");
        u2.setPassword("s3c0nD_p@ssw0rd");
        u2.setProfession(p2);
        userDAO.create(u2);
        
        UserDTO u2DTO = TransformationUtils.userToDTO(u2);

        assertEquals(1, userDAO.findByProfession(p2).size());
        assertTrue(userDAO.findByProfession(p2).contains(u2DTO));
        
        u2.setProfession(profession);
        userDAO.edit(u2);
        u2DTO = TransformationUtils.userToDTO(u2);

        assertEquals(2, userDAO.findByProfession(profession).size());
        assertTrue(userDAO.findByProfession(profession).contains(userDTO));
        assertTrue(userDAO.findByProfession(profession).contains(u2DTO));
    }

    /*
        Add user CV
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddUsersCVNullUuid() {
        userDAO.addCv(null, pdfInputStream, AllowedFileType.PDF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUsersCVNullFileStream() {
        userDAO.create(user);
        userDAO.addCv(user.getUuid(), null, AllowedFileType.PDF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUsersCVNullFileType() {
        userDAO.create(user);
        userDAO.addCv(user.getUuid(), pdfInputStream, null);
    }

    @Test
    public void testAddUsersCvPdf() {
        userDAO.create(user);
        userDAO.addCv(user.getUuid(), pdfInputStream, AllowedFileType.PDF);

        assertNotNull(userDAO.getCv(user.getUuid(), AllowedFileType.PDF));
    }
    
    @Test
    public void testAddUsersCvOdt() {
        userDAO.create(user);
        userDAO.addCv(user.getUuid(), odtInputStream, AllowedFileType.ODT);

        assertNotNull(userDAO.getCv(user.getUuid(), AllowedFileType.ODT));
    }

    /*
        Get user CV
     */

    @Test(expected = IllegalArgumentException.class)
    public void testGetUsersCVNullUuid() {
        userDAO.getCv(null, AllowedFileType.PDF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUsersCVNullFileType() {
        userDAO.create(user);
        userDAO.getCv(user.getUuid(), null);
    }

    @Test
    public void testGetUsersCVEmpty() {
        userDAO.create(user);
        assertNull(userDAO.getCv(user.getUuid(), AllowedFileType.PDF));
        assertNull(userDAO.getCv(user.getUuid(), AllowedFileType.ODT));
    }

    /*
        Fulltext search
     */

    @Test(expected = IllegalArgumentException.class)
    public void testFindUsersFulltextNullQuery() {
        userDAO.findFullText(null);
    }

    @Test
    public void testFindUsersFulltextPdf() throws InterruptedException {
        userDAO.create(user);
        userDAO.addCv(user.getUuid(), pdfInputStream, AllowedFileType.PDF);

        Thread.sleep(500); // wait for indexing

        assertTrue(userDAO.findFullText("lorem*").contains(TransformationUtils.userToDTO(user)));
        assertFalse(userDAO.findFullText("somenotexistenttext*").contains(TransformationUtils.userToDTO(user)));
    }

    @Test
    public void testFindUsersFulltextOdt() throws InterruptedException {
        userDAO.create(user);
        userDAO.addCv(user.getUuid(), odtInputStream, AllowedFileType.ODT);

        Thread.sleep(500); // wait for indexing

        assertTrue(userDAO.findFullText("lorem*").contains(TransformationUtils.userToDTO(user)));
        assertFalse(userDAO.findFullText("somenotexistenttext*").contains(TransformationUtils.userToDTO(user)));
    }
}
