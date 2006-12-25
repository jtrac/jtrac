package info.jtrac.mylar;

import info.jtrac.mylar.domain.Version;
import junit.framework.TestCase;

public class JtracClientTest extends TestCase {
	
	public void testGetItemAsXml() throws Exception {
		JtracClient client = new JtracClient("http://localhost:8080/jtrac/api/", null, null, null);		
		Version v = client.getVersion();
		assertNotNull(v.getNumber());
		assertNotNull(v.getTimestamp());
	}

}
