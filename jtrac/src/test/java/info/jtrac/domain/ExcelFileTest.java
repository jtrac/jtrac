package info.jtrac.domain;

import info.jtrac.domain.Field;
import info.jtrac.util.XmlUtils;
import junit.framework.TestCase;
import org.dom4j.Document;
import org.dom4j.Element;

public class ExcelFileTest extends TestCase {
    
    public void testLoadFile() {
        ExcelFile ef = new ExcelFile("src/test/resources/data.xls");
    } 
    
}
