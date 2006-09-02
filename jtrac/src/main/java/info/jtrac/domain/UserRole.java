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

/**
 * Class that exists purely to hold a name value pair of 
 * User <--> role, used when retrieving the list of Users mapped
 * to a Space and where the roleKey corresponding to each User is also
 * required.
 */
public class UserRole implements Serializable {
    
    private User user;
    private SpaceRole spaceRole;    
    
    public UserRole() {
        // zero arg constructor
    }
    
    public UserRole(User user, SpaceRole spaceRole) {
        this.user = user;
        this.spaceRole = spaceRole;
    }    
        
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public SpaceRole getSpaceRole() {
        return spaceRole;
    }

    public void setSpaceRole(SpaceRole spaceRole) {
        this.spaceRole = spaceRole;
    }
    
}