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
import info.jtrac.domain.Item;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Role;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;

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
        return (Item) getHibernateTemplate().load(Item.class, new Long(id));
    }
    
    public void storeMetadata(Metadata metadata) {
        getHibernateTemplate().merge(metadata);
    }
    
    public Metadata loadMetadata(int id) {
        return (Metadata) getHibernateTemplate().load(Metadata.class, new Integer(id));
    }    
    
    public void storeSpace(Space space) {
        getHibernateTemplate().merge(space);
    }
    
    public Space loadSpace(int id) {
        return (Space) getHibernateTemplate().load(Space.class, new Integer(id));
    }
    
    public List<Space> loadSpace(String prefixCode) {
        return getHibernateTemplate().find("from Space space where space.prefixCode = ?", prefixCode);
    }
    
    public List<Space> loadAllSpaces() {
        return getHibernateTemplate().loadAll(Space.class);
    }
    
    public void storeRole(Role role) {
        getHibernateTemplate().merge(role);
    }
    
    public Role loadRole(int id) {
        return (Role) getHibernateTemplate().load(Role.class, new Integer(id));
    }
    
    public void storeUser(User user) {
        getHibernateTemplate().merge(user);
    }
    
    public User loadUser(int id) {
        return (User) getHibernateTemplate().load(User.class,
                new Integer(id));
    }
    
    public List<User> loadAllUsers() {
        return getHibernateTemplate().loadAll(User.class);
    }
    
    public User loadUser(final String loginName) {
        // using criteria query to override lazy loading during authentication
        return (User) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                return session.createCriteria(User.class).setFetchMode(
                        "spaceRoles", FetchMode.JOIN).add(
                        Restrictions.eq("loginName", loginName)).uniqueResult();
            }
        });
    }
    
    public User loadUserByEmail(final String email) {
        // using criteria query to override lazy loading during authentication
        return (User) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                return session.createCriteria(User.class).setFetchMode(
                        "spaceRoles", FetchMode.JOIN).add(
                        Restrictions.eq("email", email)).uniqueResult();
            }
        });
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
            getSession().merge(user);
            logger.info("schema creation complete");
            return;
        }
        logger.info("database schema exists, normal startup");
    }        
    
}
