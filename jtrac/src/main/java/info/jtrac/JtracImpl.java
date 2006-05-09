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

import info.jtrac.domain.Attachment;
import info.jtrac.domain.Config;
import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.SpaceRole;
import info.jtrac.domain.SpaceSequence;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import info.jtrac.domain.UserRole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


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
    
    public void storeItem(Item item, Attachment attachment) {
        History history = new History(item);        
        if (attachment != null) {
            dao.storeAttachment(attachment);
            attachment.setFilePrefix(attachment.getId());
            item.add(attachment);
            history.setAttachment(attachment);
        }
        Date now = new Date();
        if (item.getTimeStamp() == null) {
            item.setTimeStamp(now);
        }
        history.setTimeStamp(now);
        item.add(history);
        SpaceSequence spaceSequence = item.getSpace().getSpaceSequence();
        item.setSequenceNum(spaceSequence.next());
        dao.storeSpaceSequence(spaceSequence);
        dao.storeItem(item);
    }
    
    public void storeHistoryForItem(Item item, History history, Attachment attachment) {
        if (history.getStatus() != null) {
            item.setStatus(history.getStatus());
            if (history.getStatus() == State.CLOSED) {
                item.setAssignedTo(null);
                history.setAssignedTo(null);
            } else {
                if(history.getAssignedTo() != null) {
                    item.setAssignedTo(history.getAssignedTo());
                }
            }           
        }
        for(Field field : item.getEditableFieldList(history.getLoggedBy())) {
            Object value = history.getValue(field.getName());
            if (value != null) {
                item.setValue(field.getName(), value);
            }
        }
        item.setItemUsers(history.getItemUsers());
        history.setTimeStamp(new Date());
        if (attachment != null) {
            dao.storeAttachment(attachment);
            attachment.setFilePrefix(attachment.getId());
            item.add(attachment);
            history.setAttachment(attachment);
        }        
        item.add(history);
        dao.storeItem(item);
    }
    
    public Item loadItem(long id) {
        return dao.loadItem(id);
    }
    
    public Item loadItem(long sequenceNum, String prefixCode) {
        List<Item> items = dao.findItems(sequenceNum, prefixCode);
        if (items.size() == 0) {
            return null;
        }
        return items.get(0);
    }
    
    public List<Item> findItems(ItemSearch itemSearch) {
        return dao.findItems(itemSearch);
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
    
    public List<User> findUsersForSpace(int spaceId) {
        return dao.findUsersForSpace(spaceId);
        // return dao.findUsersForSpaceSet(Collections.singleton(dao.loadSpace(spaceId)));
    }      
    
    public List<UserRole> findUserRolesForSpace(int spaceId) {
        return dao.findUserRolesForSpace(spaceId);
    }   
    
    public List<User> findUsersForUser(User user) {
        Collection<Space> spaces = new HashSet<Space>(user.getSpaceRoles().size());
        for (SpaceRole sr : user.getSpaceRoles()) {
            spaces.add(sr.getSpace());
        }
        // must be a better way to make this unique?
        List<User> users = dao.findUsersForSpaceSet(spaces);
        Set<User> userSet = new HashSet<User>(users);
        return new ArrayList<User>(userSet);
    }     
    
    public List<User> findUnallocatedUsersForSpace(int spaceId) {
        List<User> users = findAllUsers();
        List<UserRole> userRoles = findUserRolesForSpace(spaceId);
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
    
    public Map<String, String> loadAllConfig() {
        List<Config> list = dao.findAllConfig();
        Map<String, String> allConfig = new HashMap<String, String>(list.size());
        for (Config c : list) {
            allConfig.put(c.getKey(), c.getValue());
        }
        return allConfig;
    }
    
    public void storeConfig(Config config) {
        dao.storeConfig(config);
    }
    
    public Config loadConfig(String key) {
        return dao.loadConfig(key);
    }
    
}
