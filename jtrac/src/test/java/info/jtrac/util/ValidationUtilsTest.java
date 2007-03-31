package info.jtrac.util;

import junit.framework.TestCase;

public class ValidationUtilsTest extends TestCase {
    
    public void testUpperCase() {
        assertTrue(ValidationUtils.isAllUpperCase("ABCD"));
        assertTrue(ValidationUtils.isAllUpperCase("AB123CD"));
        assertFalse(ValidationUtils.isAllUpperCase("ABCD-ABCD"));
        assertFalse(ValidationUtils.isAllUpperCase("AB CD"));        
    }
    
    public void testValidLoginName() {
        assertTrue(ValidationUtils.isValidLoginName("abcd"));
        assertTrue(ValidationUtils.isValidLoginName("abcd123"));
        assertTrue(ValidationUtils.isValidLoginName("ab-cd"));
        assertTrue(ValidationUtils.isValidLoginName("ab.cd"));
        assertTrue(ValidationUtils.isValidLoginName("ab_cd"));
        assertTrue(ValidationUtils.isValidLoginName("Ab-Cd"));
        assertFalse(ValidationUtils.isValidLoginName("ab%cd"));
        assertFalse(ValidationUtils.isValidLoginName("ab:cd"));
        assertFalse(ValidationUtils.isValidLoginName("ab cd"));
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
