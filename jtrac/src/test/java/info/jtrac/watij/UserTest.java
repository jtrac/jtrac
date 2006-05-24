package info.jtrac.watij;

import junit.framework.TestCase;
import watij.IE;
import static watij.symbols.Symbol.*;

public class UserTest extends TestCase {
            
    public void testLogin() throws Exception {
        
        IE ie = new IE();        
        ie.start("http://localhost:8080/jtrac");
        ie.textField(name, "j_username").set("admin");
        ie.textField(name, "j_password").set("admin");
        ie.button("Submit").click();
        assertTrue(ie.containsText("DASHBOARD"));
        
        ie.link(text, "OPTIONS").click();
        ie.link(text, "Users").click();
        ie.link(text, "Create New User").click();
        ie.textField(name, "user.loginName").set("testuser");
        ie.textField(name, "user.name").set("Test User");
        ie.textField(name, "user.email").set("foo@bar.com");
        ie.button("Submit").click();
        ie.button("Cancel").click();        
        assertTrue(ie.containsText("Test User"));
	        
    }    
    
}
