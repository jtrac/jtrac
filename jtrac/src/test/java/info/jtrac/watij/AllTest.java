package info.jtrac.watij;

import junit.framework.TestCase;
import watij.IE;
import static watij.symbols.Symbol.*;

public class AllTest extends TestCase {
            
    public void testAll() throws Exception {
        
        IE ie = new IE();        
        ie.start("http://localhost:8080/jtrac");
        assertTrue(ie.containsText("JTrac"));
                
        ie.textField(name, "j_username").set("admin");
        ie.textField(name, "j_password").set("admin");
        ie.button("Submit").click();
        assertTrue(ie.containsText("DASHBOARD"));
        
        ie.link(text, "OPTIONS").click();
        assertTrue(ie.containsText("Options Menu"));
        
        ie.link(text, "Spaces").click();
        assertTrue(ie.containsText("Spaces"));
        
        ie.link(text, "Create New Space").click();
        assertTrue(ie.containsText("Create New Space"));
        
        ie.textField(name, "prefixCode").set("TEST");
        ie.button("Next").click();
        assertTrue(ie.containsText("Custom Fields for Space: TEST"));
        
        ie.button("Next").click();
        assertTrue(ie.containsText("Space Roles"));
        
        ie.button("Save").click();
        assertTrue(ie.containsText("Users allocated to Space"));
        
        ie.button("Allocate").click();
        assertTrue(ie.containsText("Admin User"));        
        
        ie.link(text, "DASHBOARD").click();
        assertTrue(ie.containsText("TEST"));
        
        ie.link(text, "NEW").click();
        assertTrue(ie.containsText("Summary"));
        
        ie.textField(name, "summary").set("Test Summary");
        ie.textField(name, "detail").set("Test Detail");
        ie.selectList(name, "assignedTo").select("Admin User");
        ie.button("Submit").click();
        assertTrue(ie.containsText("TEST-1"));
        
        ie.link(text, "SEARCH").click();
        assertTrue(ie.containsText("View Item by Id"));
        
        ie.button("Search").click();
        assertTrue(ie.containsText("1 record found"));
        
        ie.link(text, "TEST-1").click();
        assertTrue(ie.containsText("History"));
        
        ie.selectList(name, "status").select("Closed");
        ie.textField(name, "comment").set("Test Comment");
        ie.button("Submit").click();
        assertTrue(ie.containsText("Test Comment"));
        
        ie.link(text, "OPTIONS").click();                
        ie.link(text, "Users").click();
        assertTrue(ie.containsText("Users and Allocated Trackers"));
        
        ie.link(text, "Create New User").click();
        assertTrue(ie.containsText("Edit User"));
        
        ie.textField(name, "user.loginName").set("testuser");
        ie.textField(name, "user.name").set("Test User");
        ie.textField(name, "user.email").set("foo@bar.com");
        ie.button("Submit").click();
        assertTrue(ie.containsText("Spaces allocated to User"));
        
        ie.button("Cancel").click();        
        assertTrue(ie.containsText("Test User"));        
        
        ie.link(text, "LOGOUT").click();
        assertTrue(ie.containsText("Logout Successful"));
        
        ie.close();  
	        
    }    
    
}
