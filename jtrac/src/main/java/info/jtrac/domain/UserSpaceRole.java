/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.jtrac.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.acegisecurity.GrantedAuthority;

/**
 * Class that exists purely to hold a "ternary" mapping of 
 * user <--> space <--> role.
 * the jTrac authorization (access control) scheme works as follows:
 * if space is null, that means that this is a "global" jTrac role
 * if space is not null, this role applies for the user to that
 * space, and the getAuthority() method used by Acegi returns the 
 * role key appended with "_" + spacePrefixCode
 */
public class UserSpaceRole implements GrantedAuthority, Serializable {
    
    private int id;
    private User user;
    private Space space;
    private String roleKey;    
    
    public UserSpaceRole() {
        // zero arg constructor
    }
    
    public UserSpaceRole(User user, Space space, String roleKey) {
        this.user = user;
        this.space = space;
        this.roleKey = roleKey;
    }    
    
    //======== ACEGI GrantedAuthority implementation =============
    
    public String getAuthority() {        
        if (space != null) {
            return roleKey + "_" + space.getPrefixCode();
        }
        return roleKey;
    }
    
    //=============================================================      
    
    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }        
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }    
    
    public Space getSpace() {
        return space;
    }
    
    public void setSpace(Space space) {
        this.space = space;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id [").append(id);
        sb.append("]; user [").append(user);
        sb.append("]; space [").append(space);
        sb.append("]; roleKey [").append(roleKey).append("]");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserSpaceRole)) {
            return false;
        }
        final UserSpaceRole usr = (UserSpaceRole) o;
        // user can never be null, space can be null
        return ( user.equals(usr.getUser()) 
            && ( space == usr.getSpace() || space.equals(usr.getSpace()) )
            && roleKey.equals(usr.getRoleKey()) );
    }
    
    @Override
    public int hashCode() {
        System.out.println("*** + hashCode called: " + toString());
        int hash = 7;
        hash = hash * 31 + user.hashCode();
        hash = hash * 31 + (space == null ? 0 : space.hashCode());
        hash = hash * 31 + roleKey.hashCode();
        return hash;
    } 
    
}
