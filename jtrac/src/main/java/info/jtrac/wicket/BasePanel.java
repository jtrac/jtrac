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
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * base class for all wicket panels, this provides
 * a way to access the spring managed service layer
 */
public class BasePanel extends Panel {
    
    protected static final Logger logger = LoggerFactory.getLogger(BasePanel.class);
    
    protected Jtrac getJtrac() {
        return ComponentUtils.getJtrac(this);
    }          
    
    protected User getPrincipal() {
        return ComponentUtils.getPrincipal(this);
    }
    
    protected void setCurrentSpace(Space space) {
        ComponentUtils.setCurrentSpace(this, space);
    }      
    
    protected Space getCurrentSpace() {
        return ComponentUtils.getCurrentSpace(this);
    }      
    
    protected void setCurrentItemSearch(ItemSearch itemSearch) {
        ComponentUtils.setCurrentItemSearch(this, itemSearch);
    }      
    
    protected ItemSearch getCurrentItemSearch() {
        return ComponentUtils.getCurrentItemSearch(this);
    }    
    
    protected String localize(String key) {
        return ComponentUtils.localize(this, key);
    }
    
    protected String localize(String key, Object... params) {
        return ComponentUtils.localize(this, key, params);
    }      
    
    protected void refreshPrincipal(User user) {
        ComponentUtils.refreshPrincipal(this, user);
    }
    
    protected void refreshPrincipal() {
        ComponentUtils.refreshPrincipal(this);
    }    
    
    public BasePanel(String id) {
        super(id);
    }       
    
}
