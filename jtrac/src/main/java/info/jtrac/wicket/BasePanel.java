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
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * base class for all wicket panels, this provides
 * a way to access the spring managed service layer
 * as well as other convenience common methods
 */
public class BasePanel extends Panel {
    
    protected static final Logger logger = LoggerFactory.getLogger(BasePanel.class);
    
    protected Jtrac getJtrac() {
        return JtracApplication.get().getJtrac();
    }          
    
    protected User getPrincipal() {
        return JtracSession.get().getUser();
    }
    
    protected void setCurrentSpace(Space space) {
        JtracSession.get().setCurrentSpace(space);
    }      
    
    protected Space getCurrentSpace() {
        return JtracSession.get().getCurrentSpace();
    }      
    
    protected void setCurrentItemSearch(ItemSearch itemSearch) {
        JtracSession.get().setItemSearch(itemSearch);
    }      
    
    protected ItemSearch getCurrentItemSearch() {
        return JtracSession.get().getItemSearch();
    }      
    
    protected String localize(String key) {
        return getLocalizer().getString(key, null);
    }
    
    protected String localize(String key, Object... params) {
        StringResourceModel m = new StringResourceModel(key, null, null, params);
        m.setLocalizer(getLocalizer());
        return m.getString();
    }            
    
    public BasePanel(String id) {
        super(id);
    }       
    
}
