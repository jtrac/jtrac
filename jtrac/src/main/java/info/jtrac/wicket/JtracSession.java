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
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.springframework.util.StringUtils;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * custom wicket session for JTrac
 */
public class JtracSession extends WebSession {   
    
    private static final Logger logger = LoggerFactory.getLogger(JtracSession.class);   
    
    private User user;
    private Space currentSpace;
    private ItemSearch itemSearch;
    
    public static JtracSession get() {        
        Session session = Session.get();
        if (!JtracSession.class.getClassLoader().equals(session.getClass().getClassLoader())) {
            logger.debug("session classloaders don't match, serializing...");
            // this will happen only in development mode when our classloader hack of
            // a wicket filter is operating, since the session was deserialized by the
            // web-app-server it is now in a different classloader - here we just
            // serialize and deserialize in memory to get it back in *our* classloader
            // and then the cast to JtracSession will work fine - but directly using this
            // is not good enough since some transient fields are lost, so we have to use
            // wicket to create a new session and copy over the state we need from the old one            
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ObjectOutput oo = new ObjectOutputStream(os);
                oo.writeObject(session);
                oo.close();
                InputStream is = new ByteArrayInputStream(os.toByteArray());                
                ObjectInput oi = new ObjectInputStream(is);                                    
                Object oldSession = oi.readObject();                
                JtracSession oldJtracSession = (JtracSession) oldSession;                
                JtracSession newJtracSession = JtracApplication.get().newSession(RequestCycle.get().getRequest(), null);                
                newJtracSession.setCurrentSpace(oldJtracSession.getCurrentSpace());
                newJtracSession.setUser(oldJtracSession.getUser());
                newJtracSession.setItemSearch(oldJtracSession.getItemSearch());
                Session.set(newJtracSession);                 
                return newJtracSession;
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (JtracSession) session;        
    }
    
    public JtracSession(Request request) {        
        super(request);
        logger.debug("JtracSession create: " + getClass().getClassLoader());
        int timeOut = JtracApplication.get().getJtrac().getSessionTimeoutInMinutes();
        ((ServletWebRequest) request).getHttpServletRequest().getSession().setMaxInactiveInterval(timeOut * 60);        
    }
    
    public void setUser(User user) {
        this.user = user;
        if (user.getLocale() == null) {
            // for downward compatibility, may be null in old JTrac versions
            user.setLocale(JtracApplication.get().getJtrac().getDefaultLocale());
        }
        // flip locale only if different from existing
        if (!getLocale().getDisplayName().equals(user.getLocale())) {
            setLocale(StringUtils.parseLocaleString(user.getLocale()));
        }
    }
    
    private Jtrac getJtrac() {
        return JtracApplication.get().getJtrac();
    }

    /* reload user details from database */
    public void refreshPrincipal() {
        // who knows, loginName could have changed, use id to get latest
        User temp = getJtrac().loadUser(getUser().getId());        
        // loadUserByUsername forces hibernate eager load
        // TODO make this suck less
        setUser((User) getJtrac().loadUserByUsername(temp.getLoginName())); 
    }
    
    /* only reload if passed in user is same as session user */
    public void refreshPrincipalIfSameAs(User temp) {
        if(user.getId() == temp.getId()) {
            refreshPrincipal();
        }
    }
    
    public User getUser() {
        return user;
    }
    
    public boolean isAuthenticated() {
        return user != null;
    }

    public Space getCurrentSpace() {
        return currentSpace;
    }

    public void setCurrentSpace(Space currentSpace) {
        this.currentSpace = currentSpace;
    }    

    public ItemSearch getItemSearch() {
        return itemSearch;
    }

    public void setItemSearch(ItemSearch itemSearch) {
        if(itemSearch != null) {
            this.currentSpace = itemSearch.getSpace();
        }        
        this.itemSearch = itemSearch;
    }
    
}
