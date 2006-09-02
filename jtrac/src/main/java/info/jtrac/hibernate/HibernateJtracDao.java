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
import info.jtrac.domain.UserRole;
import info.jtrac.domain.Counts;
import info.jtrac.domain.History;
import info.jtrac.domain.SpaceRole;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * DAO Implementation using Spring Hibernate template
 */
public class HibernateJtracDao
        extends HibernateDaoSupport implements JtracDao {
    
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
    
    public SpaceRole loadSpaceRole(long id) {
        return (SpaceRole) getHibernateTemplate().get(SpaceRole.class, id);
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
    
    public void storeUser(User user) {
        getHibernateTemplate().merge(user);
    }
    
    public User loadUser(long id) {
        return (User) getHibernateTemplate().get(User.class, id);
    }
    
    public List<User> findAllUsers() {
        return getHibernateTemplate().find("from User user order by user.name");
    }
    
    public List<User> findUsersByLoginName(final String loginName) {
        // using criteria query to override lazy loading during authentication
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                return session.createCriteria(User.class).setFetchMode(
                        "spaceRoles", FetchMode.JOIN).add(
                        Restrictions.eq("loginName", loginName)).list();
            }
        });
    }
    
    public List<User> findUsersByEmail(final String email) {
        // using criteria query to override lazy loading during authentication
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                return session.createCriteria(User.class).setFetchMode(
                        "spaceRoles", FetchMode.JOIN).add(
                        Restrictions.eq("email", email)).list();
            }
        });
    }
    
    public List<UserRole> findUserRolesForSpace(long spaceId) {
        List<Object[]> rawList = getHibernateTemplate().find("select user, spaceRole from User user" +
                " join user.spaceRoles as spaceRole where spaceRole.space.id = ? order by user.name", spaceId);
        List<UserRole> userRoles = new ArrayList<UserRole>();
        for (Object[] userRole : rawList) {
            User user = (User) userRole[0];
            SpaceRole sr = (SpaceRole) userRole[1];
            userRoles.add(new UserRole(user, sr));
        }
        return userRoles;
    }
    
    public Counts loadCountsForUser(User user) {
        Set<Space> spaces = user.getSpaces();
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
        Counts c = new Counts();
        HibernateTemplate ht = getHibernateTemplate();
        List<Object[]> loggedByList = ht.find("select item.space.id, count(item) from Item item" +
                " where item.loggedBy.id = ? group by item.space.id", user.getId());
        List<Object[]> assignedToList = ht.find("select item.space.id, count(item) from Item item" +
                " where item.assignedTo.id = ? group by item.space.id", user.getId());
        List<Object[]> statusList = ht.find("select item.space.id, item.status, count(item) from Item item" +
                " where item.space.id in " + sb.toString() + " group by item.space.id, item.status");
        for(Object[] oa : loggedByList) {
            c.addLoggedBy((Long) oa[0], (Integer) oa[1]);
        }
        for(Object[] oa : assignedToList) {
            c.addAssignedTo((Long) oa[0], (Integer) oa[1]);
        }
        for(Object[] oa : statusList) {
            int i = (Integer) oa[1];
            if (i == State.CLOSED) {
                c.addClosed((Long) oa[0], (Integer) oa[2]);
            } else {
                c.addOpen((Long) oa[0], (Integer) oa[2]);
            }
        }
        return c;
    }
        
    public List<User> findUsersForSpace(long spaceId) {
        return getHibernateTemplate().find("select distinct user from User user join user.spaceRoles as spaceRole" 
                + " where spaceRole.space.id = ? order by user.name", spaceId);
    }
    
    public List<User> findUsersForSpaceSet(Collection<Space> spaces) {
        Criteria criteria = getSession().createCriteria(User.class);
        criteria.createCriteria("spaceRoles").add(Restrictions.in("space", spaces));
        criteria.addOrder(Order.asc("name"));
        return criteria.list();
    }
    
    public void removeSpaceRole(SpaceRole spaceRole) {        
        getHibernateTemplate().delete(spaceRole);
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
    
    public void createSchema() {
        try {
            getHibernateTemplate().find("from Item item where item.id = 1");
        } catch (BadSqlGrammarException e) {
            logger.warn("database schema not found, proceeding to create");
            schemaHelper.createSchema();
            User user = new User();
            user.setLoginName("admin");
            user.setName("Admin User");
            user.setEmail("jtrac.admin");
            user.setPassword("21232f297a57a5a743894a0e4a801fc3");
            user.addSpaceRole(null, "ROLE_ADMIN");
            logger.info("inserting default admin user into database");
            storeUser(user);
            logger.info("schema creation complete");
            return;
        }
        logger.info("database schema exists, normal startup");
    }

    public int findItemCount(Space space, Field field) {
        Criteria criteria = getSession().createCriteria(Item.class);
        criteria.add(Restrictions.eq("space", space));
        criteria.add(Restrictions.isNotNull(field.getName().toString()));
        criteria.setProjection(Projections.rowCount());
        return (Integer) criteria.list().get(0);         
    }

    public int removeField(Space space, Field field) {
        int itemCount = getHibernateTemplate().bulkUpdate("update Item item set item." + field.getName() + " = null" 
                + " where item.space.id = ?", space.getId());
        logger.info("no of Item rows where " + field.getName() + " set to null = " + itemCount);
        int historyCount = getHibernateTemplate().bulkUpdate("update History history set history." + field.getName() + " = null"
                + " where history.parent in ( from Item item where item.space.id = ? )", space.getId());
        logger.info("no of History rows where " + field.getName() + " set to null = " + historyCount);
        return itemCount;
    }

    public int findItemCount(Space space, Field field, int optionKey) {
        Criteria criteria = getSession().createCriteria(Item.class);
        criteria.add(Restrictions.eq("space", space));
        criteria.add(Restrictions.eq(field.getName().toString(), optionKey));
        criteria.setProjection(Projections.rowCount());
        return (Integer) criteria.list().get(0);         
    }

    public int removeFieldValues(Space space, Field field, int optionKey) {
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
    
}
