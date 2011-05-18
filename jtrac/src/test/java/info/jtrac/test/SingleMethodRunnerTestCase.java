package info.jtrac.test;

import info.jtrac.JtracTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class is for development convenience and runs only a single
 * method in test case since NetBeans does not support this at the moment.
 */
public class SingleMethodRunnerTestCase extends TestCase {
    /**
     * This method creates a TestSuite and adds a test
     * for a specified method (given as System property:
     * <code>method.name</code>).
     * 
     * @return testSuite The TestSuite containing the test for the specified method.
     * @throws Exception
     */
    public static Test suite() throws Exception { 
        TestSuite testSuite = new TestSuite();
        String name = System.getProperty("method.name");
        Test test = (Test) new JtracTest(name); 
        testSuite.addTest(test);
        return testSuite;
    }
}