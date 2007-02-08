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

package info.jtrac.hibernate;

import info.jtrac.JtracDao;
import info.jtrac.domain.AbstractItem;
import info.jtrac.domain.Attachment;
import info.jtrac.domain.Config;
import info.jtrac.domain.Field;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.SpaceSequence;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import info.jtrac.domain.Counts;
import info.jtrac.domain.CountsHolder;
import info.jtrac.domain.History;
import info.jtrac.domain.ItemItem;
import info.jtrac.domain.UserSpaceRole;
import java.util.Collection;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateJdbcException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * DAO Implementation using Spring Hibernate template
 * note usage of the Spring "init-method" and "destroy-method" options
 */
public class HibernateJtracDao extends HibernateDaoSupport implements JtracDao {
    
    private final Log logger = LogFactory.getLog(getClass());
    
    private SchemaHelper schemaHelper;
    
    public void setSchemaHelper(SchemaHelper schemaHelper) {
        this.schemaHelper = schemaHelper;
    }
    
    public void storeItem(Item item) {
        getHibernateTemplate().merge(item);
    }
    
    public Item loadItem(long id) {
        return (Item) getHibernateTemplate().get(Item.class, id);
    }
    
    public History loadHistory(long id) {
        return (History) getHibernateTemplate().get(History.class, id);
    }
    
    public List<Item> findItems(long sequenceNum, String prefixCode) {
        Object[] params = new Object[] { sequenceNum, prefixCode };
        return getHibernateTemplate().find("from Item item where item.sequenceNum = ? and item.space.prefixCode = ?", params);
    }
    
    public List<Item> findItems(ItemSearch itemSearch) {
        int pageSize = itemSearch.getPageSize();
        if (pageSize == -1) {
            List<Item> list = getHibernateTemplate().findByCriteria(itemSearch.getCriteria());
            itemSearch.setResultCount(list.size());
            return list;
        } else {
            // pagination
            int firstResult = pageSize * itemSearch.getCurrentPage();
            List<Item> list = getHibernateTemplate().findByCriteria(itemSearch.getCriteria(), firstResult, pageSize);
            DetachedCriteria criteria = itemSearch.getCriteriaForCount();
            criteria.setProjection(Projections.rowCount());
            Integer count = (Integer) getHibernateTemplate().findByCriteria(criteria).get(0);
            itemSearch.setResultCount(count);
            return list;
        }
    }
    
    public List<AbstractItem> findAllItems() {
        return getHibernateTemplate().loadAll(AbstractItem.class);
    }
    
    public void removeItem(Item item) {
        getHibernateTemplate().delete(item);
    }    
    
    public void removeItemItem(ItemItem itemItem) {
        getHibernateTemplate().delete(itemItem);
    }    
    
    public void storeAttachment(Attachment attachment) {
        getHibernateTemplate().merge(attachment);
    }
    
    public void storeMetadata(Metadata metadata) {
        getHibernateTemplate().merge(metadata);
    }
    
    public Metadata loadMetadata(long id) {
        return (Metadata) getHibernateTemplate().get(Metadata.class, id);
    }
    
    public void storeSpace(Space space) {
        getHibernateTemplate().merge(space);
    }
    
    public Space loadSpace(long id) {
        return (Space) getHibernateTemplate().get(Space.class, id);
    }
    
    public UserSpaceRole loadUserSpaceRole(long id) {
        return (UserSpaceRole) getHibernateTemplate().get(UserSpaceRole.class, id);
    }    
    
    public SpaceSequence loadSpaceSequence(long id) {
        return (SpaceSequence) getHibernateTemplate().get(SpaceSequence.class, id);
    }
    
    public void storeSpaceSequence(SpaceSequence spaceSequence) {
        getHibernateTemplate().merge(spaceSequence);
        // very important, needed to guarantee unique sequenceNum on item insert !
        // see JtracImpl.storeItem() for complete picture
        getHibernateTemplate().flush();
    }
    
    public List<Space> findSpacesByPrefixCode(String prefixCode) {
        return getHibernateTemplate().find("from Space space where space.prefixCode = ?", prefixCode);
    }
    
    public List<Space> findAllSpaces() {
        return getHibernateTemplate().find("from Space space order by space.prefixCode");
    }
    
