package info.jtrac.mylar;

import junit.framework.TestCase;

public class JtracRepositoryConnectorTest extends TestCase {
	
	public void testGetRepositoryUrlFromTaskUrl() {
		JtracRepositoryConnector connector = new JtracRepositoryConnector();
		String url = "http://localhost/jtrac/app/item/ABC-123";
		assertEquals("http://localhost/jtrac", connector.getRepositoryUrlFromTaskUrl(url));
	}
	
	public void testGetTaskIdFromTaskUrl() {
		JtracRepositoryConnector connector = new JtracRepositoryConnector();
		String url = "http://localhost/jtrac/app/item/ABC-123";
		assertEquals("ABC-123", connector.getTaskIdFromTaskUrl(url));
	}	

}
