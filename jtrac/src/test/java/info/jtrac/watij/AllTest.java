package info.jtrac.watij;

import junit.framework.Test;
import static watij.finders.SymbolFactory.*;

public class AllTest extends WatijTestCase {
    
    static {
        clazz = AllTest.class;
    }
    
    public AllTest(String name) {
        super(name);
    }        
    
    public void testGetLoginPage() throws Exception {
        ie.start("http://localhost:8080/jtrac/app/login");
        assertTrue(ie.containsText("JTrac"));
    }
    
    public void testSuccessfulLogin() throws Exception {        
        ie.textField(name, "loginName").set("admin");
        ie.textField(name, "password").set("admin");
        ie.button("Submit").click();
        assertTrue(ie.containsText("DASHBOARD"));
    }     
    
    public void testCreateNewSpaceAndAllocateAdmin() throws Exception {
        
        click("options");
        assertTrue(ie.containsText("Options Menu"));        
        
        click("spaces");
        assertTrue(ie.containsText("Space List"));                
        
        clickLink("create");
        assertTrue(ie.containsText("Space Details"));        
                
        ie.textField(name, "space.name").set("Test Space");
        ie.textField(name, "space.prefixCode").set("TEST");
        ie.button("Next").click();
        assertTrue(ie.containsText("Custom Fields for Space:"));
        
        ie.button("Next").click();
        assertTrue(ie.containsText("Space Roles"));
        
        ie.button("Save").click();
        assertTrue(ie.containsText("Users Allocated To Space"));
                
        ie.button("Allocate").click();        
        assertTrue(ie.containsText("Admin"));  
           
    }
    
    public void testCreateNewItem() throws Exception {
        
        click("dashboard");
        assertTrue(ie.containsText("Test Space"));
        
        click("new");
        assertTrue(ie.containsText("Summary"));
        
        ie.textField(name, "summary").set("Test Summary");
        ie.textField(name, "detail").set("Test Detail");
        ie.selectList(name, "hideAssignedTo:border:assignedTo").option(text, "Admin").select();
        ie.button("Submit").click();
        assertTrue(ie.containsText("TEST-1"));
    }

    public void testSearchAllContainsItem() throws Exception {
        
        click("search");
        assertTrue(ie.containsText("Text Search"));
        
        ie.button("Search").click();
        assertTrue(ie.containsText("1 Record Found"));
        
        clickLink("refId");
        assertTrue(ie.containsText("History"));
    }
     
    public void testUpdateHistoryForItem() throws Exception {
        
        ie.selectList(name, "status").option(text, "Closed").select();
        ie.textField(name, "comment").set("Test Comment");
        ie.button("Submit").click();
        assertTrue(ie.containsText("Test Comment"));
        
    }

    public void testCreateNewUser() throws Exception {
        
        click("options");                
        click("users");
        assertTrue(ie.containsText("Users and allocated Spaces"));
        
        clickLink("create");
        assertTrue(ie.containsText("User Details"));
        
        ie.textField(name, "user.loginName").set("testuser");
        ie.textField(name, "user.name").set("Test User");
        ie.textField(name, "user.email").set("foo@bar.com");
        ie.button("Submit").click();       
        assertTrue(ie.containsText("Test User"));    
    }
        
    public void testLogout() throws Exception {
        
        click("logout");
        assertTrue(ie.containsText("Logout Successful"));
        ie.close();
        
    }        
    
}
