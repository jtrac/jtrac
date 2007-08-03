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

package info.jtrac.wicket;

import org.apache.wicket.application.ReloadingClassLoader;
import org.apache.wicket.protocol.http.ReloadingWicketFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * only used in development mode, hot deploy modified code
 * as far as possible without having to restart webappimport org.slf4j.LoggerFactory;

 */
public class JtracReloadingWicketFilter extends ReloadingWicketFilter {        
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final String banner = 
        "\n***********************************************\n"
        + "*** WARNING: Reloading Wicket Filter in use ***\n"
        + "***    This is wrong if production mode.    ***\n"
        + "***********************************************";      
    
    static {
        ReloadingClassLoader.includePattern("info.jtrac.wicket.*");        
        ReloadingClassLoader.excludePattern("info.jtrac.wicket.JtracApplication");
        ReloadingClassLoader.excludePattern("info.jtrac.wicket.JtracSession");
        ReloadingClassLoader.excludePattern("info.jtrac.wicket.DashboardPage");
        // ReloadingClassLoader.excludePattern("org.springframework.*");
        // ReloadingClassLoader.excludePattern("org.acegisecurity.*");
    }
    
    public JtracReloadingWicketFilter() {
        super();
        logger.warn("reloading wicket filter being used - this is wrong if production mode");
    }
    
}
