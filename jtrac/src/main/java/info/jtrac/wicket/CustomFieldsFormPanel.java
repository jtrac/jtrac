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
import info.jtrac.domain.Item;
import info.jtrac.domain.User;
import info.jtrac.wicket.yui.YuiCalendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.TextField;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Fragment;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.BoundCompoundPropertyModel;

/**
 * panel for custom fields that can be reused in the ite-create / item-view forms
 */
public class CustomFieldsFormPanel extends BasePanel {        
    
    public CustomFieldsFormPanel(String id, final BoundCompoundPropertyModel model, Item item, User user) {
        super(id);
        List<Field> fields = item.getEditableFieldList(user);
        ListView listView = new ListView("fields", fields) {
            protected void populateItem(ListItem listItem) {
                final Field field = (Field) listItem.getModelObject();
                listItem.add(new Label("label", field.getLabel()));
                listItem.add(new Label("star", field.isOptional() ? "&nbsp;" : "*").setEscapeModelStrings(false));
                if (field.getName().getType() < 4) { // drop down list                    
                    Fragment f = new Fragment("field", "dropDown");
                    final Map<String, String> options = field.getOptions();                                
                    List<String> keys = new ArrayList(options.keySet());  // bound value
                    DropDownChoice choice = new DropDownChoice("field", keys, new IChoiceRenderer() {
                        public Object getDisplayValue(Object o) {
                            return options.get(o);
                        };
                        public String getIdValue(Object o, int i) {
                            return o.toString();
                        };
                    });
                    choice.setNullValid(true);
                    // choice.add(new ErrorHighlighter());                    
                    choice.setLabel(new AbstractReadOnlyModel() {
                        public Object getObject() {
                            return field.getLabel();
                        }
                    });                        
                    if (!field.isOptional()) {
                        choice.setRequired(true);
                    }
                    WebMarkupContainer border = new WebMarkupContainer("border");
                    f.add(border);
                    border.add(new ErrorHighlighter(choice));
                    border.add(model.bind(choice, field.getName().getText()));                    
                    listItem.add(f);
                } else if (field.getName().getType() == 6){ // date picker                        
                    listItem.add(new YuiCalendar("field", model, field.getName().getText(), !field.isOptional(), field.getLabel()));
                } else {
                    Fragment f = new Fragment("field", "textField");
                    TextField textField = new TextField("field");
                    if (field.getName().getType() == 4) {
                        textField.setType(Double.class);
                    }
                    textField.add(new ErrorHighlighter());
                    if (!field.isOptional()) {
                        textField.setRequired(true);
                    }
                    textField.setLabel(new AbstractReadOnlyModel() {
                        public Object getObject() {
                            return field.getLabel();
                        }
                    });                         
                    f.add(model.bind(textField, field.getName().getText()));
                    listItem.add(f);
                }
            }
        };
        listView.setReuseItems(true);
        add(listView);            
    }
    
}
