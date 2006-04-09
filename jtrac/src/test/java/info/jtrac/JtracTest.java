package info.jtrac;

import info.jtrac.domain.Field;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.domain.UserRole;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.acegisecurity.GrantedAuthority;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * JUnit test cases for the business implementation as well as the DAO, combined into
 * one class so that the Spring application context needs to be loaded only once
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
            "file:src/main/webapp/WEB-INF/applicationContext.xml" };
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
        dao.storeMetadata(m1);
        assertTrue(m1.getId() > 0);
        Metadata m2 = dao.loadMetadata(m1.getId());
        assertTrue(m2 != null);
        Map<Field.Name, Field> fields = m2.getFields();
        assertTrue(fields.size() == 2);
    }

    public void testUserInsertAndLoad() {
        User user = new User();
        user.setLoginName("test");
        user.setEmail("test@jtrac.com");
        dao.storeUser(user);
        User user1 = dao.findUsersByLoginName("test").get(0);
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
        dao.storeSpace(space);

        User user = new User();
        user.setLoginName("test");

        user.addSpaceRole(space, "ROLE_TEST");
        dao.storeUser(user);

        User u1 = dao.findUsersByLoginName("test").get(0);

        GrantedAuthority[] gas = u1.getAuthorities();
        assertEquals(2, gas.length);
        assertEquals("ROLE_USER", gas[0].getAuthority());
        assertEquals("ROLE_TEST_SPACE", gas[1].getAuthority());

        List<UserRole> userRoles = dao.findUsersForSpace(space.getId());
        assertEquals(1, userRoles.size());
        UserRole ur = userRoles.get(0);
        assertEquals("test", ur.getUser().getLoginName());
        assertEquals("ROLE_TEST", ur.getRoleKey());

    }

}
