package contact.test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.JettyMain;
import contact.entity.Contact;
import contact.resource.ContactResource;
import contact.service.ContactDao;

/**
 * Unit test for contact web service. This provide a quick testing solution for rapid development.
 * 
 * @author wutichai
 *
 */
public class WebServiceTest {
	private static Boolean serviceUrl;
	
	ContactResource cr;
	Contact contact1;
	Contact contact2;
	Contact contact3;

	
    @BeforeClass
    public static void doFirst( ) throws Exception {
        // Start the Jetty server.
        // Suppose this method returns the URL (with port) of the server
        serviceUrl = JettyMain.startServer( 8080 );
        assertTrue("Start server", serviceUrl == true);
    }
    
    @AfterClass
    public static void doLast( ) throws Exception {
        // stop the Jetty server after the last test
    	serviceUrl = JettyMain.stopServer();
    	assertTrue("Stop server", serviceUrl == true);
    }
    
    @Before
    public void setUp() {
    	contact1 = new Contact("contact1", "Joe Contact", "joe@microsoft.com");
		contact2 = new Contact("contact2", "Sally Contract", "sally@foo.com");
		contact3 = new Contact("contact3", "Foo Bar", "foo@barclub.com");
    }
    
    @Test
    public void testPost() {
    	
    	Contact con1 = cr.getContact(1);
    	assertTrue("con1 must be experiment John", con1.getId() == 1);
    }
}
