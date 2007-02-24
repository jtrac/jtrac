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

import info.jtrac.domain.User;
import org.springframework.util.StringUtils;
import wicket.Request;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebSession;

/**
 * custom wicket session for JTrac
 */
public class JtracSession extends WebSession {
    
    private User user;
    
    public JtracSession(final WebApplication application, Request request) {
        super(application, request);
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
    
}
