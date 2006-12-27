package info.jtrac.mylar.domain;

import info.jtrac.mylar.util.XmlUtils;
import junit.framework.TestCase;

import org.dom4j.Document;

public class ItemTest extends TestCase {
	
	public void testXmlToItem() {
		String xml = "<item refId='ABC-123'><summary>Test Summary</summary><detail>Test Detail</detail>"
			+ "<loggedBy>Some Body</loggedBy><assignedTo>Any Body</assignedTo></item>";
		Document d = XmlUtils.parse(xml);
		Item item = new Item(new JtracDocument(d));
		assertEquals("ABC-123", item.getRefId());
		assertEquals("Test Summary", item.getSummary());
		assertEquals("Test Detail", item.getDetail());
		assertEquals("Some Body", item.getLoggedBy());
		assertEquals("Any Body", item.getAssignedTo());
	}

}
