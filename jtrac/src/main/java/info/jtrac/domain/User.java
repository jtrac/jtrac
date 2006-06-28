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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.UserDetails;

/**
 * Standard User entity with attributes such as name, password etc.
 * The parent relationship is used for easy grouping of users and
 * flexible inheritance of permission schemes.  The user type
 * determines if this is a normal user or a user group.  Only
 * user groups can have child references.
 *
 * We also tie in to the Acegi security framework and implement
 * the Acegi UserDetails interface so that Acegi can take care
 * of Authentication and Authorization
 */
public class User implements UserDetails, Serializable {
    
    private int id;
    private Integer type;
    private User parent;
    private String loginName;
    private String name;
    private String password;
    private String email;
    private Metadata metadata;
    private boolean locked;
    private Set<SpaceRole> spaceRoles = new HashSet<SpaceRole>();
    
    //=============================================================
   
    public void addSpaceRole(Space space, String roleKey) {
        spaceRoles.add(new SpaceRole(space, roleKey));
    }
    
    public void removeSpace(Space space) {
        Set<SpaceRole> remove = new HashSet<SpaceRole>();
        for(SpaceRole sr : spaceRoles) {
            if (sr.getSpace().equals(space)) {
                remove.add(sr);
            }
        }
        spaceRoles.removeAll(remove);
    }
    
    private List<String> getRoleKeys(Space space) {
        List<String> roleKeys = new ArrayList<String>();
        for(SpaceRole sr : spaceRoles) {
            if (sr.getSpace() != null && sr.getSpace().equals(space)) {
                roleKeys.add(sr.getRoleKey());
            }
        }
        return roleKeys;
    }
    
    public Map<Integer, String> getPermittedTransitions(Space space, int status) {
        return space.getMetadata().getPermittedTransitions(getRoleKeys(space), status);
    }
    
    public List<Field> getEditableFieldList(Space space, int status) {
        return space.getMetadata().getEditableFields(getRoleKeys(space), Collections.singletonList(status));
    }
    
    public Set<Space> getSpaces() {
        Set<Space> spaces = new HashSet<Space>(spaceRoles.size());
        for (SpaceRole sr : spaceRoles) {
            if (sr.getSpace() != null) {
                spaces.add(sr.getSpace());
            }
        }
        return spaces;
    }
    
    public int getSpaceCount() {
        return getSpaces().size();
    }
    
    //============ ACEGI UserDetails implementation ===============
    
    public boolean isAccountNonExpired() {
        return true;
    }
    
    public boolean isAccountNonLocked() {
        return !isLocked();
    }
    
    public GrantedAuthority[] getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SpaceRole(null, "ROLE_USER"));
        for (SpaceRole sr : spaceRoles) {            
            authorities.add(sr);
        }
        return authorities.toArray(new GrantedAuthority[authorities.size()]);
    }
    
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public String getUsername() {
        return getLoginName();
    }
    
    public String getPassword() {
        return password;
    }
    
    //=============================================================
    
    public Set<SpaceRole> getSpaceRoles() {
        return spaceRoles;
    }

    public void setSpaceRoles(Set<SpaceRole> spaceRoles) {
        this.spaceRoles = spaceRoles;
    }    
    
    public User getParent() {
        return parent;
    }
    
    public void setParent(User parent) {
        this.parent = parent;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    
    public boolean isLocked() {
        return locked;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    public Metadata getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Integer getType() {
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public String getLoginName() {
        return loginName;
    }
    
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id [").append(id);
        sb.append("]; loginName [").append(loginName);
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        final User u = (User) o;
        return u.getLoginName().equals(loginName);
    }
    
    @Override
    public int hashCode() {
        return loginName.hashCode();
    }
    
}
