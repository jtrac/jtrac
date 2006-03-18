package info.jtrac.domain;

import info.jtrac.domain.Field;
import info.jtrac.domain.Role;
import info.jtrac.domain.State;
import info.jtrac.util.XmlUtils;

import javax.naming.Name;
import junit.framework.TestCase;
import org.dom4j.Document;

public class RoleTest extends TestCase {
    
    public void testConstructFromXml() {
        Document d = XmlUtils.parse("<role name='TESTER'>" +
            "<state status='1'>" +
                "<transition status='2'/>" +
                "<transition status='3'/>" +
                "<field name='cusInt01' mask='1'/>" + 
                "<field name='cusInt02' mask='2'/>" +
            "</state>" +                
            "<state status='2'>" + 
                "<transition status='3'/>" +
                "<field name='cusInt03' mask='1'/>" + 
                "<field name='cusInt04' mask='2'/>" +
            "</state></role>");
        Role role = new Role(d.getRootElement());
        assertEquals("TESTER", role.getName());
        assertEquals(2, role.getStates().size());
        State s1 = role.getStates().get(1);
        assertEquals(2, s1.getTransitions().size());
        assertTrue(s1.getTransitions().contains(2));
        assertTrue(s1.getTransitions().contains(3));
        assertEquals(2 , s1.getFields().size());
        assertEquals(new Integer(1), s1.getFields().get(Field.Name.CUS_INT_01));
        assertEquals(new Integer(2), s1.getFields().get(Field.Name.CUS_INT_02));
    }    
}
