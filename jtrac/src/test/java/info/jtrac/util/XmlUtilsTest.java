package info.jtrac.util;

import info.jtrac.util.XmlUtils;
import junit.framework.TestCase;
import org.dom4j.Document;

public class XmlUtilsTest extends TestCase {
    
    public void testXmlStringParse() {
        String s = "<test/>";
        Document d = XmlUtils.parse(s);
        assertTrue(d.getRootElement().getName().equals("test"));
    }
    
    public void testBadXmlParseFails() {
        String s = "foo";
        try {
            Document d = XmlUtils.parse(s);
            fail("How did we parse invalid XML?");
        } catch (Exception e) {
            // expected
        }        
    }
    
    public void testGetAsPrettyXml() {
        String s = "<root><node1><node2>data</node2></node1></root>";
        String result = XmlUtils.getAsPrettyXml(s);
        assertTrue(result.equals("<root>\n <node1>\n  <node2>data</node2>\n </node1>\n</root>"));        
    }
    
}
