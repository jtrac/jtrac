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

import info.jtrac.domain.Field;
import info.jtrac.domain.Space;
import java.util.ArrayList;
import java.util.List;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.Form;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;

/**
 * space edit form
 */
public class SpaceFieldsPage extends BasePage {      
    
    private void addComponents(Space space) {
        add(new HeaderPanel(null)); 
        border.add(new SpaceFieldsForm("form", space));
    }     
    
    public SpaceFieldsPage(Space space) {
        super("Edit Space Fields");
        addComponents(space);
    }
    
    private class SpaceFieldsForm extends Form {        
        
        public SpaceFieldsForm(String id, final Space space) {
            super(id);
            add(new Label("name", space.getName()));
            add(new Label("prefixCode", space.getPrefixCode()));
            ListView listView = new ListView("fields", space.getMetadata().getFieldList()) {
                protected void populateItem(ListItem listItem) {
                    Field field = (Field) listItem.getModelObject();
                    listItem.add(new Button("up"));
                    listItem.add(new Button("down"));
                    listItem.add(new Label("name", field.getName().getText()));
                    listItem.add(new Label("type", field.getName().getDescription()));
                    listItem.add(new Label("optional", field.isOptional() ? "Y" : ""));
                    listItem.add(new Label("label", field.getLabel()));
                    List<String> optionsList;
                    if(field.getOptions() != null) {
                        optionsList = new ArrayList(field.getOptions().values());
                    } else {
                        optionsList = new ArrayList(0);
                    }
                    ListView options = new ListView("options", optionsList) {
                        protected void populateItem(ListItem item) {
                            item.add(new Label("option", item.getModelObject() + ""));
                        }                        
                    };
                    listItem.add(options);
                    listItem.add(new Button("edit"));
                }                
            };
            add(listView);
            add(new Link("cancel") {
                public void onClick() {
                  
                }                
            });            
            
        }
        
        @Override
        protected void onSubmit() {

        }        
    }        
        

    
}
