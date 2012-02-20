package cz.muni.fi.pa165.hrs.server.dao.profession;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.server.system.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jcr.Session;

import static org.junit.Assert.*;

/**
 * Test cases for {@code ProfessionDAO} methods
 *
 * @author Andrej Kuročenko <kurochenko@mail.muni.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/test/spring/*.xml"})
public class ProfessionDAOTest {

    @Autowired
    @Qualifier("testJcrSession")
    private Session session;

    private ProfessionDAO professionDAO = new JackRabbitProfessionDAO();
    Profession profession;

    @Before
    public void setUp() {
        // Force rollback changes in repository
        Utils.destroyRepositorySkeleton(session);
        Utils.createRepositorySkeleton(session);
        professionDAO.setSession(session);

        profession = new Profession();
        profession.setName("IT");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSessionNull() {
        professionDAO.setSession(null);
    }

    /*
        CREATE
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateProfessionNull() {
        professionDAO.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProfessionNotNullId() {
        profession.setUuid("someuuid");
        professionDAO.create(profession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProfessionNullName() {
        profession.setName(null);
        professionDAO.create(profession);
    }

    @Test
    public void testCreateProfession() {
        assertNull(profession.getUuid());

        professionDAO.create(profession);

        assertNotNull(profession.getUuid());
        assertEquals(profession, professionDAO.findByUuid(profession.getUuid()));
        assertEquals("IT", professionDAO.findByUuid(profession.getUuid()).getName());
    }

    /*
        UPDATE
     */

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateProfessionNull() {
        professionDAO.edit(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateProfessionNullId() {
        professionDAO.create(profession);

        profession.setUuid(null);
        profession.setName("updatedName");
        professionDAO.edit(profession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateProfessionNullName() {
        professionDAO.create(profession);

        profession.setName(null);
        professionDAO.edit(profession);
    }

    @Test
    public void testUpdateProfession() {
        professionDAO.create(profession);
        String oldUuid = profession.getUuid();

        profession.setName("New name");
        professionDAO.edit(profession);

        assertEquals(oldUuid, profession.getUuid());
        assertEquals("New name", profession.getName());
        assertEquals(professionDAO.findByUuid(profession.getUuid()), profession);
    }
    
    /*
        Delete
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteProfessionNullUuid() {
        professionDAO.remove(null);
    }

    @Test
    public void testDeleteProfession() {
        professionDAO.create(profession);
        professionDAO.remove(profession.getUuid());
        assertNull(professionDAO.findByUuid(profession.getUuid()));
    }

    /*
       Get by UUID
    */
    @Test(expected = IllegalArgumentException.class)
    public void testGetProfessionByUuidNull() {
        professionDAO.findByUuid(null);
    }

    @Test
    public void testGetProfessionByUuidNotExistent() {
        assertNull(professionDAO.findByUuid("not-existent-uuid"));
    }

    @Test
    public void testGetProfessionByUuid() {
        professionDAO.create(profession);
        assertEquals(profession, professionDAO.findByUuid(profession.getUuid()));
    }

    /*
       Get by name
    */
    @Test(expected = IllegalArgumentException.class)
    public void testGetProfessionByNameNull() {
        professionDAO.findByName(null);
    }

    @Test
    public void testGetProfessionByNameNotExistent() {
        assertNull(professionDAO.findByName("not-existent-name"));
    }

    @Test
    public void testGetProfessionByName() {
        professionDAO.create(profession);
        assertEquals(profession, professionDAO.findByName("IT"));
    }

    /*
        Get all professions
     */
    @Test
    public void testGetAllProfessionsEmpty() {
        assertTrue(professionDAO.findAll().isEmpty());
    }

    @Test
    public void testGetAllProfessions() {
        professionDAO.create(profession);

        assertEquals(1, professionDAO.findAll().size());
        assertTrue(professionDAO.findAll().contains(profession));

        Profession profession2 = new Profession();
        profession2.setName("someProfessionSecond");
        professionDAO.create(profession2);

        assertEquals(2, professionDAO.findAll().size());
        assertTrue(professionDAO.findAll().contains(profession));
        assertTrue(professionDAO.findAll().contains(profession2));
    }
}
