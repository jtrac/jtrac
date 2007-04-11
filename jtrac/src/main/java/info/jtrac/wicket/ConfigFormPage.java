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

import info.jtrac.domain.Config;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.BoundCompoundPropertyModel;

/**
 * config value edit form
 */
public class ConfigFormPage extends BasePage {              
    
    public ConfigFormPage(String param, String value) {            
        add(new ConfigForm("form", param, value));
    }
    
    private class ConfigForm extends Form {                
        
        private String param;
        
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }         
        
        public ConfigForm(String id, final String param, final String value) {
            
            super(id);                             
            
            this.param = param;
            this.value = value;
            
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(this);
            setModel(model);
            
            add(new Label("heading", localize("config." + param)));
            add(new Label("param", param));

            add(new TextField("value"));
            
            // cancel ==========================================================
            add(new Link("cancel") {
                public void onClick() {
                    setResponsePage(new ConfigListPage(param));
                }                
            });            
        }
                
        @Override
        protected void onSubmit() {            
            getJtrac().storeConfig(new Config(param, value));
            setResponsePage(new ConfigListPage(param));
        }     
                        
    }        
    
}
