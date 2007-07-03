package info.jtrac.selenium;

public class AllTest extends SeleniumTestCase {
        
    static {
        clazz = AllTest.class;
    }
    
    public AllTest(String name) {
        super(name);
    }
    
    public void testGetLoginPage() {
        selenium.open("http://localhost:8080/jtrac/app/login");  
        assertEquals("JTrac Login", selenium.getTitle());
    }   
    
    public void testSuccessfulLogin() {   
        selenium.type("loginName", "admin");
        selenium.type("password", "admin");
        selenium.clickAndWait("//input[@value='Submit']");          
        assertTextPresent("DASHBOARD");
    }    
        
    public void testCreateNewSpaceAndAllocateAdmin() throws Exception {        
        selenium.clickAndWait("link=OPTIONS");        
        assertTextPresent("Options Menu");                
        selenium.clickAndWait("link=Manage Spaces");        
        assertTextPresent("Space List");                        
        selenium.clickAndWait("link=Create New Space");        
        assertTextPresent("Space Details");                        
        selenium.type("space.name", "Test Space");
        selenium.type("space.prefixCode", "TEST");
        selenium.clickAndWait("//input[@value='Next']");        
        assertTextPresent("Custom Fields for Space:");        
        selenium.clickAndWait("//input[@value='Next']");        
        assertTextPresent("Space Roles");        
        selenium.clickAndWait("//input[@value='Save']");        
        assertTextPresent("Users Allocated To Space");                
        selenium.clickAndWait("//input[@value='Allocate']");        
        assertTextPresent("Admin");             
    }  
    
    public void testCreateNewItem() throws Exception {        
        selenium.clickAndWait("link=DASHBOARD");
        assertTextPresent("Test Space");        
        selenium.clickAndWait("link=NEW");
        assertTextPresent("Summary");        
        selenium.type("summary", "Test Summary");
        selenium.type("detail", "Test Detail");
        selenium.select("hideAssignedTo:border:assignedTo", "Admin");        
        selenium.clickAndWait("//input[@value='Submit']");
        assertTextPresent("TEST-1");
    }

    public void testSearchAllContainsItem() throws Exception {        
        selenium.clickAndWait("link=SEARCH");
        assertTextPresent("Show History");        
        selenium.clickAndWait("//input[@value='Search']");
        assertTextPresent("1 Record Found");        
        selenium.clickAndWait("link=TEST-1");
        assertTextPresent("History");
    }
     
    public void testUpdateHistoryForItem() throws Exception {        
        selenium.select("status", "Closed");
        selenium.type("comment", "Test Comment");
        selenium.clickAndWait("//input[@value='Submit']");
        assertTextPresent("Test Comment");
        
    }

    public void testCreateNewUser() throws Exception {        
        selenium.clickAndWait("link=OPTIONS");                
        selenium.clickAndWait("link=Manage Users");
        assertTextPresent("Users and allocated Spaces");        
        selenium.clickAndWait("link=Create New User");
        assertTextPresent("User Details");        
        selenium.type("user.loginName", "testuser");
        selenium.type("user.name", "Test User");
        selenium.type("user.email", "foo@bar.com");
        selenium.clickAndWait("//input[@value='Submit']");      
        selenium.clickAndWait("//input[@value='Search']");
        assertTextPresent("Test User");    
    }
        
    public void testLogout() throws Exception {        
        selenium.clickAndWait("link=LOGOUT");
        assertTextPresent("Logout Successful");
        stopSelenium();        
    } 
    
}
