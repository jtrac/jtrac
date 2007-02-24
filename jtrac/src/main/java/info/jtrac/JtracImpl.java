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
import info.jtrac.domain.CountsHolder;
import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemItem;
import info.jtrac.domain.ItemRefId;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.SpaceSequence;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.lucene.IndexSearcher;
import info.jtrac.lucene.Indexer;
import info.jtrac.mail.MailSender;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;


import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

/**
 * Jtrac Service Layer implementation
 * This is where all the business logic is
 * For data persistence this delegates to JtracDao
 */
public class JtracImpl implements Jtrac {
    
    private JtracDao dao;
    private PasswordEncoder passwordEncoder;
    private MailSender mailSender;
    private Indexer indexer;
    private IndexSearcher indexSearcher;
    private MessageSource messageSource;
    
    private Map<String, String> locales;
    private String defaultLocale;
    
    public void setLocaleList(String[] array) {
        locales = new LinkedHashMap<String, String>();
        for(String localeString : array) {
            Locale locale = StringUtils.parseLocaleString(localeString);
            locales.put(localeString, localeString + " - " + locale.getDisplayName());
        }
        logger.info("available locales configured " + locales);
    }
    
    public void setDao(JtracDao dao) {
        this.dao = dao;
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
    
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
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
    
    public Map<String, String> getLocales() {
        return locales;
    }
    
    public String getDefaultLocale() {
        return defaultLocale;
    }
    
    /**
     * this is automatically called by spring init-method hook on
     * startup, also called whenever config is edited to refresh
     */
    public void init() {
        
        Map<String, String> config = loadAllConfig();
        
        // initialize default locale
        String temp = config.get("locale.default");
        if (temp == null || !locales.containsKey(temp)) {
            logger.warn("invalid default locale configured = '" + temp + "', defaulting to 'en'");
            temp = "en";
        }
        logger.info("default locale set to '" + temp + "'");
        defaultLocale = temp;
        
        // initialize mail sender
        this.mailSender = new MailSender(config, messageSource, defaultLocale);
        
        
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
        if (item.isSendNotifications()) {
            mailSender.send(item, messageSource);
        }
    }
    
    public void storeHistoryForItem(Item item, History history, Attachment attachment) {
        // first apply edits onto item record before we change the item status
        // the item.getEditableFieldList routine depends on the current State of the item
        for(Field field : item.getEditableFieldList(history.getLoggedBy())) {
            Object value = history.getValue(field.getName());
            if (value != null) {
                item.setValue(field.getName(), value);
            }
        }
        if (history.getStatus() != null) {
            item.setStatus(history.getStatus());
            item.setAssignedTo(history.getAssignedTo()); // this may be null, when closing
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
        if (history.isSendNotifications()) {
            mailSender.send(item, messageSource);
        }
    }
    
    public Item loadItem(long id) {
        return dao.loadItem(id);
    }
    
    public Item loadItemByRefId(String refId) {
        ItemRefId itemRefId = new ItemRefId(refId); // throws runtime exception if invalid id
        List<Item> items = dao.findItems(itemRefId.getSequenceNum(), itemRefId.getPrefixCode());
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
    
    public void removeItem(Item item) {
        dao.removeItem(item);
    }
    
    public void removeItemItem(ItemItem itemItem) {
        dao.removeItemItem(itemItem);
    }
    
    public int loadCountOfRecordsHavingFieldNotNull(Space space, Field field) {
        return dao.loadCountOfRecordsHavingFieldNotNull(space, field);
    }
    
    public int bulkUpdateFieldToNull(Space space, Field field) {
        return dao.bulkUpdateFieldToNull(space, field);
    }
    
    public int loadCountOfRecordsHavingFieldWithValue(Space space, Field field, int optionKey) {
        return dao.loadCountOfRecordsHavingFieldWithValue(space, field, optionKey);
    }
    
    public int bulkUpdateFieldToNullForValue(Space space, Field field, int optionKey) {
        return dao.bulkUpdateFieldToNullForValue(space, field, optionKey);
    }
    
    public int loadCountOfRecordsHavingStatus(Space space, int status) {
        return dao.loadCountOfRecordsHavingStatus(space, status);
    }
    
    public int bulkUpdateStatusToOpen(Space space, int status) {
        return dao.bulkUpdateStatusToOpen(space, status);
    }
    
    public int bulkUpdateRenameSpaceRole(Space space, String oldRoleKey, String newRoleKey) {
        return dao.bulkUpdateRenameSpaceRole(space, oldRoleKey, newRoleKey);
    }
    
    public int bulkUpdateDeleteSpaceRole(Space space, String roleKey) {
        return dao.bulkUpdateDeleteSpaceRole(space, roleKey);
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
        logger.debug("loadUserByUserName success for '" + loginName + "'");
        User user = users.get(0);
        Map<Long, Boolean> map = new HashMap<Long, Boolean>();
        for(UserSpaceRole usr : user.getSpaceRoles()) {
            logger.debug("UserSpaceRole: " + usr);            
            // this is a hack, the effect of the next line would be to
            // override hibernate lazy loading and get the space and associated metadata.
            // since this only happens only once on authentication and simplifies a lot of
            // code later because the security principal is "fully prepared",
            // this is hopefully pardonable.  The downside is that there may be as many extra db hits
            // as there are spaces allocated for the user.  Hibernate caching should alleviate this
            usr.isAbleToCreateNewItem();
        }               
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
        dao.storeUser(user);
    }
    
    public void storeUser(User user, String password, boolean sendNotifications) {
        if (password == null) {
            password = generatePassword();
        }
        user.setPassword(encodeClearText(password));
        storeUser(user);
        if(sendNotifications) {
            mailSender.sendUserPassword(user, password);
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
    
    public List<User> findUsersWithRoleForSpace(long spaceId, String roleKey) {
        return dao.findUsersWithRoleForSpace(spaceId, roleKey);
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
    
    public CountsHolder loadCountsForUser(User user) {
        return dao.loadCountsForUser(user);
    }
    
    public Counts loadCountsForUserSpace(User user, Space space) {
        return dao.loadCountsForUserSpace(user, space);
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
    
    public void removeSpace(Space space) {
        logger.info("proceeding to delete space: " + space);
        dao.bulkUpdateDeleteSpaceRole(space, null);
        dao.bulkUpdateDeleteItemsForSpace(space);
        dao.removeSpace(space);
        logger.info("successfully deleted space");
    }
    
    //==========================================================================
    
    public void storeMetadata(Metadata metadata) {
        dao.storeMetadata(metadata);
    }
    
    public Metadata loadMetadata(long id) {
        return dao.loadMetadata(id);
    }
    
    //==========================================================================
    
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
        // init stuff that could have changed
        init();
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
    
    //========================================================
    
    public void rebuildIndexes() {
        indexer.clearIndexes();
        List<AbstractItem> items = dao.findAllItems();
        for (AbstractItem item : items) {
            indexer.index(item);
        }
    }
    
    public List<AbstractItem> findAllItems() {
        // this returns all Item and all History records for indexing
        return dao.findAllItems();
    }
    
    public void clearIndexes() {
        indexer.clearIndexes();
    }
    
    public void index(AbstractItem item) {
        indexer.index(item);
    }
    
    //==========================================================================
    
    public String getReleaseVersion() {
        return Version.VERSION;
    }
    
    public String getReleaseTimestamp() {
        return Version.TIMESTAMP;
    }
    
}
