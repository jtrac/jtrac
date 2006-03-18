/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.jtrac.util;

import java.io.IOException;
import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * Utilities to parse strings into XML DOM Documents and vice versa
 */
public final class XmlUtils {
    
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
            throw new RuntimeException("XML Parse failed on '" + xmlString + "'");
        }
    }
    
    public static Element getNewElement(String name) {
        return DocumentHelper.createElement(name);
    }
    
    public static Document getNewDocument(String rootElementName) {
        return DocumentHelper.createDocument(getNewElement(rootElementName));
    }
    
}
