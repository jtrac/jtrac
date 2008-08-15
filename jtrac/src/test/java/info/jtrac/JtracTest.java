package info.jtrac;

import info.jtrac.domain.Config;
import info.jtrac.domain.Counts;
import info.jtrac.domain.CountsHolder;
import info.jtrac.domain.Field;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemItem;
import info.jtrac.domain.ItemUser;
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

/**
 * JUnit test cases for the business implementation as well as the DAO
 * Tests assume that a database is available, and with HSQLDB around this is not
 * an issue.
 */
public class JtracTest extends JtracTestBase {
    
    public JtracTest(String name) {
        super(name);
    }
    
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
    
    private void cleanDatabase() { 
        jdbcTemplate.execute("delete from user_space_roles where id > 1");
        deleteFromTables(new String[] {
            "history",
            "items",
            "spaces",
            "metadata",
            "space_sequence"                        
        });        
        jdbcTemplate.execute("delete from users where id > 1");         
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
        assertEquals(1, gas.length);        
        assertEquals("ROLE_TEST:TEST", gas[0].getAuthority());
        
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
        
        assertEquals(1, set.size());        
        assertTrue(set.contains("ROLE_ADMIN"));
        
    }
    
    public void testDefaultAdminUserHasAdminRole() {
        UserDetails ud = jtrac.loadUserByUsername("admin");
        Set<String> set = new HashSet<String>();
        for (GrantedAuthority ga : ud.getAuthorities()) {
            set.add(ga.getAuthority());
        }
        assertEquals(1, set.size());        
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
        
        CountsHolder ch = jtrac.loadCountsForUser(u);
        assertEquals(1, ch.getTotalAssignedToMe());
        assertEquals(1, ch.getTotalLoggedByMe());
        assertEquals(1, ch.getTotalTotal());
        
        Counts c = ch.getCounts().get(s.getId());
        assertEquals(1, c.getLoggedByMe());
        assertEquals(1, c.getAssignedToMe());
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
        setComplete();
        endTransaction();        
        assertEquals(0, jdbcTemplate.queryForInt("select count(0) from user_space_roles where space_id = " + spaceId));
        cleanDatabase();
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
        jtrac.bulkUpdateRenameSpaceRole(space, "DEFAULT", "NEWDEFAULT");
        assertEquals(0, jdbcTemplate.queryForInt("select count(0) from user_space_roles where role_key = 'DEFAULT'"));
        assertEquals(1, jdbcTemplate.queryForInt("select count(0) from user_space_roles where role_key = 'NEWDEFAULT'"));
    }
    
    public void testGetItemAsHtmlDoesNotThrowException() {
        Config config = new Config("mail.server.host", "dummyhost");
        jtrac.storeConfig(config);
        // now email sending is switched on
        Space s = getSpace();
        jtrac.storeSpace(s);
        User u = new User();
        u.setLoginName("test");
        u.setName("Test User");
        u.setEmail("test");
        u.addSpaceWithRole(s, "DEFAULT");
        jtrac.storeUser(u);
        Item i = new Item();
        i.setSpace(s);
        i.setAssignedTo(u);
        i.setLoggedBy(u);
        i.setStatus(State.CLOSED);
        // next step will internally try to render item as Html for sending e-mail
        jtrac.storeItem(i, null);
        String rendered = ItemUtils.getAsXml(i).asXML();
        assertTrue(rendered.contains("<item refId=\"TEST-"));
    }
    
    public void testDeleteItemThatHasRelatedItems() {
        Space s = getSpace();
        jtrac.storeSpace(s);
        User u = new User();
        u.setLoginName("test");
        u.setEmail("dummy");
        u.addSpaceWithRole(s, "DEFAULT");
        jtrac.storeUser(u);
        //========================
        Item i0 = new Item();
        i0.setSpace(s);
        i0.setAssignedTo(u);
        i0.setLoggedBy(u);
        i0.setStatus(State.CLOSED);
        jtrac.storeItem(i0, null);
        //=======================
        Item i1 = new Item();
        i1.setSpace(s);
        i1.setAssignedTo(u);
        i1.setLoggedBy(u);
        i1.setStatus(State.CLOSED);
        i1.addRelated(i0, ItemItem.DEPENDS_ON);
        jtrac.storeItem(i1, null);
        //========================
        Item i2 = new Item();
        i2.setSpace(s);
        i2.setAssignedTo(u);
        i2.setLoggedBy(u);
        i2.setStatus(State.CLOSED);
        i2.addRelated(i1, ItemItem.DUPLICATE_OF);
        jtrac.storeItem(i2, null);
        assertEquals(3, jtrac.loadCountOfHistoryInvolvingUser(u));
        // can we remove i1?
        Item temp = jtrac.loadItem(i1.getId());
        jtrac.removeItem(temp);
    }        
    
