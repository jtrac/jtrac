package info.jtrac.mylar;

import junit.framework.TestCase;

public class JtracClientTest extends TestCase {
	
	public void testGetItemAsXml() throws Exception {
		JtracClient client = new JtracClient("http://localhost:8080/jtrac");
		byte[] response = client.doGet("http://localhost:8080/jtrac/api/item/SSS-3");
		System.out.println(new String(response));
	}

}
