package info.jtrac.util;

import junit.framework.TestCase;

public class ValidationUtilsTest extends TestCase {
    
    public void testValidateSpaceKey() {
        assertTrue(ValidationUtils.isValidSpaceKey("ABCD"));
        assertTrue(ValidationUtils.isValidSpaceKey("AB123CD"));
        assertFalse(ValidationUtils.isValidSpaceKey("ABCD-ABCD"));
        assertFalse(ValidationUtils.isValidSpaceKey("AB CD"));        
    }
    
    public void testValidateRoleKey() {
        assertTrue(ValidationUtils.isValidRoleKey("ABCD"));
        assertTrue(ValidationUtils.isValidRoleKey("AB123CD"));
        assertFalse(ValidationUtils.isValidRoleKey("ABCD-ABCD"));
        assertFalse(ValidationUtils.isValidRoleKey("ABcD_ABCD"));
        assertFalse(ValidationUtils.isValidRoleKey("AB CD")); 
        assertTrue(ValidationUtils.isValidRoleKey("ABCD_EFG"));
        assertFalse(ValidationUtils.isValidRoleKey("AB1CDE2_"));
        assertFalse(ValidationUtils.isValidRoleKey("_ABCDEF"));
        assertTrue(ValidationUtils.isValidRoleKey("1ABCD3_EFG2"));        
    }    
    
    public void testValidateLoginName() {
        assertTrue(ValidationUtils.isValidLoginName("abcd"));
        assertTrue(ValidationUtils.isValidLoginName("abcd123"));
        assertTrue(ValidationUtils.isValidLoginName("ab-cd"));
        assertTrue(ValidationUtils.isValidLoginName("ab.cd"));
        assertTrue(ValidationUtils.isValidLoginName("ab_cd"));
        assertTrue(ValidationUtils.isValidLoginName("Ab-Cd"));
        assertTrue(ValidationUtils.isValidLoginName("ab@cd"));
        assertTrue(ValidationUtils.isValidLoginName("AB\\cd"));
        assertTrue(ValidationUtils.isValidLoginName("AB\\abc@def.com"));
        assertFalse(ValidationUtils.isValidLoginName("ab%cd"));
        assertFalse(ValidationUtils.isValidLoginName("ab:cd"));
        assertFalse(ValidationUtils.isValidLoginName("ab cd"));
    }
    
    public void testValidateStateName() {
        assertTrue(ValidationUtils.isValidStateName("Abcd"));
        assertTrue(ValidationUtils.isValidStateName("Abcd-Efgh"));
        assertTrue(ValidationUtils.isValidStateName("Abcd-Efgh-Hijk"));
        assertFalse(ValidationUtils.isValidStateName("AbcdEfgh"));
        assertFalse(ValidationUtils.isValidStateName("Abcd123"));
        assertFalse(ValidationUtils.isValidStateName("8bcd"));
        assertFalse(ValidationUtils.isValidStateName("Ab-cd"));
        assertFalse(ValidationUtils.isValidStateName("Ab cd"));
    }
    
}
