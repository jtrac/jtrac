package info.jtrac.mylar;

import info.jtrac.mylar.domain.Item;
import info.jtrac.mylar.domain.JtracVersion;
import junit.framework.TestCase;

public class JtracClientTest extends TestCase {
	
	JtracClient client;
	
	@Override
	public void setUp() {
		client = new JtracClient("http://localhost:8080/jtrac", null, null, null);
	}
	
	public void testGetItem() throws Exception {		
		JtracVersion v = client.getJtracVersion();
		assertNotNull(v.getNumber());
		assertNotNull(v.getTimestamp());
	}
	
	public void testPutItem() throws Exception {
		Item item = new Item();
		item.setSummary("Test Summary");
		item.setDetail("Test Detail");
		item.setLoggedBy("Some Body");
		item.setAssignedTo("Any Body");
		String refId = client.putItem(item);
		assertNotNull(refId);
	}

}
