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

import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.SpaceRole;
import info.jtrac.domain.User;
import info.jtrac.domain.UserRole;
import java.util.Date;
import java.util.LinkedHashSet;

import java.util.List;
import java.util.Random;


import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Jtrac Facade implementation
 * This is where all the business logic is
 * For data persistence this delegates to JtracDao
 */
public class JtracImpl implements Jtrac {
    
    private JtracDao dao;
    private PasswordEncoder passwordEncoder;
    
    public void setDao(JtracDao dao) {
        this.dao = dao;
    }
    
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    } 
    
    private final Log logger = LogFactory.getLog(getClass());
    
    /**
     * this has not been factored into the util package or a helper class
     * because it depends on the PasswordEncoder configured 
     */
    public String generatePassword() {
        byte[] ab = new byte[1];
        Random r = new Random();
        r.nextBytes(ab);
        return passwordEncoder.encodePassword(new String(ab), null).substring(24);
    }
    
    /**
     * this has not been factored into the util package or a helper class
     * because it depends on the PasswordEncoder configured 
     */    
    public String encodeClearText(String clearText) {
        return passwordEncoder.encodePassword(clearText, null);
    }
    
    public void storeItem(Item item) {
        History history = new History(item);
        Date now = new Date();
        if (item.getTimeStamp() == null) {
            item.setTimeStamp(now);
        }
        history.setTimeStamp(now);
        if (item.getHistory() == null) {
            item.setHistory(new LinkedHashSet<History>());
        }
        item.getHistory().add(history);
        dao.storeItem(item);
    }
    
    public Item loadItem(long id) {
        return dao.loadItem(id);
    }
    
    // =========  acegi UserDetailsService implementation ==========
    public UserDetails loadUserByUsername(String loginName) {
        List<User> users = null;
        if (loginName.indexOf("@") != -1) {
            users = dao.findUsersByEmail(loginName);
        } else {
            users = dao.findUsersByLoginName(loginName);
        }
        if (users.size() == 0) {
            throw new UsernameNotFoundException("User not found for '" + loginName + "'");
        }
        logger.debug("acegi: loadUserByUserName success for '" + loginName + "'");
        return users.get(0);
    }
    
    public User loadUser(int id) {
        return dao.loadUser(id);
    }
    
    public User loadUser(String loginName) {
        List<User> users = dao.findUsersByLoginName(loginName);
        if (users.size() == 0) {
            return null;
        }
        return users.get(0);
    }
  
    public void storeUser(User user) {                
        dao.storeUser(user);
    }
    
    public List<User> findAllUsers() {
        return dao.findAllUsers();
    }
    
    public List<UserRole> findUsersForSpace(int spaceId) {
        return dao.findUsersForSpace(spaceId);
    }   
    
    public List<User> findUnallocatedUsersForSpace(int spaceId) {
        List<User> users = findAllUsers();
        List<UserRole> userRoles = findUsersForSpace(spaceId);
        for(UserRole userRole : userRoles) {
            users.remove(userRole.getUser());
        }
        return users;
    }     
    
    public Space loadSpace(int id) {
        return dao.loadSpace(id);
    }
    
    public Space loadSpace(String prefixCode) {
        List<Space> spaces = dao.findSpacesByPrefixCode(prefixCode);
        if (spaces.size() == 0) {
            return null;
        }
        return spaces.get(0);
    }
    
    public void storeSpace(Space space) {
        dao.storeSpace(space);
    }
    
    public List<Space> findAllSpaces() {
        return dao.findAllSpaces();
    }
    
    public List<Space> findUnallocatedSpacesForUser(int userId) {
        List<Space> spaces = findAllSpaces();
        User user = loadUser(userId);
        for(SpaceRole spaceRole : user.getSpaceRoles()) {
            spaces.remove(spaceRole.getSpace());
        }
        return spaces;
    }
    
    public void storeMetadata(Metadata metadata) {
        dao.storeMetadata(metadata);
    }
    
    public Metadata loadMetadata(int id) {
        return dao.loadMetadata(id);
    }
    
}
