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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.context.ServletContextAware;

/**
 * <p>
 * Custom extension of the Spring PropertyPlaceholderConfigurer that
 * sets up the jtrac.home System property (creates if required) and also creates
 * a default jtrac.properties file for HSQLDB - useful for those who want
 * to quickly evaluate JTrac.  Just dropping the war into a servlet container
 * would work without the need to even configure a datasource.
 * </p>
 * <p>
 * This class would effectively do nothing if a <code>jtrac.properties</code> file exists in jtrac.home
 * </p>
 * <ol>
 *   <li>A "jtrac.home" property is looked for in <code>/WEB-INF/classes/jtrac-init.properties</code></li>
 *   <li>if not found, then a <code>jtrac.home</code> system property is checked for</li>
 *   <li>then a servlet context init-parameter called <code>jtrac.home</code> is looked for</li>
 *   <li>last resort, a <code>.jtrac</code> folder is created in the <code>user.home</code> and used as <code>jtrac.home</code></li>
 * </ol>
 * 
 * <p>
 * Other tasks
 * </p>
 * <ul>
 *   <li>initialize the "test" query for checking idle database connections</li>
 *   <li>initialize list of available locales based on the properties files available</li>
 * </ul>
 * 
 * <p>
 * Also playing an important role during startup are the following factory beans:
 * </p>
 * <ul>
 *   <li>DataSourceFactoryBean:</li>
 *     <ul>
 *       <li>switches between embedded HSQLDB or Apache DBCP (connection pool)</li>
 *       <li>performs graceful shutdown of database if embedded HSQLDB</li>
 *     </ul>
 *   <li>ProviderManagerFactoryBean</li>
 *     <ul>
 *       <li>conditionally includes LDAP authentication if requested</li>
 *     </ul>
 * </ul>
 * 
 * <p>
 * Note that later on during startup, the HibernateJtracDao would check if
 * database tables exist, and if they don't, would proceed to create them.
 * </p>
 */

public class JtracConfigurer extends PropertyPlaceholderConfigurer implements ServletContextAware {
    
