package info.jtrac.watij;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import watij.elements.HtmlElement;
import watij.elements.HtmlElements;
import watij.runtime.ie.IE;
import static watij.finders.SymbolFactory.*;

public abstract class WatijTestCase extends TestCase {
    
    public WatijTestCase(String name) {
        super(name);
    }
            
    private static ThreadLocalIE threadLocalIE;
    protected static Class clazz;
    protected IE ie;
    
    public static Test suite() throws Exception {        
        threadLocalIE = new ThreadLocalIE();
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
    
    private static class ThreadLocalIE extends ThreadLocal {
        @Override
        public IE initialValue() {
            return new IE();
        }
    }
    
    @Override
    public final void setUp() {
        ie = (IE) threadLocalIE.get();
    }    
    
    protected void click(String id) throws Exception {
        ie.htmlElement(xpath, "//*[@id='" + id + "']").click();
    }
    
    protected void clickLink(String id) throws Exception {
        HtmlElements elements =  ie.htmlElements(xpath, "//A");
        for(int i = 0; i < elements.length(); i++) {
            HtmlElement element = elements.get(i);
            if(element.html().contains("wicket:id=\"" + id + "\"")) {   
                element.click();
                break;
            }
        }
    }
    
}
