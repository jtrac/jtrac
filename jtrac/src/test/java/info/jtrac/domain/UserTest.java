package info.jtrac.domain;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        u.addSpaceRole(null, "ROLE_ADMIN");
        
        GrantedAuthority[] gas = u.getAuthorities();
        
        Set<String> set = new HashSet<String>();
        for(GrantedAuthority ga : gas) {
            set.add(ga.getAuthority());
        }        
        
        assertEquals(4, gas.length);
        
        assertTrue(set.contains("ROLE_USER"));
        assertTrue(set.contains("ROLE_ONE-ONE_SPACE-ONE"));
        assertTrue(set.contains("ROLE_ONE-TWO_SPACE-ONE"));
        assertTrue(set.contains("ROLE_ADMIN"));
     
    }
    
}
