package info.jtrac;

import info.jtrac.domain.Config;
import info.jtrac.domain.Field;
import info.jtrac.domain.Item;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.domain.UserRole;
import info.jtrac.domain.Counts;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.UserDetails;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * JUnit test cases for the business implementation as well as the DAO, combined into
 * one class so that the Spring application context needs to be loaded only once
 * which is taken care of by the Spring JUnit helper / extensions
 * Tests assume that a database is available, and with HSQLDB around this is not
 * an issue.
 */
public class JtracTest extends AbstractTransactionalDataSourceSpringContextTests {
    
    private Jtrac jtrac;
    private JtracDao dao;
    
    // magically autowired by Spring JUnit helper / extension
    public void setDao(JtracDao dao) {
        this.dao = dao;
    }
    
    //  magically autowired by Spring JUnit helper / extension
    public void setJtrac(Jtrac jtrac) {
        this.jtrac = jtrac;
    }
    
    protected String[] getConfigLocations() {
        System.setProperty("jtrac.home", "target/home");
        return new String[] {
            "file:src/main/webapp/WEB-INF/applicationContext.xml",
            "file:src/main/webapp/WEB-INF/applicationContext-lucene.xml"
        };
    }
    
    //==============================================================================
    
    public void testGeneratedPasswordIsAlwaysDifferent() {
        String p1 = jtrac.generatePassword();
        String p2 = jtrac.generatePassword();
        assertTrue(!p1.equals(p2));
    }
    
    public void testEncodeClearTextPassword() {
        assertEquals("21232f297a57a5a743894a0e4a801fc3", jtrac.encodeClearText("admin"));
    }
    
    private Metadata getMetadata() {
        Metadata metadata = new Metadata();
        String xmlString = "<metadata><fields>"
                + "<field name='cusInt01' label='Test Label'/>"
                + "<field name='cusInt02' label='Test Label 2'/>"
                + "</fields></metadata>";
        metadata.setXml(xmlString);
        return metadata;
    }
    
    //========================== DAO TESTS ==============================
    
    public void testMetadataInsertAndLoad() {
        Metadata m1 = getMetadata();
        jtrac.storeMetadata(m1);
        assertTrue(m1.getId() > 0);
        Metadata m2 = jtrac.loadMetadata(m1.getId());
        assertTrue(m2 != null);
        Map<Field.Name, Field> fields = m2.getFields();
        assertTrue(fields.size() == 2);
    }
    
    public void testUserInsertAndLoad() {
        User user = new User();
        user.setLoginName("test");
        user.setEmail("test@jtrac.com");
        jtrac.storeUser(user);
        User user1 = jtrac.loadUser("test");
        assertTrue(user1.getEmail().equals("test@jtrac.com"));
        User user2 = dao.findUsersByEmail("test@jtrac.com").get(0);
        assertTrue(user2.getLoginName().equals("test"));
    }
    
    public void testUserSpaceRolesInsert() {
        Space space = new Space();
        space.setPrefixCode("SPACE");
        space.setDescription("test description");
        Metadata metadata = getMetadata();
        
        space.setMetadata(metadata);
        jtrac.storeSpace(space);
        
        User user = new User();
        user.setLoginName("test");
        
        user.addSpaceRole(space, "ROLE_TEST");
        jtrac.storeUser(user);
        
        User u1 = jtrac.loadUser("test");
        
        GrantedAuthority[] gas = u1.getAuthorities();
        assertEquals(2, gas.length);
        assertEquals("ROLE_USER", gas[0].getAuthority());
        assertEquals("ROLE_TEST_SPACE", gas[1].getAuthority());
        
        List<UserRole> userRoles = jtrac.findUserRolesForSpace(space.getId());
        assertEquals(1, userRoles.size());
        UserRole ur = userRoles.get(0);
        assertEquals("test", ur.getUser().getLoginName());
        assertEquals("ROLE_TEST", ur.getRoleKey());
        
        List<User> users = jtrac.findUsersForUser(u1);
        assertEquals(1, users.size());
        
        List<User> users2 = jtrac.findUsersForSpace(space.getId());
        assertEquals(1, users2.size());
        
    }
    
    public void testConfigStoreAndLoad() {
        Config config = new Config("testParam", "testValue");
        jtrac.storeConfig(config);
        String value = jtrac.loadConfig("testParam");
        assertEquals("testValue", value);
    }
    
    public void testStoreAndLoadUserWithAdminRole() {
        User user = new User();
        user.setLoginName("test");
        user.addSpaceRole(null, "ROLE_ADMIN");
        jtrac.storeUser(user);
        
        UserDetails ud = jtrac.loadUserByUsername("test");
        
        Set<String> set = new HashSet<String>();
        for (GrantedAuthority ga : ud.getAuthorities()) {
            set.add(ga.getAuthority());
        }
        
        assertEquals(2, set.size());
        assertTrue(set.contains("ROLE_USER"));
        assertTrue(set.contains("ROLE_ADMIN"));
        
    }
    
    public void testDefaultAdminUserHasAdminRole() {
        UserDetails ud = jtrac.loadUserByUsername("admin");
        Set<String> set = new HashSet<String>();
        for (GrantedAuthority ga : ud.getAuthorities()) {
            set.add(ga.getAuthority());
        }
        assertEquals(2, set.size());
        assertTrue(set.contains("ROLE_USER"));
        assertTrue(set.contains("ROLE_ADMIN"));
    }
    
    public void testItemInsertAndCounts() {
        Space s = new Space();
        s.setPrefixCode("TEST");
        jtrac.storeSpace(s);
        User u = new User();
        u.setLoginName("test");
        u.addSpaceRole(s, "DEFAULT");
        jtrac.storeUser(u);
        Item i = new Item();
        i.setSpace(s);
        i.setAssignedTo(u);
        i.setLoggedBy(u);
        i.setStatus(99);
        jtrac.storeItem(i, null);
        assertEquals(1, i.getSequenceNum());
        
        Counts total = jtrac.loadCountsForUser(u);
        assertEquals(1, total.getAssignedTo());
        assertEquals(1, total.getLoggedBy());
        assertEquals(0, total.getOpen());
        assertEquals(1, total.getClosed());
        assertEquals(1, total.getTotal());
        
        Map<Long, Counts> counts = total.getCounts();
        assertEquals(1, counts.size());
        Counts c = counts.get(s.getId());
        assertEquals(1, c.getLoggedBy());
        assertEquals(1, c.getAssignedTo());        
        assertEquals(0, c.getOpen());
        assertEquals(1, c.getClosed());        
        assertEquals(1, c.getTotal());
    }
    
    
}
