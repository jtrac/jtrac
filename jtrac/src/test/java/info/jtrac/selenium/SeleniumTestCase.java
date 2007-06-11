package info.jtrac.selenium;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openqa.selenium.server.SeleniumServer;
import watij.elements.HtmlElement;
import watij.elements.HtmlElements;
import watij.runtime.ie.IE;
import static watij.finders.SymbolFactory.*;

public abstract class SeleniumTestCase extends TestCase {
    
    public SeleniumTestCase(String name) {
        super(name);
    }
            
    private static ThreadLocalSelenium threadLocalSelenium;
    protected static Class clazz;
    protected Selenium selenium;
    
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
        public Selenium initialValue() {            
            Selenium s = new DefaultSelenium("localhost", SeleniumServer.getDefaultPort(), "*iexplore", "http://localhost:8080/jtrac");
            s.start();
            return s;
        }
    }
    
    @Override
    public final void setUp() {
        selenium = (Selenium) threadLocalSelenium.get();        
    }    
    
}
