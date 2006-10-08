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

import info.jtrac.domain.AbstractItem;
import info.jtrac.domain.Attachment;
import info.jtrac.domain.Config;
import info.jtrac.domain.Counts;
import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.SpaceSequence;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.lucene.IndexSearcher;
import info.jtrac.lucene.Indexer;
import info.jtrac.util.EmailUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

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
 * Jtrac Service Layer implementation
 * This is where all the business logic is
 * For data persistence this delegates to JtracDao
 */
public class JtracImpl implements Jtrac {
    
    private JtracDao dao;
    private PasswordEncoder passwordEncoder;
    private EmailUtils emailUtils;
    private Indexer indexer;
    private IndexSearcher indexSearcher;
    
    public void setDao(JtracDao dao) {
        this.dao = dao;
        // performs one time init on Spring assisted startup        
        initEmailUtils();
    }
    
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setIndexSearcher(IndexSearcher indexSearcher) {
        this.indexSearcher = indexSearcher;
    }

    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
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
    
    /**
     * initialize the email adapter
     */
    private void initEmailUtils() {
        String host = loadConfig("mail.server.host");
        if (host == null) {
            logger.warn("'mail.server.host' config is null, mail adapter not initialized");
            return;
        }
        String port = loadConfig("mail.server.port");       
        String url = loadConfig("jtrac.url.base");
        String from = loadConfig("mail.from");
        String prefix = loadConfig("mail.subject.prefix");
        String userName = loadConfig("mail.server.username");
        String password = loadConfig("mail.server.password");
        this.emailUtils = new EmailUtils(host, port, url, from, prefix, userName, password);
    }      
    
    //==========================================================================
    
    public void storeItem(Item item, Attachment attachment) {
        History history = new History(item);        
        if (attachment != null) {
            dao.storeAttachment(attachment);
            attachment.setFilePrefix(attachment.getId());
            item.add(attachment);
            history.setAttachment(attachment);
        }
        Date now = new Date();
        item.setTimeStamp(now);
        history.setTimeStamp(now);
        item.add(history);
        // SpaceSequence spaceSequence = item.getSpace().getSpaceSequence();
        // very important - have to do this to guarantee unique sequenceNum !
        // see HibernateJtracDao.storeSpaceSequence() for complete picture
        long ssId = item.getSpace().getSpaceSequence().getId();
        SpaceSequence spaceSequence = dao.loadSpaceSequence(ssId);
        item.setSequenceNum(spaceSequence.next());
        dao.storeSpaceSequence(spaceSequence);
        // this will at the moment execute unnecessary updates (bug in Hibernate handling of "version" property)
        // se http://opensource.atlassian.com/projects/hibernate/browse/HHH-1401
        dao.storeItem(item);
        indexer.index(item);
        indexer.index(history);
        if (item.isSendNotifications() && emailUtils != null) {
            emailUtils.send(item);
        }
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
        indexer.index(history);
        if (item.isSendNotifications() && emailUtils != null) {
            emailUtils.send(item);
        }
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
    
    public History loadHistory(long id) {
        return dao.loadHistory(id);
    }
    
    public List<Item> findItems(ItemSearch itemSearch) {
        String summary = itemSearch.getSummary();
        if (summary != null) {
            List<Long> hits = indexSearcher.findItemIdsContainingText(summary);
            if (hits.size() == 0) {
                itemSearch.setResultCount(0);
                return Collections.<Item>emptyList();
            }
            itemSearch.setItemIds(hits);            
        }
        return dao.findItems(itemSearch);
    }
    
    public int findItemCount(Space space, Field field) {
        return dao.findItemCount(space, field);
    }
    
    public int removeField(Space space, Field field) {
        return dao.removeField(space, field);
    }
    
    public int findItemCount(Space space, Field field, String optionKey) {
        return dao.findItemCount(space, field, Integer.parseInt(optionKey));
    }
    
    public int removeFieldValues(Space space, Field field, String optionKey) {
        return dao.removeFieldValues(space, field, Integer.parseInt(optionKey));
    }
    
    //========================================================    
    
    public void rebuildIndexes() {
        indexer.clearIndexes();
        List<AbstractItem> items = dao.findAllItems();
        for (AbstractItem item : items) {
            indexer.index(item);
        }
    }
    
    // =========  Acegi UserDetailsService implementation ==========
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
        User user = users.get(0);
        logger.debug("spaceRoles: " + user.getUserSpaceRoles());
        return user;
    }
    
