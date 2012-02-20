package cz.muni.fi.pa165.hrs.server.system;

import cz.muni.fi.pa165.hrs.server.jcr.AllowedFileType;
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

import static cz.muni.fi.pa165.hrs.server.jcr.JcrType.*;
import static org.junit.Assert.*;

/**
 *
 * @author Andrej Kuročenko <kurochenko@mail.muni.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/test/spring/*.xml"})
public class UtilsTest {

    @Autowired
    @Qualifier("testJcrSession")
    private Session session;

    @Qualifier("testSimpleTextPdf")
    @Autowired
    private File pdfSimpleTextFile;

    @Qualifier("testSimpleTextOdt")
    @Autowired
    private File odtSimpleTextFile;

    @Before
    public void setUp() {
        Utils.destroyRepositorySkeleton(session);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPasswordHashSHA1NullPassword() {
        Utils.passwordHashSHA1(null);
    }

    @Test
    public void testPasswordHashSHA1() {
        assertEquals("2f97b55483e1597aa51a0a4f839f132a1260ee4e", Utils.passwordHashSHA1("h4x0r_p@ssw0rd"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateRepositorySkeletonNullSession() {
        Utils.createRepositorySkeleton(null);    
    }

    @Test
    public void testCreateRepositorySkeleton() throws RepositoryException {
        Utils.createRepositorySkeleton(session);

        Node root = session.getRootNode(); 
        assertTrue(root.hasNode(NODE_HRS.toString()));
        
        Node hrs = root.getNode(NODE_HRS.toString());
        assertTrue(hrs.hasNode(NODE_PROFESSIONS.toString()));
        assertTrue(hrs.hasNode(NODE_USERS.toString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDestroyRepositorySkeletonNullSession() {
        Utils.destroyRepositorySkeleton(null);
    }

    @Test
    public void testDestroyRepositorySkeletonNotExistent() {
        Utils.destroyRepositorySkeleton(session);
    }

    @Test
    public void testDestroyRepositorySkeleton() throws RepositoryException {
        Utils.createRepositorySkeleton(session);
        Utils.destroyRepositorySkeleton(session);

        assertFalse(session.getRootNode().hasNode(NODE_HRS.toString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractTextNullStream() {
        Utils.extractText(null, AllowedFileType.PDF);
        Utils.extractText(null, AllowedFileType.ODT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractTextNullFileType() throws FileNotFoundException {
        Utils.extractText(new FileInputStream(pdfSimpleTextFile), null);
    }

    @Test
    public void testExtractText() throws FileNotFoundException {
        assertEquals("Some text", Utils.extractText(new FileInputStream(pdfSimpleTextFile), AllowedFileType.PDF));
        assertEquals("Some text", Utils.extractText(new FileInputStream(odtSimpleTextFile), AllowedFileType.ODT));
    }
}
