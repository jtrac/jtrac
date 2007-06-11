package info.jtrac.selenium;

public class AllTest extends SeleniumTestCase {
        
    static {
        clazz = AllTest.class;
    }
    
    public AllTest(String name) {
        super(name);
    }
    
    public void testGetLoginPage() throws Exception {
        selenium.open("http://localhost:8080/jtrac/app/login");        
    }   
    
}
