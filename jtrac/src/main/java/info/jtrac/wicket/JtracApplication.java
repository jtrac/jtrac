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
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.wicket.yui.TestPage;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.target.coding.IndexedParamUrlCodingStrategy;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * main wicket application for jtrac
 * holds singleton service layer instance pulled from spring
 */
public class JtracApplication extends WebApplication {                
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
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
        ServletContext sc = getServletContext();
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
                    if(logger.isDebugEnabled()) {
                        logger.debug("i18n failed for key: '" + key + "', Class: " 
                                + clazz + ", Style: " + style + ", Exception: " + e);
                    }
                    return null;
                }
            }
            public String loadStringResource(Component component, String key) {
                Class clazz = component == null ? null : component.getClass();
                Locale locale = component == null ? Session.get().getLocale() : component.getLocale();
                return loadStringResource(clazz, key, locale, null);
            }            
        });                               
        
        // getPageSettings().setMaxPageVersions(3);
        
        getSecuritySettings().setAuthorizationStrategy(new IAuthorizationStrategy() {
            public boolean isActionAuthorized(Component c, Action a) {
                return true;
            }
            public boolean isInstantiationAuthorized(Class clazz) {
                if (BasePage.class.isAssignableFrom(clazz)) {
                    if (((JtracSession) Session.get()).isAuthenticated()) {
                        return true;
                    }
                    // attempt CAS authentication ==============================         
                    logger.debug("check if CAS authentication succeeded");
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if(authentication != null && authentication.isAuthenticated()) {
                        logger.debug("CAS authentication succeeded, initializing session");
                        ((JtracSession) Session.get()).setUser((User) authentication.getPrincipal());
                        return true;
                    }
                    // attempt remember-me auto login ==========================
                    Cookie[] cookies =  ((WebRequest) RequestCycle.get().getRequest()).getCookies();
                    if(cookies != null) {
                        for(Cookie c : cookies) {
                            if(c.getName().equals("jtrac")) {
                                String value = c.getValue();
                                logger.debug("found jtrac cookie: " + value);                
                                if (value != null) {
                                    int index = value.indexOf(':');
                                    if (index != -1) {
                                        String loginName = value.substring(0, index);
                                        String encodedPassword = value.substring(index + 1);
                                        logger.debug("valid cookie, attempting authentication");
                                        User user = (User) getJtrac().loadUserByUsername(loginName);                                              
                                        if(encodedPassword.equals(user.getPassword())) {                                        
                                            ((JtracSession) Session.get()).setUser(user);
                                            logger.debug("remember me login success");
                                            // and proceed
                                            return true;
                                        }
                                    }
                                }                
                            }
                        }
                    }
                    // attempt guest access if there are "public" spaces =======
                    List<Space> spaces = getJtrac().findSpacesWhereGuestAllowed();
                    if (spaces.size() > 0) {
                        logger.debug(spaces.size() + " public space(s) available, initializing guest user");
                        User guestUser = new User();
                        guestUser.setLoginName("guest");
                        guestUser.setName("Guest");
                        guestUser.addSpaceWithRole(null, "ROLE_GUEST");
                        for (Space space : spaces) {            
                            guestUser.addSpaceWithRole(space, "ROLE_GUEST");
                        }
                        ((JtracSession) Session.get()).setUser(guestUser);                        
                        // and proceed
                        return true;
                    }
                    // not authenticated, go to login page
                    logger.debug("page requested was " + clazz.getName() + ", redirecting");
                    throw new RestartResponseAtInterceptPageException(LoginPage.class);
                    // String serviceUrl = "http://localhost:8080/jtrac/auth/j_acegi_cas_security_check";                    
                    // throw new RestartResponseAtInterceptPageException(new RedirectPage("http://localhost:8080/cas/login?service=" + serviceUrl));
                }
                return true;
            }
        });
        
        // friendly urls for selected pages
        mountBookmarkablePage("/login", LoginPage.class);
        mountBookmarkablePage("/logout", LogoutPage.class);
        mountBookmarkablePage("/svn", SvnStatsPage.class);
        mountBookmarkablePage("/test", TestPage.class);
        // bookmarkable url for viewing items
        mount(new IndexedParamUrlCodingStrategy("/item", ItemViewPage.class));
        
    }   
    
    @Override
    public Session newSession(Request request, Response response) {                   
        return new JtracSession(JtracApplication.this, request);        
    }
    
    public Class getHomePage() {
        return DashboardPage.class;
    }    
    
    public User authenticate(String loginName, String password) {                    
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginName, password);
        AuthenticationManager am = (AuthenticationManager) applicationContext.getBean("authenticationManager");
        try {
            Authentication authentication = am.authenticate(token);
            return (User) authentication.getPrincipal();
        } catch(AuthenticationException ae) {
            logger.debug("acegi authentication failed: " + ae);
            return null;
        }
    }
    
}
