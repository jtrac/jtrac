package info.jtrac;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * base class for tests that can test either the service layer or dao or both
 * using the Spring JUnit helper class with the long name, ensures that
 * the applicationContext is only built once
 */
public abstract class JtracTestBase extends AbstractTransactionalDataSourceSpringContextTests {
    
    protected Jtrac jtrac;
    protected JtracDao dao;
    
    public JtracTestBase(String name) {
        super(name);
    }
    
    // magically autowired by Spring JUnit support
    public void setDao(JtracDao dao) {
        this.dao = dao;
    }
    
    //  magically autowired by Spring JUnit support
    public void setJtrac(Jtrac jtrac) {
        this.jtrac = jtrac;
    }
    
    @Override
    protected String[] getConfigLocations() {
        System.setProperty("jtrac.home", "target/home");
        return new String[] {
            "file:src/main/webapp/WEB-INF/applicationContext.xml",
            "file:src/main/webapp/WEB-INF/applicationContext-lucene.xml"
        };
    }   
    
}
