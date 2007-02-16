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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.StringResourceModel;

/**
 * space edit form
 */
public class SpaceFieldsPage extends BasePage {              
    
    private void addComponents(Space space, String selectedFieldName) {
        add(new HeaderPanel(null)); 
        border.add(new SpaceFieldsForm("form", space, selectedFieldName));
    }     
    
    public SpaceFieldsPage(Space space, String selectedFieldName) {
        super("Edit Space Fields");
        addComponents(space, selectedFieldName);
    }
    
    private class SpaceFieldsForm extends Form {        
        
        public SpaceFieldsForm(String id, final Space space, final String selectedFieldName) {
            super(id);
            
            add(new Label("name", space.getName()));
            add(new Label("prefixCode", space.getPrefixCode()));
            
            final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
            
            ListView listView = new ListView("fields", space.getMetadata().getFieldList()) {
                protected void populateItem(ListItem listItem) {
                    final Field field = (Field) listItem.getModelObject();
                    
                    if (field.getName().getText().equals(selectedFieldName)) {
                        listItem.add(new SimpleAttributeModifier("class", "selected"));
                    } else if(listItem.getIndex() % 2 == 1) {
                        listItem.add(sam);
                    }                     
                    
                    listItem.add(new Button("up") {
                        @Override
                        protected void onSubmit() {    
                            List<Field.Name> fieldOrder = space.getMetadata().getFieldOrder();
                            int index = fieldOrder.indexOf(field.getName());
                            int swapIndex = index - 1;
                            if (swapIndex < 0) {
                                if (fieldOrder.size() > 1) {        
                                    swapIndex = fieldOrder.size() - 1;
                                } else {
                                    swapIndex = 0;
                                }
                            }
                            if (index != swapIndex) {
                                Collections.swap(fieldOrder, index, swapIndex);
                                setResponsePage(new SpaceFieldsPage(space, field.getName().getText()));
                            }                            
                        }                    
                    });
                    
                    listItem.add(new Button("down") {
                        @Override
                        protected void onSubmit() {  
                            List<Field.Name> fieldOrder = space.getMetadata().getFieldOrder();
                            int index = fieldOrder.indexOf(field.getName());
                            int swapIndex = index + 1;
                            if (swapIndex == fieldOrder.size() ) {
                                swapIndex = 0;
                            }
                            if (index != swapIndex) {
                                Collections.swap(fieldOrder, index, swapIndex);
                                setResponsePage(new SpaceFieldsPage(space, field.getName().getText()));
                            }                            
                        }                        
                    });
                    
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
            
            final Map<String, String> types = space.getMetadata().getAvailableFieldTypes();
            List<String> typesList = new ArrayList(types.keySet());
            DropDownChoice choice = new DropDownChoice("type", typesList, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    StringResourceModel m = new StringResourceModel("space_fields.typeRemaining", null, null, new Object[]{types.get(o)});
                    m.setLocalizer(getLocalizer());
                    return getLocalizer().getString("space_fields.type_" + o, null)
                        + " - " + m.getString();
                }
                public String getIdValue(Object o, int i) {
                    return o.toString();
                }
            });
            add(choice);
            
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
