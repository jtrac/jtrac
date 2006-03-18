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
    
    public void testTitleCase() {
        assertTrue(ValidationUtils.isTitleCase("Abcd"));
        assertFalse(ValidationUtils.isTitleCase("Abcd123"));
        assertFalse(ValidationUtils.isTitleCase("8bcd"));
        assertFalse(ValidationUtils.isTitleCase("Ab-cd"));
        assertFalse(ValidationUtils.isTitleCase("Ab cd"));
    }
    
}
