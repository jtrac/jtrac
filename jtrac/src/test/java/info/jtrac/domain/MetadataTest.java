package info.jtrac.domain;
import java.util.Map;

import java.util.Set;
import junit.framework.TestCase;

public class MetadataTest extends TestCase {
    
    private Metadata getMetadata() {
        Metadata metadata = new Metadata();
        String xmlString = "<metadata><fields>" 
                + "<field name='cusInt01' label='Test Label'/>"
                + "<field name='cusInt02' label='Test Label 2'/>"
                + "</fields></metadata>";
        metadata.setXml(xmlString);
        return metadata;
    }    
    
    public void testGetFieldByName() {
        Metadata m = getMetadata();
        Field f = m.getField("cusInt01");
        assertEquals("Test Label", f.getLabel());
    }
    
    public void testGetFieldsFromXml() {
        Metadata m = getMetadata();
        Map<Field.Name, Field> fields = m.getFields();
        assertTrue(fields.size() == 2);
        Field[] fa = fields.values().toArray(new Field[0]);
        assertEquals("cusInt01",  fa[0].getName() + "");
        assertEquals("Test Label",  fa[0].getLabel());
        assertEquals("cusInt02",  fa[1].getName() + "");
        assertEquals("Test Label 2",  fa[1].getLabel());
    }
    
    public void testMetadataInheritance() {
        Metadata m1 = getMetadata();
        Metadata m2 = new Metadata();
        String xmlString = "<metadata><fields>" 
                + "<field name='cusInt03' label='Test Label 3'/>"
                + "<field name='cusInt04' label='Test Label 4'/>"
                + "</fields></metadata>";
        m2.setXml(xmlString);
        m2.setParent(m1);
        Map<Field.Name, Field> fields = m2.getFields();
        assertEquals(fields.size(), 4);
        Set<Field.Name> names = m2.getUnusedFieldNames();
        assertEquals(names.contains(Field.Name.CUS_INT_01), false);
        assertEquals(names.contains(Field.Name.CUS_INT_04), false);
        assertEquals(names.size(), Field.Name.values().length - 4);        
    }
    
    public void testInitRolesThenAddRolesAndStates() {
        Metadata m = new Metadata();
        m.initRoles();
        assertEquals("New, Open and Closed available by default", 3, m.getStatesCount());
        assertEquals("DEFAULT available by default", 1, m.getRolesCount());        
        Field f = new Field(Field.Name.CUS_INT_01);
        m.add(f);
        assertEquals(1, m.getFieldsCount());
        assertEquals("New", m.getStatusValue(0));
        assertEquals("Open", m.getStatusValue(1));
        assertEquals("Closed", m.getStatusValue(99));
        assertEquals("", m.getStatusValue(50));        
    }    
    
    
}
