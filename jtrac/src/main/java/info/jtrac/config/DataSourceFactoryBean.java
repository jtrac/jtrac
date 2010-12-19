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

package info.jtrac.config;

import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.util.StringUtils;

/**
 * <p>
 * This class implements the Spring frameworks
 * {@link org.springframework.beans.factory.FactoryBean} as well as the 
 * {@link org.springframework.beans.factory.DisposableBean} to conditionally
 * create the correct <code>DataSource</code> object to access the database.
 * </p>
 * 
 * Supported data sources are:
 * <ul>
 *    <li><a href="http://hsqldb.org/">HSQLDB (embedded)</a></li>
 *    <li><a href="http://commons.apache.org/dbcp/">Apache DBCP</a></li>
 *    <li>Java Naming and Directory Interface (JNDI); supported by most JDBC
 *    database drivers</li>
 * </ul>
 */
public class DataSourceFactoryBean implements FactoryBean, DisposableBean {
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
     * DB queries used to check if the database can be accessed
     */
    private String validationQuery;
    
    /**
     * DB JNDI data source name
     */
    private String dataSourceJndiName;
    
    /**
     * DB JNDI data source object
     */
    private DataSource dataSource;
    
    /**
     * This method allows to store the name of the DB driver class.
     * 
     * @param driverClassName The name of the DB driver class.
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
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
     * This method allows to store the SQL query string used to check if
     * the DB connection is working.
     * 
     * @param validationQuery The SQL query used to validate if the DB
     * connection is working.
     */
    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
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
     * This method returns the dataSource object used for the DB access.
     * 
     * @throws Exception
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() throws Exception {
        if(StringUtils.hasText(dataSourceJndiName)) {
            logger.info("JNDI datasource requested, looking up datasource from JNDI name: '" + dataSourceJndiName + "'");
            JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
            factoryBean.setJndiName(dataSourceJndiName);
            
            // "java:comp/env/" will be prefixed if the JNDI name doesn't already have it
            factoryBean.setResourceRef(true);
            
            // This step actually does the JNDI lookup
            try {
                factoryBean.afterPropertiesSet();
            } catch(Exception e) {
                logger.error("datasource init from JNDI failed : " + e);
                logger.error("aborting application startup");
                throw new RuntimeException(e);
            } // end try..catch
            
            dataSource = (DataSource) factoryBean.getObject();
        } else if(url.startsWith("jdbc:hsqldb:file")) {
            logger.info("embedded HSQLDB mode detected, switching on spring single connection data source");
            SingleConnectionDataSource ds = new SingleConnectionDataSource();
            ds.setUrl(url);
            ds.setDriverClassName(driverClassName);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setSuppressClose(true);
            dataSource = ds;
        } else {
            logger.info("Not using embedded HSQLDB or JNDI datasource, switching on Apache DBCP data source connection pooling");
            BasicDataSource ds = new BasicDataSource();
            ds.setUrl(url);
            ds.setDriverClassName(driverClassName);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setValidationQuery(validationQuery);
            ds.setTestOnBorrow(false);
            ds.setTestWhileIdle(true);
            ds.setTimeBetweenEvictionRunsMillis(600000);
            dataSource = ds;
        } // end if..else
        
        return dataSource;
    }
    
    /**
     * This method returns the class name of the DataSource object which can be
     * used to determine which data source implementation is currently used.
     *  
     * @return Returns the class name <code>Object.class</code> of the
     * DataSource object.
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType() {
        return DataSource.class;
    }
    
    /**
     * This method returns if the factory is implemented as Singleton.
     * 
     * @return Returns if the factory is implemented as Singleton or not.
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return true;
    }
    
    /**
     * This method is called to clean up the references when the object is
     * destroyed.
     * 
     * @throws Exception
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        if(dataSource instanceof SingleConnectionDataSource) {
            logger.info("attempting to shut down embedded HSQLDB database");
            Connection con = dataSource.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate("SHUTDOWN");
            stmt.close();
            con.close();
            logger.info("embedded HSQLDB database shut down successfully");
        } else if (dataSource instanceof BasicDataSource){
            logger.info("attempting to close Apache DBCP data source");
            ((BasicDataSource) dataSource).close();
            logger.info("Apache DBCP data source closed successfully");
        } else {
            logger.info("context shutting down for JNDI datasource");
        } // end if..else
    }
}
