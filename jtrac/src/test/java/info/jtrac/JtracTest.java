package info.jtrac;

import info.jtrac.domain.Config;
import info.jtrac.domain.Field;
import info.jtrac.domain.Item;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.domain.Counts;
import info.jtrac.domain.State;
import info.jtrac.domain.UserSpaceRole;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

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
    private DataSource ds;
    
    public JtracTest() {
        // have to do this because we have two beans of type DataSource (lazy-init)
        setAutowireMode(AUTOWIRE_BY_NAME);
        System.setProperty("jtrac.home", "target/home");
    }
    
    // magically autowired by Spring JUnit helper / extension
    public void setDao(JtracDao dao) {
        this.dao = dao;
    }
    
    //  magically autowired by Spring JUnit helper / extension
    public void setJtrac(Jtrac jtrac) {
        this.jtrac = jtrac;
    }
    
    protected String[] getConfigLocations() {
        return new String[] {
            "file:src/main/webapp/WEB-INF/applicationContext.xml",
            "file:src/main/webapp/WEB-INF/applicationContext-lucene.xml"
        };
    }
    
    //==========================================================================
    
    private Space getSpace() {
        Space space = new Space();
        space.setPrefixCode("TEST");
        space.setName("Test Space");
        return space;
    }
    
    private Metadata getMetadata() {
        Metadata metadata = new Metadata();
        String xmlString = "<metadata><fields>"
                + "<field name='cusInt01' label='Test Label'/>"
                + "<field name='cusInt02' label='Test Label 2'/>"
                + "</fields></metadata>";
        metadata.setXmlString(xmlString);
        return metadata;
    }    
    
    //==========================================================================
    
    public void testGeneratedPasswordIsAlwaysDifferent() {
        String p1 = jtrac.generatePassword();
        String p2 = jtrac.generatePassword();
        assertTrue(!p1.equals(p2));
    }
    
    public void testEncodeClearTextPassword() {
        assertEquals("21232f297a57a5a743894a0e4a801fc3", jtrac.encodeClearText("admin"));
    }
        
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
        Space space = getSpace();
        Metadata metadata = getMetadata();
        
        space.setMetadata(metadata);
        jtrac.storeSpace(space);
        
        User user = new User();
        user.setLoginName("test");
        
        user.addSpaceWithRole(space, "ROLE_TEST");
        jtrac.storeUser(user);
        
        User u1 = jtrac.loadUser("test");
        
        GrantedAuthority[] gas = u1.getAuthorities();
        assertEquals(2, gas.length);
        assertEquals("ROLE_USER", gas[0].getAuthority());
        assertEquals("ROLE_TEST_TEST", gas[1].getAuthority());
        
        List<UserSpaceRole> userSpaceRoles = jtrac.findUserRolesForSpace(space.getId());
        assertEquals(1, userSpaceRoles.size());
        UserSpaceRole usr = userSpaceRoles.get(0);
        assertEquals("test", usr.getUser().getLoginName());
        assertEquals("ROLE_TEST", usr.getRoleKey());
        
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
        user.addSpaceWithRole(null, "ROLE_ADMIN");
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
        Space s = getSpace();
        jtrac.storeSpace(s);
        User u = new User();
        u.setLoginName("test");
        u.addSpaceWithRole(s, "DEFAULT");
        jtrac.storeUser(u);
        Item i = new Item();
        i.setSpace(s);
        i.setAssignedTo(u);
        i.setLoggedBy(u);
        i.setStatus(State.CLOSED);
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
    
    public void testRemoveSpaceRoleDoesNotOrphanDatabaseRecord() {
        Space space = getSpace();
        jtrac.storeSpace(space);
        long spaceId = space.getId();
        User user = new User();
        user.setLoginName("test");
        user.addSpaceWithRole(space, "ROLE_ADMIN");
        jtrac.storeUser(user);
        long id = jdbcTemplate.queryForLong("select id from user_space_roles where space_id = " + spaceId);
        UserSpaceRole usr = jtrac.loadUserSpaceRole(id);
        assertEquals(spaceId, usr.getSpace().getId());                
        jtrac.removeUserSpaceRole(usr);
        endTransaction();
        assertEquals(0, jdbcTemplate.queryForInt("select count(0) from user_space_roles where space_id = " + spaceId));        
    }
    
    public void testFindSpacesWhereGuestAllowed() {
        Space space = getSpace();
        space.setGuestAllowed(true);
        jtrac.storeSpace(space);
        assertEquals(1, jtrac.findSpacesWhereGuestAllowed().size());
    }
    
    public void testRenameSpaceRole() {
        Space space = getSpace();
        jtrac.storeSpace(space);
        User u = new User();
        u.setLoginName("test");
        u.addSpaceWithRole(space, "DEFAULT");
        jtrac.storeUser(u);
        assertEquals(1, jdbcTemplate.queryForInt("select count(0) from user_space_roles where role_key = 'DEFAULT'"));
        jtrac.updateSpaceRole("DEFAULT", "NEWDEFAULT", space);
        assertEquals(0, jdbcTemplate.queryForInt("select count(0) from user_space_roles where role_key = 'DEFAULT'"));
        assertEquals(1, jdbcTemplate.queryForInt("select count(0) from user_space_roles where role_key = 'NEWDEFAULT'"));
        
    }
    
}
