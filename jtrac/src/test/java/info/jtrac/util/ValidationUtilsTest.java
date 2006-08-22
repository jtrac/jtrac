package info.jtrac.util;

import junit.framework.TestCase;

public class ValidationUtilsTest extends TestCase {
    
    public void testUpperCase() {
        assertTrue(ValidationUtils.isAllUpperCase("ABCD"));
        assertTrue(ValidationUtils.isAllUpperCase("AB123CD"));
        assertFalse(ValidationUtils.isAllUpperCase("ABCD-ABCD"));
        assertFalse(ValidationUtils.isAllUpperCase("AB CD"));        
    }
    
    public void testLowerCase() {
        assertTrue(ValidationUtils.isAllLowerCase("abcd"));
        assertTrue(ValidationUtils.isAllLowerCase("abcd123"));
        assertFalse(ValidationUtils.isAllLowerCase("ab-cd"));
        assertFalse(ValidationUtils.isAllLowerCase("ab cd"));
    }
    
    public void testCamelDashCase() {
        assertTrue(ValidationUtils.isCamelDashCase("Abcd"));
        assertTrue(ValidationUtils.isCamelDashCase("Abcd-Efgh"));
        assertTrue(ValidationUtils.isCamelDashCase("Abcd-Efgh-Hijk"));
        assertFalse(ValidationUtils.isCamelDashCase("AbcdEfgh"));
        assertFalse(ValidationUtils.isCamelDashCase("Abcd123"));
        assertFalse(ValidationUtils.isCamelDashCase("8bcd"));
        assertFalse(ValidationUtils.isCamelDashCase("Ab-cd"));
        assertFalse(ValidationUtils.isCamelDashCase("Ab cd"));
    }
    
}
