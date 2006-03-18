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

package info.jtrac;

import info.jtrac.domain.Metadata;
import info.jtrac.domain.Role;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;

import java.util.List;


import org.acegisecurity.userdetails.UserDetailsService;

/**
 * Jtrac main business interface (facade)
 */
public interface Jtrac extends UserDetailsService {
    
    void storeUser(User user);
    List<User> loadAllUsers();
    User loadUser(int id);
    User loadUser(String loginName);
    //========================================================
    Space loadSpace(int id);
    Space loadSpace(String prefixCode);
    void storeSpace(Space space);
    List<Space> loadAllSpaces();
    //========================================================
    void storeMetadata(Metadata metadata);
    Metadata loadMetadata(int id);
    
}
