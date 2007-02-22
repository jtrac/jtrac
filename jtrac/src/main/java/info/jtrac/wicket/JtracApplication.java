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

import info.jtrac.Jtrac;
import java.util.Locale;
import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import wicket.Component;
import wicket.ISessionFactory;
import wicket.RestartResponseAtInterceptPageException;
import wicket.Session;
import wicket.authorization.Action;
import wicket.authorization.IAuthorizationStrategy;
import wicket.markup.parser.filter.WicketMessageTagHandler;
import wicket.protocol.http.WebApplication;
import wicket.resource.loader.IStringResourceLoader;

/**
 * main wicket application for jtrac
 * holds singleton service layer instance pulled from spring
 */
public class JtracApplication extends WebApplication {        
    
    private Jtrac jtrac;
    private ApplicationContext applicationContext;

    public Jtrac getJtrac() {
        return jtrac;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    @Override
    public void init() {
        
        super.init();
        
        // get hold of spring managed service layer (see BasePage, BasePanel etc for how it is used)
        ServletContext sc = getWicketServlet().getServletContext();
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(sc);        
        jtrac = (Jtrac) applicationContext.getBean("jtrac");
        
        // delegate wicket i18n support to spring i18n
        getResourceSettings().addStringResourceLoader(new IStringResourceLoader() {
            public String loadStringResource(Class clazz, String key, Locale locale, String style) {
                try {
                    return applicationContext.getMessage(key, null, locale);
                } catch(Exception e) {
                    // have to return null so that wicket can try to resolve again
                    // e.g. without prefixing component id etc.
                    return null;
                }
            }
        });                        

        WicketMessageTagHandler.enable = true;
        
        getPageSettings().setMaxPageVersions(3);
        
        getSecuritySettings().setAuthorizationStrategy(new IAuthorizationStrategy() {
            public boolean isActionAuthorized(Component c, Action a) {
                return true;
            }
            public boolean isInstantiationAuthorized(Class clazz) {
                if (BasePage.class.isAssignableFrom(clazz)) {
                    if (((JtracSession) Session.get()).isAuthenticated()) {
                        return true;
                    }
                    throw new RestartResponseAtInterceptPageException(LoginPage.class);
                }
                return true;
            }
        });       
        
    }   
    
    @Override
    public ISessionFactory getSessionFactory() {
        return new ISessionFactory() {
            public Session newSession() {
                return new JtracSession(JtracApplication.this);
            }
        };      
    }
    
    public Class getHomePage() {
        return DashboardPage.class;
    }    
    
}
