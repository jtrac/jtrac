package info.jtrac.mylar.domain;

import info.jtrac.mylar.util.XmlUtils;
import junit.framework.TestCase;

public class SuccessTest extends TestCase {
	
	public void testXmlToSuccess() {
		String xml = "<success><foo>FOO</foo><bar>BAR</bar></success>";
		Success success = new Success(XmlUtils.parse(xml));
		assertEquals("FOO", success.getValue("foo"));
		assertEquals("BAR", success.getValue("bar"));
	}

}
