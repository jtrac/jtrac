package info.jtrac.mylar;

import info.jtrac.mylar.domain.JtracVersion;
import junit.framework.TestCase;

public class JtracClientTest extends TestCase {
	
	public void testGetItemAsXml() throws Exception {
		JtracClient client = new JtracClient("http://localhost:8080/jtrac", null, null, null);		
		JtracVersion v = client.getJtracVersion();
		assertNotNull(v.getNumber());
		assertNotNull(v.getTimestamp());
	}

}
