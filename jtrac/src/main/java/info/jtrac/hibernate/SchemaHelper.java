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

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * <p>
 * Utilities to create the database schema, drop and create tables by using
 * the Hibernate schema tools.
 * </p>
 * <p>
 * This method is normally called at the first start of the application to
 * initialize the database.
 * </p>
 */
public class SchemaHelper {
    /**
     * Logger object
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * DB driver class name
     */
    private String driverClassName;
    
    /**
     * DB url
     */
    private String url;
    
    /**
     * DB user
     */
    private String username;
    
    /**
     * DB password
     */
    private String password;
    
    /**
     * DB hibernate dialect
     */
    private String hibernateDialect;
    
    /**
     * DB JNDI data source name
     */
    private String dataSourceJndiName;
    
    /**
     * Mapping information
     */
    private String[] mappingResources;
    
    /**
     * This method allows to store the name of the DB driver class.
     * 
     * @param driverClassName The name of the DB driver class.
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
    
    /**
     * This method allows to store the Hibernate dialect required for the
     * specified database. You can find the list of supported dialects in the
     * Hibernate documentation.
     * 
     * @param hibernateDialect The dialect name supported by Hibernate.
     */
    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
    }
    
    /**
     * This method allows to store multiple mapping resources.
     * 
     * @param mappingResources The String array of mapping Resources.
     */
    public void setMappingResources(String[] mappingResources) {
        this.mappingResources = mappingResources;
    }
    
    /**
     * This method allows to store the DB url.
     * 
     * @param url The DB url.
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     * This method allows to store the DB user.
     * 
     * @param username The DB user.
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * This method allows to store the DB password.
     * 
     * @param password The DB password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * This method allows to store the JNDI data source name.
     * 
     * @param dataSourceJndiName The JNDI data source name.
     */
    public void setDataSourceJndiName(String dataSourceJndiName) {
        this.dataSourceJndiName = dataSourceJndiName;
    }
    
    /**
     * Create tables using the given Hibernate configuration data.
     */
    public void createSchema() {
        Configuration cfg = new Configuration();
        
        if(StringUtils.hasText(dataSourceJndiName)) {
            cfg.setProperty("hibernate.connection.datasource", dataSourceJndiName);
        } else {
            cfg.setProperty("hibernate.connection.driver_class", driverClassName);
            cfg.setProperty("hibernate.connection.url", url);
            cfg.setProperty("hibernate.connection.username", username);
            cfg.setProperty("hibernate.connection.password", password);
        } // end if..else
        
        cfg.setProperty("hibernate.dialect", hibernateDialect);
        
        for (String resource : mappingResources) {
            cfg.addResource(resource);
        } // end for
        
        logger.info("begin database schema creation =========================");
        new SchemaUpdate(cfg).execute(true, true);
        logger.info("end database schema creation ===========================");
    }
}
