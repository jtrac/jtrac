package info.jtrac.domain;


import info.jtrac.domain.Space;
import info.jtrac.domain.User;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.acegisecurity.GrantedAuthority;

public class UserTest extends TestCase {    
    
    public void testGetAuthoritiesFromUserSpaceRoles() {      
        
        Space s1 = new Space();
        s1.setPrefixCode("SPACE-ONE");                             

        User u = new User();
        u.setLoginName("test");        
        
        u.addSpaceRole(s1, "ROLE_ONE-ONE");
        u.addSpaceRole(s1, "ROLE_ONE-TWO");
        
        GrantedAuthority[] gas = u.getAuthorities();
        assertEquals(3, gas.length);
        assertEquals("ROLE_USER", gas[0].getAuthority());
        assertEquals("ROLE_ONE-ONE_SPACE-ONE", gas[1].getAuthority());
        assertEquals("ROLE_ONE-TWO_SPACE-ONE", gas[2].getAuthority());
     
    }
    
}