    private ServletContext servletContext;
    
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // do our custom configuration before spring gets a chance to
        try {
            configureJtrac();
        } catch(Exception e) {
            throw new BeanCreationException("JtracConfigurer failed", e);
        }
        super.postProcessBeanFactory(beanFactory);
    }
    
    private void configureJtrac() throws Exception {
        String jtracHome = null;
        ClassPathResource jtracInitResource = new ClassPathResource("jtrac-init.properties");
        // jtrac-init.properties assumed to exist
        Properties props = loadProps(jtracInitResource.getFile());
        logger.info("found 'jtrac-init.properties' on classpath, processing...");
        jtracHome = props.getProperty("jtrac.home");
        if (jtracHome != null) {
            logger.info("'jtrac.home' property initialized from 'jtrac-init.properties' as '" + jtracHome + "'");
        }
        //======================================================================
        FilenameFilter ff = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("messages_") && name.endsWith(".properties");
            }
        };
        File[] messagePropsFiles = jtracInitResource.getFile().getParentFile().listFiles(ff);
        String locales = "en";
        for(File f : messagePropsFiles) {
            int endIndex = f.getName().indexOf('.');
            String localeCode = f.getName().substring(9, endIndex);
            locales += "," + localeCode;
        }
        logger.info("locales available configured are '" + locales + "'");
        props.setProperty("jtrac.locales", locales);
        //======================================================================
        if (jtracHome == null) {
            logger.info("valid 'jtrac.home' property not available in 'jtrac-init.properties', trying system properties.");
            jtracHome = System.getProperty("jtrac.home");
            if (jtracHome != null) {
                logger.info("'jtrac.home' property initialized from system properties as '" + jtracHome + "'");
            }
        }
        if (jtracHome == null) {
            logger.info("valid 'jtrac.home' property not available in system properties, trying servlet init paramters.");
            jtracHome = servletContext.getInitParameter("jtrac.home");
            if (jtracHome != null) {
                logger.info("Servlet init parameter 'jtrac.home' exists: '" + jtracHome + "'");
            }
        }
        if (jtracHome == null) {
            jtracHome = System.getProperty("user.home") + "/.jtrac";
            logger.warn("Servlet init paramter  'jtrac.home' does not exist.  Will use 'user.home' directory '" + jtracHome + "'");
        }
        //======================================================================
        File homeFile = new File(jtracHome);
        if (!homeFile.exists()) {
            homeFile.mkdir();
            logger.info("directory does not exist, created '" + homeFile.getPath() + "'");
            if (!homeFile.exists()) {
                String message = "invalid path '" + homeFile.getAbsolutePath() + "', try creating this directory first.  Aborting.";
                logger.error(message);
                throw new Exception(message);
            }
        } else {
            logger.info("directory already exists: '" + homeFile.getPath() + "'");
        }
        props.setProperty("jtrac.home", homeFile.getAbsolutePath());
        //======================================================================
        File attachmentsFile = new File(jtracHome + "/attachments");
        if (!attachmentsFile.exists()) {
            attachmentsFile.mkdir();
            logger.info("directory does not exist, created '" + attachmentsFile.getPath() + "'");
        } else {
            logger.info("directory already exists: '" + attachmentsFile.getPath() + "'");
        }
        File indexesFile = new File(jtracHome + "/indexes");
        if (!indexesFile.exists()) {
            indexesFile.mkdir();
            logger.info("directory does not exist, created '" + indexesFile.getPath() + "'");
        } else {
            logger.info("directory already exists: '" + indexesFile.getPath() + "'");
        }
        //======================================================================
        File propsFile = new File(homeFile.getPath() + "/jtrac.properties");
        if (!propsFile.exists()) {
            propsFile.createNewFile();
            logger.info("properties file does not exist, created '" + propsFile.getPath() + "'");
            OutputStream os = new FileOutputStream(propsFile);
            Writer out = new PrintWriter(os);
            try {
                out.write("database.driver=org.hsqldb.jdbcDriver\n");
                out.write("database.url=jdbc:hsqldb:file:${jtrac.home}/db/jtrac\n");
                out.write("database.username=sa\n");
                out.write("database.password=\n");
                out.write("hibernate.dialect=org.hibernate.dialect.HSQLDialect\n");
                out.write("hibernate.show_sql=false\n");
            } finally {
                out.close();
                os.close();
            }
            logger.info("HSQLDB will be used.  Finished creating '" + propsFile.getPath() + "'");
        } else {
            logger.info("'jtrac.properties' file exists: '" + propsFile.getPath() + "'");
        }
        //======================================================================
        String version = "0.0.0";
        String timestamp = "0000";
        ClassPathResource versionResource = new ClassPathResource("jtrac-version.properties");
        if(versionResource.exists()) {
            logger.info("found 'jtrac-version.properties' on classpath, processing...");
            Properties versionProps = loadProps(versionResource.getFile());
            version = versionProps.getProperty("version");
            timestamp = versionProps.getProperty("timestamp");
        } else {
            logger.info("did not find 'jtrac-version.properties' on classpath");
        }
        logger.info("jtrac.version = '" + version + "'");
        logger.info("jtrac.timestamp = '" + timestamp + "'");
        props.setProperty("jtrac.version", version);
        props.setProperty("jtrac.timestamp", timestamp);
        
        /*
         * TODO: A better way (default value) to check the database should be used for Apache DBCP.
         * The current "SELECT...FROM DUAL" only works on Oracle (and MySQL).
         * Other databases also support "SELECT 1+1" as query
         * (e.g. PostgreSQL, Hypersonic 2 (H2), MySQL, etc.).
         */
        props.setProperty("database.validationQuery", "SELECT 1 FROM DUAL");
        props.setProperty("ldap.url", "");
        props.setProperty("ldap.activeDirectoryDomain", "");
        props.setProperty("ldap.searchBase", "");
        props.setProperty("database.datasource.jndiname", "");
        // set default properties that can be overridden by user if required
        setProperties(props);
        // finally set the property that spring is expecting, manually
        FileSystemResource fsr = new FileSystemResource(propsFile);
        setLocation(fsr);
    }
   
    private Properties loadProps(File file) throws Exception {
        InputStream is = null;
        Properties props = new Properties();
        try {
            is = new FileInputStream(file);
            props.load(is);
        } finally {
            is.close();
        }
        return props;
    }
}
