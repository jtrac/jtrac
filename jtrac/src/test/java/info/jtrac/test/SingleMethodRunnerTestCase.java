package info.jtrac.test;

import info.jtrac.JtracTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * development convenience, run only a single method in test case
 * since NetBeans does not support this at the moment
 */
public class SingleMethodRunnerTestCase extends TestCase {    
    
    public static Test suite() throws Exception { 
        TestSuite s = new TestSuite();
        String name = System.getProperty("method.name");        
        Test test = (Test) new JtracTest(name); 
        s.addTest(test);
        return s;
    }    

}
