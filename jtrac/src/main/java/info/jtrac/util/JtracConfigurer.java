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

package info.jtrac.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Custom extension of the Spring PropertyPlaceholderConfigurer that
 * creates a jtrac.home System property if required and also creates
 * a default jtrac.properties file for HSQLDB
 */
public class JtracConfigurer extends PropertyPlaceholderConfigurer {
    
    private final Log logger = LogFactory.getLog(getClass());
    
    /**
     * Check is system property "jtrac.home" is set
     * if not, assume jtrac.home = ${user.dir}/.jtrac and 
     * create jtrac.home, as well as a default jtrac.properties for HSQLDB 
     * Note that the database will be created automatically if it does not exist
     * by HibernateJtracDao
     *
     * This method is fired by an "init-method" attribute in the Spring bean definition
     */    
    public void setup() throws Exception {
        String jtracHome = System.getProperty("jtrac.home");
        if (jtracHome == null) {                        
            jtracHome = System.getProperty("user.home") + "/.jtrac";
            logger.info("System property 'jtrac.home' does not exist.  Will use '" + jtracHome + "'");
            System.setProperty("jtrac.home", jtracHome);
        } else {
            logger.info("System property 'jtrac.home' exists: '" + jtracHome + "'");
        }
        File homeFile = new File(jtracHome);
        if (!homeFile.exists()) {
            homeFile.mkdir();
            logger.info("directory does not exist, created '" + homeFile.getPath() + "'");
        } else {
            logger.info("directory already exists: '" + homeFile.getPath() + "'");
        }           
        File propFile = new File(homeFile.getPath() + "/jtrac.properties");
        if (!propFile.exists()) {                
            propFile.createNewFile();
            logger.info("properties file does not exist, created '" + propFile.getPath() + "'");
            OutputStream os = new FileOutputStream(propFile);
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
            logger.info("HSQLDB will be used.  Finished creating '" + propFile.getPath() + "'");
        } else {
            logger.info("'jtrac.properties' file exists: '" + propFile.getPath() + "'");
        }            
        
    }
    
}
