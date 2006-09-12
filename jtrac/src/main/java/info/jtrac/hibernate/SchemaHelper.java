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

import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

/**
 * Utilities to create the database schema, drop and create tables
 * Uses Hibernate Schema tools
 * Used normally at application first start
 */
public class SchemaHelper {
    
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String hibernateDialect;
    private String[] mappingResources;
    
    private DataSource dataSource;

    private final Log logger = LogFactory.getLog(SchemaHelper.class);    
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
    }
    
    public void setMappingResources(String[] mappingResources) {
        this.mappingResources = mappingResources;
    }    
    
    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * create tables using the given Hibernate configuration
     */
    public void createSchema() {
        Configuration cfg = new Configuration();        
        cfg.setProperty("hibernate.connection.driver_class", driverClassName);        
        cfg.setProperty("hibernate.connection.url", url);
        cfg.setProperty("hibernate.connection.username", username);
        cfg.setProperty("hibernate.connection.password", password);
        cfg.setProperty("hibernate.dialect", hibernateDialect);        
        for (String resource : mappingResources) {
            cfg.addResource(resource);
        }        
        new SchemaUpdate(cfg).execute(true, true);
    }  
    
    /**
     * This is not mandatory, but makes the re-start cycle faster during development
     */
    public void stopEmbeddedDb() throws Exception {
        if (url.startsWith("jdbc:hsqldb:file")) {
            logger.info("attempting to shutdown embedded HSQLDB database");
            Connection con = dataSource.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate("SHUTDOWN");
            stmt.close();
            con.close();
            logger.info("embedded HSQLDB database stopped successfully");
        }
    }
    
}