    public List<Space> findSpacesWhereGuestAllowed() {
        // forcing eager load of metadata, this is used for populating guest user security principal
        // return getHibernateTemplate().find("from Space space where space.guestAllowed = true");
        return (List<Space>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(Space.class);
                criteria.setFetchMode("metadata", FetchMode.JOIN);
                criteria.add(Restrictions.eq("guestAllowed", true));                
                return criteria.list();
            }
        });        
    }
    
    public void removeSpace(Space space) {        
        getHibernateTemplate().delete(space);
    }    
    
    public void storeUser(User user) {
        getHibernateTemplate().merge(user);
    }
    
    public User loadUser(long id) {
        return (User) getHibernateTemplate().get(User.class, id);
    }
    
    public List<User> findAllUsers() {
        return getHibernateTemplate().find("from User user order by user.name");
    }
    
    public List<User> findUsersByLoginName(String loginName) {
        return getHibernateTemplate().find("from User user where user.loginName = ?", loginName);
    }
    
    public List<User> findUsersByEmail(String email) {
        return getHibernateTemplate().find("from User user where user.email = ?", email);
    }
    
    public List<UserSpaceRole> findUserRolesForSpace(final long spaceId) {        
        // return getHibernateTemplate().find("select usr from User user" +
        //      " join user.userSpaceRoles as usr where usr.space.id = ? order by user.name", spaceId);
        // same as above commented, but forcing eager load of user object
        // done this way to avoid n + 1 selects on item / item_view
        // screens when rendering users to notify, drop down etc.
        return (List<UserSpaceRole>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(UserSpaceRole.class);
                criteria.setFetchMode("user", FetchMode.JOIN);
                criteria.add(Restrictions.eq("space.id", spaceId));
                criteria.createCriteria("user").addOrder(Order.asc("name"));
                return criteria.list();
            }
        });        
    }
    
    public List<User> findUsersWithRoleForSpace(long spaceId, String roleKey) {
        return getHibernateTemplate().find("from User user"
                + " join user.userSpaceRoles as usr where usr.space.id = ?"
                + " and usr.roleKey = ? order by user.name", new Object[] { spaceId, roleKey });        
    }    
    
    //==========================================================================    
    
    public CountsHolder loadCountsForUser(User user) {
        Collection<Space> spaces = user.getSpaces();
        if (spaces.size() == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append('(');
        for (Space s : spaces) {
            sb.append(s.getId());
            sb.append(',');
        }
        sb.setCharAt(sb.length() - 1, ')');
        CountsHolder ch = new CountsHolder();
        HibernateTemplate ht = getHibernateTemplate();        
        List<Object[]> loggedByList = ht.find("select item.space.id, count(item) from Item item" 
                + " where item.loggedBy.id = ? group by item.space.id", user.getId());
        List<Object[]> assignedToList = ht.find("select item.space.id, count(item) from Item item" 
                + " where item.assignedTo.id = ? group by item.space.id", user.getId());
        List<Object[]> statusList = ht.find("select item.space.id, count(item) from Item item" 
                + " where item.space.id in " + sb.toString() + " group by item.space.id");
        for(Object[] oa : loggedByList) {
            ch.add((Long) oa[0], Counts.LOGGED_BY_ME, (Integer) oa[1]);
        }
        for(Object[] oa : assignedToList) {
            ch.add((Long) oa[0], Counts.ASSIGNED_TO_ME, (Integer) oa[1]);
        }
        for(Object[] oa : statusList) {
            ch.add((Long) oa[0], Counts.TOTAL, (Integer) oa[1]);
        }
        return ch;
    }
    
    public Counts loadCountsForUserSpace(User user, Space space) {
        HibernateTemplate ht = getHibernateTemplate();        
        List<Object[]> loggedByList = ht.find("select status, count(item) from Item item" 
                + " where item.loggedBy.id = ? and item.space.id = ? group by item.status", new Object[] { user.getId(), space.getId() });
        List<Object[]> assignedToList = ht.find("select status, count(item) from Item item" 
                + " where item.assignedTo.id = ? and item.space.id = ? group by item.status", new Object[] { user.getId(), space.getId() });
        List<Object[]> statusList = ht.find("select status, count(item) from Item item" 
                + " where item.space.id = ? group by item.status", space.getId());
        Counts c = new Counts(true);
        for(Object[] oa : loggedByList) {
            c.add(Counts.LOGGED_BY_ME, (Integer) oa[0], (Integer) oa[1]);
        }
        for(Object[] oa : assignedToList) {
            c.add(Counts.ASSIGNED_TO_ME, (Integer) oa[0], (Integer) oa[1]);
        }
        for(Object[] oa : statusList) {
            c.add(Counts.TOTAL, (Integer) oa[0], (Integer) oa[1]);
        }
        return c;
    }
    
    //==========================================================================
    
    public List<User> findUsersForSpace(long spaceId) {
        return getHibernateTemplate().find("select distinct user from User user join user.userSpaceRoles as usr" 
                + " where usr.space.id = ? order by user.name", spaceId);
    }
    
    public List<User> findUsersForSpaceSet(Collection<Space> spaces) {
        Criteria criteria = getSession().createCriteria(User.class);
        criteria.createCriteria("userSpaceRoles").add(Restrictions.in("space", spaces));
        criteria.addOrder(Order.asc("name"));
        return criteria.list();
    }
    
    public void removeUserSpaceRole(UserSpaceRole userSpaceRole) {        
        getHibernateTemplate().delete(userSpaceRole);
    }
    
    public List<Config> findAllConfig() {
        return getHibernateTemplate().loadAll(Config.class);
    }
    
    public void storeConfig(Config config) {
        getHibernateTemplate().merge(config);
    }
    
    public Config loadConfig(String param) {
        return (Config) getHibernateTemplate().get(Config.class, param);
    }

    public int loadCountOfRecordsHavingFieldNotNull(Space space, Field field) {
        Criteria criteria = getSession().createCriteria(Item.class);
        criteria.add(Restrictions.eq("space", space));
        criteria.add(Restrictions.isNotNull(field.getName().toString()));
        criteria.setProjection(Projections.rowCount());
        int itemCount = (Integer) criteria.list().get(0);
        // even when no item has this field not null currently, items may have history with this field not null
        // because of the "parent" difference, cannot use AbstractItem and have to do a separate Criteria query
        criteria = getSession().createCriteria(History.class);
        criteria.createCriteria("parent").add(Restrictions.eq("space", space));
        criteria.add(Restrictions.isNotNull(field.getName().toString()));
        criteria.setProjection(Projections.rowCount());
        return itemCount + (Integer) criteria.list().get(0);        
    }

    public int bulkUpdateFieldToNull(Space space, Field field) {
        int itemCount = getHibernateTemplate().bulkUpdate("update Item item set item." + field.getName() + " = null" 
                + " where item.space.id = ?", space.getId());
        logger.info("no of Item rows where " + field.getName() + " set to null = " + itemCount);
        int historyCount = getHibernateTemplate().bulkUpdate("update History history set history." + field.getName() + " = null"
                + " where history.parent in ( from Item item where item.space.id = ? )", space.getId());
        logger.info("no of History rows where " + field.getName() + " set to null = " + historyCount);
        return itemCount;
    }

    public int loadCountOfRecordsHavingFieldWithValue(Space space, Field field, int optionKey) {
        Criteria criteria = getSession().createCriteria(Item.class);
        criteria.add(Restrictions.eq("space", space));
        criteria.add(Restrictions.eq(field.getName().toString(), optionKey));
        criteria.setProjection(Projections.rowCount());
        int itemCount = (Integer) criteria.list().get(0);
        // even when no item has this field value currently, items may have history with this field value
        // because of the "parent" difference, cannot use AbstractItem and have to do a separate Criteria query
        criteria = getSession().createCriteria(History.class);
        criteria.createCriteria("parent").add(Restrictions.eq("space", space));
        criteria.add(Restrictions.eq(field.getName().toString(), optionKey));
        criteria.setProjection(Projections.rowCount());
        return itemCount + (Integer) criteria.list().get(0);        
    }

    public int bulkUpdateFieldToNullForValue(Space space, Field field, int optionKey) {
        int itemCount = getHibernateTemplate().bulkUpdate("update Item item set item." + field.getName() + " = null" 
                + " where item.space.id = ?"
                + " and item." + field.getName() + " = ?", new Object[] { space.getId(), optionKey });
        logger.info("no of Item rows where " + field.getName() + " value '" + optionKey + "' replaced with null = " + itemCount);
        int historyCount = getHibernateTemplate().bulkUpdate("update History history set history." + field.getName() + " = null"
                + " where history." + field.getName() + " = ?"
                + " and history.parent in ( from Item item where item.space.id = ? )", new Object[] { optionKey, space.getId(), });
        logger.info("no of History rows where " + field.getName() + " value '" + optionKey + "' replaced with null = " + historyCount);
        return itemCount;        
    }
    
    public int loadCountOfRecordsHavingStatus(Space space, int status) {
        Criteria criteria = getSession().createCriteria(Item.class);
        criteria.add(Restrictions.eq("space", space));
        criteria.add(Restrictions.eq("status", status));
        criteria.setProjection(Projections.rowCount());
        int itemCount = (Integer) criteria.list().get(0);
        // even when no item has this status currently, items may have history with this status
        // because of the "parent" difference, cannot use AbstractItem and have to do a separate Criteria query
        criteria = getSession().createCriteria(History.class);
        criteria.createCriteria("parent").add(Restrictions.eq("space", space));
        criteria.add(Restrictions.eq("status", status));
        criteria.setProjection(Projections.rowCount());
        return itemCount + (Integer) criteria.list().get(0);
    }    
    
    public int bulkUpdateStatusToOpen(Space space, int status) {
        int itemCount = getHibernateTemplate().bulkUpdate("update Item item set item.status = " + State.OPEN 
                + " where item.status = ? and item.space.id = ?", new Object[] { status, space.getId() });
        logger.info("no of Item rows where status changed from " + status + " to " + State.OPEN + " = " + itemCount);
        int historyCount = getHibernateTemplate().bulkUpdate("update History history set history.status = " + State.OPEN 
                + " where history.status = ?"
                + " and history.parent in ( from Item item where item.space.id = ? )", new Object[] { status, space.getId() });
        logger.info("no of History rows where status changed from " + status + " to " + State.OPEN + " = " + historyCount);
        return itemCount;
    }    
    
    public int bulkUpdateRenameSpaceRole(Space space, String oldRoleKey, String newRoleKey) {
        return getHibernateTemplate().bulkUpdate("update UserSpaceRole usr set usr.roleKey = ?"
                + " where usr.roleKey = ? and usr.space.id = ?", new Object[] { newRoleKey, oldRoleKey, space.getId() });
    }
    
    public int bulkUpdateDeleteSpaceRole(Space space, String roleKey) {
        if (roleKey == null) {
            return getHibernateTemplate().bulkUpdate("delete UserSpaceRole usr where usr.space.id = ?", space.getId());            
        } else {
            return getHibernateTemplate().bulkUpdate("delete UserSpaceRole usr"
                    + " where usr.space.id = ? and usr.roleKey = ?", new Object[] { space.getId(), roleKey });
        }
    }
    
    public int bulkUpdateDeleteItemsForSpace(Space space) {
        int historyCount = getHibernateTemplate().bulkUpdate("delete History history where history.parent in"
                + " ( from Item item where item.space.id = ? )", space.getId());
        logger.debug("deleted " + historyCount + " records from history");
        int itemItemCount = getHibernateTemplate().bulkUpdate("delete ItemItem itemItem where itemItem.item in"
                + " ( from Item item where item.space.id = ? )", space.getId());
        logger.debug("deleted " + itemItemCount + " records from item_items");
        int itemCount = getHibernateTemplate().bulkUpdate("delete Item item where item.space.id = ?", space.getId());
        logger.debug("deleted " + itemCount + " records from items");
        return historyCount + itemItemCount + itemCount;
    }
    
    //==========================================================================
    
    /**
     * note that this is automatically configured to run on startup 
     * as a spring bean "init-method"
     */
    public void createSchema() {
        try {
            getHibernateTemplate().find("from Item item where item.id = 1");
        } catch (HibernateJdbcException e) {
            logger.warn("database schema not found, proceeding to create");
            schemaHelper.createSchema();
            User admin = new User();
            admin.setLoginName("admin");
            admin.setName("Admin");
            admin.setEmail("admin");
            admin.setPassword("21232f297a57a5a743894a0e4a801fc3");
            admin.addSpaceWithRole(null, "ROLE_ADMIN");
            logger.info("inserting default admin user into database");
            storeUser(admin);
            logger.info("schema creation complete");
            return;
        }
        logger.info("database schema exists, normal startup");
    }   
    
    /**
     * note that this is automatically configured to run on context shutdown 
     * as a spring bean "destroy-method"
     */
    public void stopEmbeddedDb() throws Exception {
        schemaHelper.stopEmbeddedDb();
    }
    
}
