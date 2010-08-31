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

package info.jtrac.wicket.devmode;

import java.io.IOException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.wicket.protocol.http.WicketFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * this is a different approach to reloading instead of using the reloading wicket
 * filter that comes along with wicket.  See this wicket mail discussion thread:
 * 
 * http://www.nabble.com/Reloading-on-demand-%28a-different-approach-to-reloading%29-tt15582878.html
 */
public class ReloadingWicketFilter extends WicketFilter {

    private static final Logger logger = LoggerFactory.getLogger(ReloadingWicketFilter.class);
    
    private static final String banner = 
        "\n***********************************************\n"
        + "*** WARNING: Reloading Wicket Filter in use ***\n"
        + "***    This is wrong if production mode.    ***\n"
        + "***********************************************";      
    
    private ReloadingClassLoader reloadingClassLoader;
    private FilterConfig filterConfig;

    public ReloadingWicketFilter() {        
        reloadingClassLoader = new ReloadingClassLoader(getClass().getClassLoader());               
        reloadingClassLoader.watch("info.jtrac.wicket.*");        
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        super.init(filterConfig);
        logger.warn(banner);
    }

    @Override
    public boolean doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        if (reloadingClassLoader.hasChanges()) {
            logger.debug("changes to reloadable classes detected, reloading...");            
            reloadingClassLoader = reloadingClassLoader.clone();            
            // request.getSession().invalidate();            
            super.init(filterConfig);
        }        
        return super.doGet(request, response);
    }

    @Override
    protected ClassLoader getClassLoader() {
        return reloadingClassLoader;
    }
    
}
