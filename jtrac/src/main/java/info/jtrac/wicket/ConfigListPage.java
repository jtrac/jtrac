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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 * This class is responsible to list all configuration parameters.
 * It also declares which edit forms will be called to modify the
 * configuration parameters.
 */
public class ConfigListPage extends BasePage {
    /**
     * Constructor
     * 
     * @param selectedParam
     */
    public ConfigListPage(final String selectedParam) {
        
        final Map<String, String> configMap = getJtrac().loadAllConfig();
        
        List<String> params = new ArrayList(Config.getParams());
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
        add(new ListView("configs", params) {
            protected void populateItem(ListItem listItem) {
                final String param = (String) listItem.getModelObject();
                final String value = configMap.get(param);
                
                if (param.equals(selectedParam)) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if(listItem.getIndex() % 2 == 1) {
                    listItem.add(sam);
                }
                
                listItem.add(new Label("param", param));
                listItem.add(new Label("value", value));
                
                if (param.toLowerCase().indexOf("password") != -1) {
                    // Password value to be edited
                    listItem.add(new Link("link") {
                        public void onClick() {
                            setResponsePage(new ConfigPasswordFormPage(param, value));
                        }
                    });
                } else {
                    // Normal text value to be edited
                    listItem.add(new Link("link") {
                        public void onClick() {
                            setResponsePage(new ConfigFormPage(param, value));
                        }
                    });
                }
                listItem.add(new Label("description", localize("config." + param)));
            }
        });
        
    }
}