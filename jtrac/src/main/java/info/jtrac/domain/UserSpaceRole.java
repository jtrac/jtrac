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
import org.acegisecurity.GrantedAuthority;

/**
 * Class that exists purely to hold a "ternary" mapping of 
 * user <--> space <--> role and is also persisted
 * the JTrac authorization (access control) scheme works as follows:
 * if space is null, that means that this is a "global" JTrac role
 * if space is not null, this role applies for the user to that
 * space, and the getAuthority() method used by Acegi returns the 
 * role key appended with "_" + spacePrefixCode
 */
public class UserSpaceRole implements GrantedAuthority, Serializable {
    
    private long id;
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
    
    public boolean isAbleToCreateNewItem() {
        if (space == null) {
            return false;
        }
        return user.getPermittedTransitions(space, State.NEW).size() > 0;
    }
    
    //======== ACEGI GrantedAuthority implementation =============
    
    public String getAuthority() {        
        if (space != null) {
            return roleKey + "_" + space.getPrefixCode();
        }
        return roleKey;
    }
    
    //=============================================================      
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }        
    
    public Space getSpace() {
        return space;
    }
    
    public void setSpace(Space space) {
        this.space = space;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        return (space == usr.getSpace() || space.equals(usr.getSpace())
            && user.equals(usr.getUser())
            && roleKey.equals(usr.getRoleKey()));
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 31 + user.hashCode();
        hash = hash * 31 + (space == null ? 0 : space.hashCode());        
        hash = hash * 31 + roleKey.hashCode();
        return hash;
    } 
    
    @Override
    public String toString() {
        return getAuthority();
    }
    
}
