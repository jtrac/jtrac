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
import info.jtrac.domain.Attachment;
import info.jtrac.domain.Config;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.SpaceSequence;
import info.jtrac.domain.User;
import info.jtrac.domain.UserRole;
import java.util.ArrayList;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.orm.hibernate3.HibernateCallback;
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
        return (Item) getHibernateTemplate().load(Item.class, id);
    }
    
    public List<Item> findItems(ItemSearch itemSearch) {
        return getHibernateTemplate().findByCriteria(itemSearch.getCriteria());
    }
    
    public void storeAttachment(Attachment attachment) {
        getHibernateTemplate().merge(attachment);
    }    
    
    public void storeMetadata(Metadata metadata) {
        getHibernateTemplate().merge(metadata);
    }
    
    public Metadata loadMetadata(int id) {
        return (Metadata) getHibernateTemplate().load(Metadata.class, id);
    }    
    
    public void storeSpace(Space space) {
        getHibernateTemplate().merge(space);
    }
    
    public Space loadSpace(int id) {
        return (Space) getHibernateTemplate().load(Space.class, id);
    }
    
    public void storeSpaceSequence(SpaceSequence spaceSequence) {
        getHibernateTemplate().merge(spaceSequence);
    }
    
    public List<Space> findSpacesByPrefixCode(String prefixCode) {
        return getHibernateTemplate().find("from Space space where space.prefixCode = ?", prefixCode);
    }
    
    public List<Space> findAllSpaces() {
        return getHibernateTemplate().loadAll(Space.class);
    }
    
    public void storeUser(User user) {
        getHibernateTemplate().merge(user);
    }
    
    public User loadUser(int id) {
        return (User) getHibernateTemplate().load(User.class, id);
    }
    
    public List<User> findAllUsers() {
        return getHibernateTemplate().loadAll(User.class);
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
    
    public List<UserRole> findUsersForSpace(int spaceId) {
        List<Object[]> rawList = getHibernateTemplate().find("select user, spaceRole.roleKey from User user" + 
                " join user.spaceRoles as spaceRole where spaceRole.space.id = ?", spaceId);
        List<UserRole> userRoles = new ArrayList<UserRole>();
        for (Object[] userRole : rawList) {
            User user = (User) userRole[0];
            String roleKey = (String) userRole[1];
            userRoles.add(new UserRole(user, roleKey));
        }
        return userRoles;
    }
    
    public void storeConfig(Config config) {
        getHibernateTemplate().merge(config);
    }
    
    public Config loadConfig(String key) {
        return (Config) getHibernateTemplate().load(Config.class, key);
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
            user.setPassword("21232f297a57a5a743894a0e4a801fc3");
            user.addSpaceRole(null, "ROLE_ADMIN");
            getSession().save(user);
            logger.info("schema creation complete");
            return;
        }
        logger.info("database schema exists, normal startup");
    }        
    
}
