package cz.muni.fi.pa165.hrs.server.jcr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author Andrej Kuročenko <kurochenko@mail.muni.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/test/spring/*.xml"})
public class JcrSessionFactoryTest {

    @Autowired
    private Repository repository;

    @Autowired
    private SimpleCredentials credentials;

    @Autowired
    @Qualifier("cndFile")
    private File cndFile;

    @Test(expected = IllegalArgumentException.class)
    public void testFactoryConstructorNullRepository() throws FileNotFoundException {
        new JcrSessionFactory(null, credentials, new FileInputStream(cndFile));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFactoryConstructorNullCredentials() throws FileNotFoundException {
        new JcrSessionFactory(repository, null, new FileInputStream(cndFile));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFactoryConstructorNullStream() throws FileNotFoundException {
        new JcrSessionFactory(repository, credentials, null);
    }

    @Test
    public void testFactoryConstructor() throws FileNotFoundException {
        new JcrSessionFactory(repository, credentials, new FileInputStream(cndFile));
    }
}
