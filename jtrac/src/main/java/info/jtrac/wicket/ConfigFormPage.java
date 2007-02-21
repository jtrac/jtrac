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
import java.io.Serializable;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.link.Link;
import wicket.model.BoundCompoundPropertyModel;

/**
 * config value edit form
 */
public class ConfigFormPage extends BasePage {              
    
    public ConfigFormPage(String param, String value) {
        super("Edit Configuration Setting");
        add(new HeaderPanel(null));
        border.add(new ConfigForm("form", param, value));
    }
    
    private class ConfigForm extends Form {                
        
        private String param;
        
        public ConfigForm(String id, final String param, final String value) {
            
            super(id);                             
            this.param = param;
            
            ConfigModel modelObject = new ConfigModel();          
            modelObject.setValue(value);
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(modelObject);
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
            ConfigModel model = (ConfigModel) getModelObject();
            getJtrac().storeConfig(new Config(param, model.getValue()));
            setResponsePage(new ConfigListPage(param));
        }     
                        
    }        
        
    /**
     * custom form backing object that wraps config value
     * required for editing
     */
    private class ConfigModel implements Serializable {
                
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }       
               
    }
    
}
