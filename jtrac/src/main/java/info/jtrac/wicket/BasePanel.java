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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wicket.markup.html.panel.Panel;

/**
 * base class for all wicket panels, this provides
 * a way to access the spring managed service layer
 */
public class BasePanel extends Panel {
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    protected Jtrac getJtrac() {
        return ((JtracApplication) getApplication()).getJtrac();
    }      
    
    protected void setCurrentSpace(Space space) {
        ((JtracSession) getSession()).setCurrentSpace(space);
    }    
    
    protected Space getCurrentSpace() {
        return ((JtracSession) getSession()).getCurrentSpace();
    }      
    
    public BasePanel(String id) {
        super(id);
    } 
    
    /**
     * localization helper
     */
    protected String localize(String key) {
        return getLocalizer().getString(key, null);
    }    
    
    protected User getPrincipal() {
        return ((JtracSession) getSession()).getUser();
    }    
    
}
