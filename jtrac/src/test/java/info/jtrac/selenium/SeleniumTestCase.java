package info.jtrac.selenium;

import com.thoughtworks.selenium.DefaultSelenium;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openqa.selenium.server.SeleniumServer;

/** 
 * base class for Selenium test scripts that hack JUnit so as to be
 * able to run test methods in the order in which they appear
 * in the source file. If the class name is "AllTest.java", following
 * boiler-plate must be included (I said this was a hack :)
 *
 *  static {
 *      clazz = AllTest.class;
 *  }
 *  
 *  public AllTest(String name) {
 *      super(name);
 *  }
 *
 */
public abstract class SeleniumTestCase extends TestCase {
    
    public SeleniumTestCase(String name) {
        super(name);
    }
  
    private static ThreadLocalSelenium threadLocalSelenium;
    protected static Class clazz;
    protected JtracSelenium selenium;    
    protected static SeleniumServer server;
    
    public static Test suite() throws Exception {        
        threadLocalSelenium = new ThreadLocalSelenium();
        Constructor constructor = clazz.getDeclaredConstructors()[0];
        Method[] methods = clazz.getMethods();
        TestSuite s = new TestSuite();
        for(Method m : methods) {
            if (m.getName().startsWith("test")) {
                Test test = (Test) constructor.newInstance(new Object[] { m.getName() });
                s.addTest(test);
            }
        }
        return s;
    }
    
    private static class ThreadLocalSelenium extends ThreadLocal {
        @Override
        public JtracSelenium initialValue() {
            try {
                server = new SeleniumServer();
                server.start();
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
            JtracSelenium s = new JtracSelenium("localhost", SeleniumServer.getDefaultPort(), "*iexplore", "http://localhost:8080/jtrac");
            s.start();
            return s;
        }
    }
    
    @Override
    public final void setUp() {
        selenium = (JtracSelenium) threadLocalSelenium.get();        
    }    
    
    protected void assertTextPresent(String text) {
        assertTrue(selenium.isTextPresent(text));
    }    
    
    protected void stopSelenium() {
        selenium.stop();
        server.stop();
    }
    
    /**
     * custom extension of Selenium to automatically wait for page to load
     * after clicking a button or link
     */
    public static class JtracSelenium extends DefaultSelenium {
        
        public JtracSelenium(String host, int port, String browser, String url) {
            super(host, port, browser, url);
        }
        
        public void clickAndWait(String locator) {
            click(locator);
            waitForPageToLoad("30000");
        }        
        
    }
    
}
