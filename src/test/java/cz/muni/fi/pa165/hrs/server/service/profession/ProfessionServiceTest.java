package cz.muni.fi.pa165.hrs.server.service.profession;

import cz.muni.fi.pa165.hrs.model.profession.Profession;
import cz.muni.fi.pa165.hrs.server.dao.profession.JackRabbitProfessionDAO;
import cz.muni.fi.pa165.hrs.server.dao.profession.ProfessionDAO;
import cz.muni.fi.pa165.hrs.server.system.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jcr.Session;

import static org.junit.Assert.*;

/**
 * Test cases for {@code ProfessionService} class
 *
 * @author Andrej Kuročenko <kurochenko@mail.muni.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/test/spring/*.xml"})
public class ProfessionServiceTest {

    @Autowired
    private Session session;

    private ProfessionService professionService = new ProfessionServiceImpl();
    ProfessionDAO professionDAO = new JackRabbitProfessionDAO();

    
    @Before
    public void setUp() {
        Utils.destroyRepositorySkeleton(session);
        Utils.createRepositorySkeleton(session);
        professionDAO.setSession(session);
        professionService.setProfessionDAO(professionDAO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetProfessionDAONull() {
        professionService.setProfessionDAO(null);
    }

    /*
       CREATE
    */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateProfessionNull() {
        professionService.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProfessionNotNullId() {
        Profession profession = new Profession();
        profession.setUuid("someuuid");
        profession.setName("somename");
        professionService.create(profession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProfessionNullName() {
        Profession profession = new Profession();
        profession.setUuid(null);
        profession.setName(null);
        professionService.create(profession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProfessionExistingName() {
        Profession profession = new Profession();
        profession.setUuid(null);
        profession.setName("somename");
        professionService.create(profession);

        profession.setUuid(null);
        professionService.create(profession);

    }

    @Test
    public void testCreateProfession() {
        Profession profession = new Profession();
        profession.setName("IT");
        assertNull(profession.getUuid());

        professionService.create(profession);

        assertNotNull(profession.getUuid());
        assertEquals(profession, professionService.findByUuid(profession.getUuid()));
    }

    /*
        UPDATE
     */

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateProfessionNull() {
        professionService.edit(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateProfessionNullId() {
        Profession profession = new Profession();
        profession.setName("somename");
        professionService.create(profession);

        profession.setUuid(null);
        profession.setName("updatedName");
        professionService.edit(profession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateProfessionNullName() {
        Profession profession = new Profession();
        profession.setName("somename");
        professionService.create(profession);

        profession.setName(null);
        professionService.edit(profession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateProfessionNotExisting() {
        Profession profession = new Profession();
        profession.setUuid("somefalseuuid");
        profession.setName("someuuid");
        professionService.edit(profession);
    }

    @Test
    public void testUpdateProfession() {
        Profession profession = new Profession();
        profession.setName("IT");
        professionService.create(profession);
        String oldUuid = profession.getUuid();

        profession.setName("New name");
        professionService.edit(profession);

        assertEquals(oldUuid, profession.getUuid());
        assertEquals("New name", profession.getName());
        assertEquals(professionService.findByUuid(profession.getUuid()), profession);
    }

    /*
       Delete
    */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteProfessionNullUuid() {
        professionService.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteProfessionNotExistentUuid() {
        professionService.remove("notexistentuid");
    }

    @Test
    public void testDeleteProfession() {
        Profession profession = new Profession();
        profession.setName("To remove");
        professionService.create(profession);
        System.out.println(profession.getUuid());
        professionService.remove(profession.getUuid());
        assertNull(professionService.findByUuid(profession.getUuid()));
    }

    /*
       Get by UUID
    */
    @Test(expected = IllegalArgumentException.class)
    public void testGetProfessionByUuidNull() {
        professionService.findByUuid(null);
    }

    @Test
    public void testGetProfessionByUuidNotExistent() {
        assertNull(professionService.findByUuid("not-existent-uuid"));
    }

    @Test
    public void testGetProfessionByUuid() {
        Profession profession = new Profession();
        profession.setName("someProfession");
        professionService.create(profession);

        assertEquals(profession, professionService.findByUuid(profession.getUuid()));
    }

    /*
       Get by name
    */
    @Test(expected = IllegalArgumentException.class)
    public void testGetProfessionByNameNull() {
        professionService.findByName(null);
    }

    @Test
    public void testGetProfessionByNameNotExistent() {
        assertNull(professionService.findByName("not-existent-name"));
    }

    @Test
    public void testGetProfessionByName() {
        Profession profession = new Profession();
        profession.setName("someProfession");
        professionService.create(profession);

        assertEquals(profession, professionService.findByName("someProfession"));
    }

    /*
        Get all professions
     */
    @Test
    public void testGetAllProfessionsEmpty() {
        assertTrue(professionService.findAll().isEmpty());
    }

    @Test
    public void testGetAllProfessions() {
        Profession profession = new Profession();
        profession.setName("someProfession");
        professionService.create(profession);

        assertEquals(1, professionService.findAll().size());
        assertTrue(professionService.findAll().contains(profession));

        Profession profession2 = new Profession();
        profession2.setName("someProfessionSecond");
        professionService.create(profession2);

        assertEquals(2, professionService.findAll().size());
        assertTrue(professionService.findAll().contains(profession));
        assertTrue(professionService.findAll().contains(profession2));
    }

}
