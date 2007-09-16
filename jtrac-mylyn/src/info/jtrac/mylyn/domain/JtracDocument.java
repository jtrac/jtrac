package info.jtrac.mylyn.domain;

import org.dom4j.Document;
import org.dom4j.Node;

public class JtracDocument {

	private Document document;
	
	public JtracDocument(Document document) {
		this.document = document;
	}
	
	public Document getDocument() {
		return document;
	}
	
	public String getText(String xpath) {
		Node n = document.selectSingleNode(xpath);
		if (n == null) {
			return null;
		}
		return n.getStringValue();
	}
	
}
