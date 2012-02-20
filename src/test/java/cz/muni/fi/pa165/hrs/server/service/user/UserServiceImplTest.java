package cz.muni.fi.pa165.hrs.server.service.user;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.model.user.User;
import cz.muni.fi.pa165.hrs.model.user.UserDTO;
import cz.muni.fi.pa165.hrs.server.dao.profession.JackRabbitProfessionDAO;
import cz.muni.fi.pa165.hrs.server.dao.profession.ProfessionDAO;
import cz.muni.fi.pa165.hrs.server.dao.user.JackRabbitUserDAO;
import cz.muni.fi.pa165.hrs.server.dao.user.UserDAO;
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
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Test cases for {@code UserService} class
 *
 * @author Andrej Kuročenko <kurochenko@mail.muni.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/test/spring/*.xml"})
public class UserServiceImplTest {

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

    private UserDAO userDAO = new JackRabbitUserDAO();
    private ProfessionDAO professionDAO = new JackRabbitProfessionDAO();

    private User user;
    private Profession profession;
    
    @Before
    public void setUp() throws IOException {
        Utils.destroyRepositorySkeleton(session);
        Utils.createRepositorySkeleton(session);
        userDAO.setSession(session);
        professionDAO.setSession(session);
        userService.setUserDAO(userDAO);
        userService.setProfessionDAO(professionDAO);

        pdfInputStream = new FileInputStream(pdfFile);
        odtInputStream = new FileInputStream(odtFile);

        profession = new Profession();
        profession.setName("Boss");
        professionDAO.create(profession);

        user = new User();
        user.setName("User Name");
        user.setEmail("user@email.tld");
        user.setPassword("h4x0r_p@ssw0rd");
        user.setProfession(profession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetProfessionDAONull() {
        userService.setProfessionDAO(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUserDAONull() {
        userService.setUserDAO(null);
    }

    /*
       CREATE
    */

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNull() {
        userService.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNotNullId() {
        user.setUuid("falseUuid");
        userService.create(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNullEmail() {
        user.setEmail(null);
        userService.create(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNullPasswd() {
        user.setPassword(null);
        userService.create(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNullProfession() {
        user.setProfession(null);
        userService.create(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserExisting() {
        userService.create(user);
        
        user.setUuid(null);
        userService.create(user);
    }

    @Test
    public void testCreateUser() {
        userService.create(user);
        assertNotNull(user.getUuid());

        assertEquals(user, userService.findByUuid(user.getUuid()));
    }

    /*
        Update
     */

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserNull() {
        userService.edit(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserNullId() {
        userService.create(user);

        user.setUuid(null);
        userService.edit(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserNullEmail() {
        userService.create(user);
        user.setEmail(null);
        userService.edit(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserNullPasswd() {
        userService.create(user);
        user.setPassword(null);
        userService.edit(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserNullProfession() {
        userService.create(user);
        user.setProfession(null);
        userService.edit(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserNotExistent() {
        userService.create(user);
        user.setUuid("notexistentUuid");
        userService.edit(user);
    }

    @Test
    public void testUpdateUser() {
        userService.create(user);

        Profession p2 = new Profession();
        p2.setName("Lame");
        professionDAO.create(p2);

        user.setName("Updated Name");
        user.setEmail("updated@email.xml");
        user.setPassword("uPd4t3D_ps$wD");
        user.setProfession(p2);

        userService.edit(user);

        assertEquals(user, userService.findByUuid(user.getUuid()));
    }

    /*
        DELETE
     */

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserNullUuid() {
        userService.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserNotExistentUuid() {
        userService.remove("notExistentUuid");
    }

    @Test
    public void testDeleteUser() {
        userService.create(user);
        userService.remove(user.getUuid());

        assertTrue(userService.findAll().isEmpty());
        assertNull(userService.findByUuid(user.getUuid()));
    }

    /*
       Get by UUID
    */

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserByUuidNull() {
        userService.findByUuid(null);
    }

    @Test
    public void testGetUserByNotExistentUuid() {
        assertNull(userService.findByUuid("some-false-uuid"));
    }

    @Test
    public void testGetUserByUuid() {
        userService.create(user);
        assertEquals(user, userService.findByUuid(user.getUuid()));
    }

    /*
       Get by UUID
    */

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserByEmailNull() {
        userService.findByEmail(null);
    }

    @Test
    public void testGetUserByNotExistentEmail() {
        assertNull(userService.findByEmail("false@email.tld"));
    }

    @Test
    public void testGetUserByEmail() {
        userService.create(user);
        assertEquals(user, userService.findByEmail("user@email.tld"));
    }

    /*
       Get all users
    */

    @Test
    public void testGetAllUsersEmpty() {
        assertTrue(userService.findAll().isEmpty());
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
        userService.create(u2);
        userService.create(user);

        UserDTO userDTO = TransformationUtils.userToDTO(user);
        UserDTO u2DTO = TransformationUtils.userToDTO(u2);

        assertEquals(2, userService.findAll().size());
        assertTrue(userService.findAll().contains(userDTO));
        assertTrue(userService.findAll().contains(u2DTO));
    }

    /*
       Get by profession
    */

    @Test(expected = IllegalArgumentException.class)
    public void testGetUsersByProfessionNull() {
        userService.findByProfession(null);
    }

    @Test
    public void testGetUsersByProfessionEmpty() {
        assertTrue(userService.findByProfession(profession).isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUsersByProfessionNotExistent() {
        userService.create(user);

        Profession p2 = new Profession();
        p2.setName("Lame");
        professionDAO.create(p2);
        professionDAO.remove(p2.getUuid());


        userService.findByProfession(p2);
    }

    @Test
    public void testGetUsersByProfession() {
        userService.create(user);
        UserDTO userDTO = TransformationUtils.userToDTO(user);

        assertEquals(1, userService.findByProfession(profession).size());
        assertTrue(userService.findByProfession(profession).contains(userDTO));

        Profession p2 = new Profession();
        p2.setName("Lame");
        professionDAO.create(p2);

        User u2 = new User();
        u2.setName("Second user");
        u2.setEmail("second@user.tld");
        u2.setPassword("s3c0nD_p@ssw0rd");
        u2.setProfession(p2);
        userService.create(u2);

        UserDTO u2DTO = TransformationUtils.userToDTO(u2);

        assertEquals(1, userService.findByProfession(p2).size());
        assertTrue(userService.findByProfession(p2).contains(u2DTO));

        u2.setProfession(profession);
        userService.edit(u2);
        u2DTO = TransformationUtils.userToDTO(u2);

        assertEquals(2, userService.findByProfession(profession).size());
        assertTrue(userService.findByProfession(profession).contains(userDTO));
        assertTrue(userService.findByProfession(profession).contains(u2DTO));
    }

    /*
       Add user CV
    */
    @Test(expected = IllegalArgumentException.class)
    public void testAddUsersCVNullUuid() {
        userService.addCv(null, pdfInputStream, AllowedFileType.PDF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUsersCVNullFileStream() {
        userService.create(user);
        userService.addCv(user.getUuid(), null, AllowedFileType.PDF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUsersCVNullFileType() {
        userService.create(user);
        userService.addCv(user.getUuid(), pdfInputStream, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUsersCVNotExistentUser() {
        userService.create(user);
        userService.remove(user.getUuid());
        userService.addCv(user.getUuid(), pdfInputStream, AllowedFileType.PDF);
    }

    @Test
    public void testAddUsersCvPdf() {
        userService.create(user);
        userService.addCv(user.getUuid(), pdfInputStream, AllowedFileType.PDF);

        assertNotNull(userDAO.getCv(user.getUuid(), AllowedFileType.PDF));
    }

    @Test
    public void testAddUsersCvOdt() {
        userService.create(user);
        userService.addCv(user.getUuid(), odtInputStream, AllowedFileType.ODT);

        assertNotNull(userDAO.getCv(user.getUuid(), AllowedFileType.ODT));
    }

    /*
        Get user CV
     */

    @Test(expected = IllegalArgumentException.class)
    public void testGetUsersCVNullUuid() {
        userService.getCv(null, AllowedFileType.PDF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUsersCVNullFileType() {
        userService.create(user);
        userService.getCv(user.getUuid(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUsersCVNotExistentUser() {
        userService.create(user);
        userService.remove(user.getUuid());
        userService.getCv(user.getUuid(), AllowedFileType.PDF);
    }

    @Test
    public void testGetUsersCVEmpty() {
        userService.create(user);
        assertNull(userService.getCv(user.getUuid(), AllowedFileType.PDF));
        assertNull(userService.getCv(user.getUuid(), AllowedFileType.ODT));
    }

    /*
        Fulltext search
     */

    @Test(expected = IllegalArgumentException.class)
    public void testFindUsersFulltextNullQuery() {
        userService.findFulltext(null);
    }

    @Test
    public void testFindUsersFulltextPdf() throws InterruptedException {
        userService.create(user);
        userService.addCv(user.getUuid(), pdfInputStream, AllowedFileType.PDF);

        Thread.sleep(500); // wait for indexing

        assertTrue(userService.findFulltext("lorem*").contains(TransformationUtils.userToDTO(user)));
        assertFalse(userService.findFulltext("somenotexistenttext*").contains(TransformationUtils.userToDTO(user)));
    }

    @Test
    public void testFindUsersFulltextOdt() throws InterruptedException {
        userService.create(user);
        userService.addCv(user.getUuid(), odtInputStream, AllowedFileType.ODT);

        Thread.sleep(500); // wait for indexing

        assertTrue(userService.findFulltext("lorem*").contains(TransformationUtils.userToDTO(user)));
        assertFalse(userService.findFulltext("somenotexistenttext*").contains(TransformationUtils.userToDTO(user)));
    }
    
}
