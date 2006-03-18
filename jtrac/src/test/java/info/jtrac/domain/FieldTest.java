package info.jtrac.domain;

import info.jtrac.domain.Field;
import info.jtrac.util.XmlUtils;
import junit.framework.TestCase;
import org.dom4j.Document;
import org.dom4j.Element;

public class FieldTest extends TestCase {
    
    public void testSetValidName() {
        Field field = new Field();
        field.setName("cusInt01");
        assertEquals(field.getName().toString(), "cusInt01");        
    }
    
    public void testSetInValidNameFails() {
        Field field = new Field();
        try {
            field.setName("foo");
            fail("How did we set an invalid name?");
        } catch (Exception e) {
            // expected
        }        
    }    
    
    public void testConstructFromXml() {
        Document d = XmlUtils.parse("<field name='cusInt01' label='Test Label'/>");
        Field field = new Field(d.getRootElement());
        assertEquals("cusInt01", field.getName().toString());
        assertEquals("Test Label", field.getLabel());
        assertEquals(field.isOptional(), false);
    }
    
    public void testConstructFromXmlWithOptionalAttribute() {
        Document d = XmlUtils.parse("<field name='cusInt01' label='Test Label' optional='true'/>");
        Field field = new Field(d.getRootElement());
        assertTrue(field.isOptional());
    }
    
    public void testGetAsXml() {
        Field field = new Field();
        field.setName("cusInt01");
        field.setLabel("Test Label");        
        Element e = field.getAsElement();
        assertEquals("cusInt01", e.attributeValue("name"));
        assertEquals("Test Label", e.attributeValue("label"));    
    }  
    
}
