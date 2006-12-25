package info.jtrac.mylar.util;

import java.io.IOException;
import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XmlUtils {

    /**
     * uses Dom4j to neatly format a given XML string
     * by adding indents, newlines etc.
     */
    public static String getAsPrettyXml(String xmlString) {
        return getAsPrettyXml(parse(xmlString));
    }
    
    /**
     * Override that accepts an XML DOM Document
     * @param document XML as DOM Document
     */
    public static String getAsPrettyXml(Document document) {
        OutputFormat format = new OutputFormat(" ", true);
        format.setSuppressDeclaration(true);
        StringWriter out = new StringWriter();
        XMLWriter writer = new XMLWriter(out, format);
        try {
            try {
                writer.write(document);
            } finally {
                writer.close();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return out.toString().trim();
    }
    
    /**
     * Converts a String into XML by parsing into a DOM Document
     * uses Dom4j
     */
    public static Document parse(String xmlString) {
        try {
            return DocumentHelper.parseText(xmlString);
        } catch (DocumentException de) {
            throw new RuntimeException(de);
        }
    }
    
    public static Document getNewDocument(String rootElementName) {
        Document d = DocumentHelper.createDocument();
        d.addElement(rootElementName);
        return d;
    }
    
	
}