    public User loadUser(long id) {
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
        String clearText = null;
        if (user.getId() == 0) {
            if (user.getPassword() == null) {
                clearText = generatePassword();
                user.setPassword(encodeClearText(clearText));
            } else {
                // password was provided by Admin.  Maybe e-mail is not available
                // we don't set clearText, so no email will be sent
                user.setPassword(encodeClearText(user.getPassword()));
            }            
            dao.storeUser(user);
        } else {
            // the User object passed in may be incomplete, just bound from an HTML form
            // load actual user from database, which retains for e.g. the spaceRoles
            User temp = loadUser(user.getId());
            // apply edits from the GUI
            temp.setEmail(user.getEmail());        
            temp.setLoginName(user.getLoginName());
            temp.setName(user.getName());
            if (user.getPassword() != null) {
                clearText = user.getPassword();
                temp.setPassword(encodeClearText(clearText));                
            }
            dao.storeUser(temp);
        }
        if (emailUtils != null && clearText != null) {                
            emailUtils.sendUserPassword(user, clearText);
        }
    }
    
    public List<User> findAllUsers() {
        return dao.findAllUsers();
    }
    
    public List<User> findUsersForSpace(long spaceId) {
        return dao.findUsersForSpace(spaceId);
    }      
    
    public List<UserSpaceRole> findUserRolesForSpace(long spaceId) {
        return dao.findUserRolesForSpace(spaceId);
    }   
    
    public List<User> findUsersForUser(User user) {
        Collection<Space> spaces = new HashSet<Space>(user.getUserSpaceRoles().size());
        for (UserSpaceRole usr : user.getUserSpaceRoles()) {
            spaces.add(usr.getSpace());
        }
        // must be a better way to make this unique?
        List<User> users = dao.findUsersForSpaceSet(spaces);
        Set<User> userSet = new LinkedHashSet<User>(users);
        return new ArrayList<User>(userSet);
    }     
    
    public List<User> findUnallocatedUsersForSpace(long spaceId) {
        List<User> users = findAllUsers();
        List<UserSpaceRole> userSpaceRoles = findUserRolesForSpace(spaceId);
        for(UserSpaceRole userSpaceRole : userSpaceRoles) {
            users.remove(userSpaceRole.getUser());
        }
        return users;
    }       
    
    //==========================================================================
    
    public Counts loadCountsForUser(User user) {
        return dao.loadCountsForUser(user);
    }            
    
    //==========================================================================
    
    public void storeUserSpaceRole(User user, Space space, String roleKey) {        
        user.addSpaceWithRole(space, roleKey);
        dao.storeUser(user);      
    }
    
    public void removeUserSpaceRole(UserSpaceRole userSpaceRole) {
        User user = userSpaceRole.getUser();
        user.removeSpaceWithRole(userSpaceRole.getSpace(), userSpaceRole.getRoleKey());
        // dao.storeUser(user);
        dao.removeUserSpaceRole(userSpaceRole);       
    }     
    
    public UserSpaceRole loadUserSpaceRole(long id) {
        return dao.loadUserSpaceRole(id);
    }
    
    public int renameSpaceRole(String oldRoleKey, String newRoleKey, Space space) {        
        return dao.renameSpaceRole(oldRoleKey, newRoleKey, space);        
    }
    
    //==========================================================================
    
    public Space loadSpace(long id) {
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
    
    public List<Space> findSpacesWhereGuestAllowed() {
        return dao.findSpacesWhereGuestAllowed();
    }
    
    public List<Space> findUnallocatedSpacesForUser(long userId) {
        List<Space> spaces = findAllSpaces();
        User user = loadUser(userId);
        for(UserSpaceRole usr : user.getUserSpaceRoles()) {
            spaces.remove(usr.getSpace());
        }
        return spaces;
    }
    
    public void storeMetadata(Metadata metadata) {
        dao.storeMetadata(metadata);
    }
    
    public Metadata loadMetadata(long id) {
        return dao.loadMetadata(id);
    }
    
    public Map<String, String> loadAllConfig() {
        List<Config> list = dao.findAllConfig();
        Map<String, String> allConfig = new HashMap<String, String>(list.size());
        for (Config c : list) {
            allConfig.put(c.getParam(), c.getValue());
        }
        return allConfig;
    }
    
    public void storeConfig(Config config) {
        dao.storeConfig(config);
        // ugly hack, TODO make smarter in future
        // email adapter to be re-initialized
        initEmailUtils();
    }
    
    public String loadConfig(String param) {
        Config config = dao.loadConfig(param);
        if (config == null) {
            return null;
        }
        String value = config.getValue();
        if (value == null || value.trim().equals("")) {
            return null;
        }
        return value;
    }
    
}