    public void testDeletingUserDeletesItemUsersAlso() {
        Space s = getSpace();
        jtrac.storeSpace(s);
        User u = new User();
        u.setLoginName("test");
        u.setEmail("dummy");
        u.addSpaceWithRole(s, "DEFAULT");
        jtrac.storeUser(u);
        //========================
        Item i = new Item();
        i.setSpace(s);
        i.setAssignedTo(u);
        i.setLoggedBy(u);
        i.setStatus(State.CLOSED);
        //========================
        // another user to "watch" this item
        User w = new User();
        w.setLoginName("test1");
        w.setEmail("dummy");
        w.addSpaceWithRole(s, "DEFAULT");
        jtrac.storeUser(w);
        ItemUser iu = new ItemUser(w);
        Set<ItemUser> ius = new HashSet<ItemUser>();
        ius.add(iu);
        i.setItemUsers(ius);
        //========================
        jtrac.storeItem(i, null);
        setComplete();
        endTransaction();
        
        startNewTransaction();
        jtrac.removeUser(w);
        setComplete();
        endTransaction();
        
        startNewTransaction();  
        Item dummyItem = jtrac.loadItem(i.getId());
        assertEquals(0, dummyItem.getItemUsers().size());
        
        cleanDatabase();                
        
    }      
    
    public void testLogicToFindNotUsersAndSpacesNotAllocated() {
        
        cleanDatabase();
        
        Space s1 = getSpace();
        Metadata m1 = getMetadata();
        m1.initRoles();
        s1.setMetadata(m1);
        jtrac.storeSpace(s1);
        
        Space s2 = getSpace();
        s2.setPrefixCode("TEST2");
        Metadata m2 = getMetadata();
        m2.initRoles();
        s2.setMetadata(m2);
        jtrac.storeSpace(s2);
        
        User u1 = new User();
        u1.setLoginName("test");
        
        u1.addSpaceWithRole(s1, "DEFAULT");
        jtrac.storeUser(u1);
        
        List<Space> list = jtrac.findSpacesNotFullyAllocatedToUser(u1.getId());
        assertEquals(2, list.size()); 
        
        jtrac.storeUserSpaceRole(u1, s1, "ROLE_ADMIN");
        
        List<Space> list2 = jtrac.findSpacesNotFullyAllocatedToUser(u1.getId());
        assertEquals(1, list2.size());   
        
        User u2 = new User();
        u2.setLoginName("test2");
        jtrac.storeUser(u2);
        
        List<User> list3 = jtrac.findUsersNotFullyAllocatedToSpace(s1.getId());
        // admin user exists also
        assertEquals(2, list3.size());
        
        jtrac.storeUserSpaceRole(u2, s1, "DEFAULT");
        
        List<User> list4 = jtrac.findUsersNotFullyAllocatedToSpace(s1.getId());
        logger.info(list4);
        assertEquals(2, list4.size()); 
        
        
        jtrac.storeUserSpaceRole(u2, s1, "ROLE_ADMIN");
        
        List<User> list5 = jtrac.findUsersNotFullyAllocatedToSpace(s1.getId());
        assertEquals(1, list5.size());                
        
    }
    
    public void testFindSuperUsers() {                
        
        List<User> list1 = dao.findSuperUsers();
        assertEquals(1, list1.size());
        assertEquals("admin", list1.get(0).getLoginName());
        
        User u1 = new User();
        u1.setLoginName("test2");
        jtrac.storeUser(u1);
        
        jtrac.storeUserSpaceRole(u1, null, "ROLE_ADMIN");              
        
        List<User> list2 = dao.findSuperUsers();
        assertEquals(2, list2.size());           
                
    }
    
    public void testLoadSpaceRolesMapForUser() {
        
        User u1 = new User();
        u1.setLoginName("test2");
        jtrac.storeUser(u1);
        
        jtrac.storeUserSpaceRole(u1, null, "ROLE_ADMIN");   
        
        Map<Long, List<UserSpaceRole>> map = jtrac.loadSpaceRolesMapForUser(u1.getId());
        assertEquals(1, map.size());
        
    }
    
}
