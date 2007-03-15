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
import info.jtrac.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.model.StringResourceModel;

/**
 * base class for all wicket pages, this provides
 * a way to access the spring managed service layer
 * also takes care of the standard template for all
 * pages which is using wicket markup inheritance
 */
public abstract class BasePage extends WebPage {
    
    protected final Log logger = LogFactory.getLog(getClass());        
    
    protected Jtrac getJtrac() {
        return ((JtracApplication) getApplication()).getJtrac();
    }          
    
    protected User getPrincipal() {
        return ((JtracSession) getSession()).getUser();
    }
    
    /**
     * conditional flip of session if same user id
     */
    protected void refreshPrincipal(User user) {
        if(user.getId() == getPrincipal().getId()) {
            refreshPrincipal();
        }
    }
    
    protected void refreshPrincipal() {
        logger.debug("refreshing principal");
        User temp = getJtrac().loadUser(getPrincipal().getId());
        // loadUserByUsername forces hibernate eager load
        ((JtracSession) getSession()).setUser((User) getJtrac().loadUserByUsername(temp.getLoginName()));        
    }    
    
    public BasePage() {        
        add(new HeaderPanel());
        add(new Label("version", System.getProperty("jtrac.version")));
    }
    
    /**
     * localization helper
     */
    protected String localize(String key) {
        return getLocalizer().getString(key, null);
    }
    
    protected String localize(String key, Object... params) {
        // integer params cause problems, go with String only
        StringResourceModel m = new StringResourceModel(key, null, null, params);
        m.setLocalizer(getLocalizer());
        return m.getString();
    }    
    
}
