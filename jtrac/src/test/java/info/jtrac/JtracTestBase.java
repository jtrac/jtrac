package info.jtrac;

import info.jtrac.domain.Config;
import info.jtrac.domain.Counts;
import info.jtrac.domain.CountsHolder;
import info.jtrac.domain.Field;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemItem;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.domain.State;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.util.ItemUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.UserDetails;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * base class for tests that can test either the service layer or dao or both
 * using the Spring JUnit helper class with the long name, ensures that
 * the applicationContext is only built once
 */
public abstract class JtracTestBase extends AbstractTransactionalDataSourceSpringContextTests {
    
    protected Jtrac jtrac;
    protected JtracDao dao;
    
    public JtracTestBase() {
        System.setProperty("jtrac.home", "target/home");
    }
    
    // magically autowired by Spring JUnit support
    public void setDao(JtracDao dao) {
        this.dao = dao;
    }
    
    //  magically autowired by Spring JUnit support
    public void setJtrac(Jtrac jtrac) {
        this.jtrac = jtrac;
    }
    
    protected String[] getConfigLocations() {
        return new String[] {
            "file:src/main/webapp/WEB-INF/applicationContext.xml",
            "file:src/main/webapp/WEB-INF/applicationContext-lucene.xml"
        };
    }   
    
}
