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

package info.jtrac.webflow;

import info.jtrac.domain.Config;
import info.jtrac.util.ValidationUtils;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.DataBinder;
import org.springframework.webflow.Event;
import org.springframework.webflow.RequestContext;
import org.springframework.webflow.ScopeType;

/**
 * Multiaction that participates in the config editing flow 
 */
public class ConfigFormAction extends AbstractFormAction {
    
    public ConfigFormAction() {
        setFormObjectClass(Config.class);
        setFormObjectName("config");
        setFormObjectScope(ScopeType.REQUEST);        
    }
    
    @Override
    protected void initBinder(RequestContext request, DataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));        
    }
    
    @Override
    public Object loadFormObject(RequestContext context) throws Exception {
        String key = ValidationUtils.getParameter(context, "key");
        Config config = jtrac.loadConfig(key);
        if (config == null) {
            config = new Config(key, null);
        }
        return config;
    }
    
    public Event configFormHandler(RequestContext context) throws Exception {
        Config config = (Config) getFormObject(context);
        jtrac.storeConfig(config);
        return success();
    }        
    
}
