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

import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import javax.servlet.http.HttpSession;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.springframework.util.StringUtils;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;

/**
 * custom wicket session for JTrac
 */
public class JtracSession extends WebSession {
    
    private User user;
    private Space currentSpace;
    
    public JtracSession(final WebApplication application, Request request) {
        super(application, request);
        int timeOut = ((JtracApplication) application).getJtrac().getSessionTimeoutInMinutes();
        ((ServletWebRequest) request).getHttpServletRequest().getSession().setMaxInactiveInterval(timeOut * 60);        
    }

    public void setUser(User user) {
        this.user = user;
        if(user.getLocale() == null) {
            // for downward compatibility, may be null in old JTrac versions
            user.setLocale(((JtracApplication) getApplication()).getJtrac().getDefaultLocale());
        }
        // flip locale only if different from existing
        if(!getLocale().getDisplayName().equals(user.getLocale())) {
            setLocale(StringUtils.parseLocaleString(user.getLocale()));
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
    
}
