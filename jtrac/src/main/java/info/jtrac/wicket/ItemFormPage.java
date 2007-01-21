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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.form.TextField;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.markup.html.panel.Fragment;
import wicket.model.BoundCompoundPropertyModel;

/**
 * Create / Edit item form page
 */
public class ItemFormPage extends BasePage {
    
    public ItemFormPage(Item item) {
        super("Edit Item");
        border.add(new FeedbackPanel("feedback"));        
        border.add(new ItemForm("form", item));
        border.add(new DatePicker("datePicker"));
    }
    
    private class ItemForm extends Form {
        
        public ItemForm(String id, Item item) {
            super(id);
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(item);
            setModel(model);
            add(new TextField("summary").setRequired(true).add(new ErrorHighlighter()));
            add(new TextArea("detail").setRequired(true).add(new ErrorHighlighter()));
            // custom fields
            List<Field> fields = item.getSpace().getMetadata().getFieldList();
            ListView listView = new ListView("fields", fields) {
                protected void populateItem(ListItem listItem) {
                    Field field = (Field) listItem.getModelObject();
                    listItem.add(new Label("label", field.getLabel()));
                    listItem.add(new Label("star", field.isOptional() ? null : "*"));
                    if (field.getName().getType() < 4) { // drop down list
                        Fragment f = new Fragment("field", "select");
                        final Map<String, String> options = field.getOptions();                                
                        List<String> keys = new ArrayList(options.keySet());
                        DropDownChoice choice = new DropDownChoice("select", keys, new IChoiceRenderer() {
                            public Object getDisplayValue(Object o) {
                                return options.get(o);
                            };
                            public String getIdValue(Object o, int i) {
                                return o.toString();
                            };
                        });
                        choice.setNullValid(true);
                        f.add(model.bind(choice, field.getNameText()));
                        listItem.add(f);
                    } else if (field.getName().getType() == 6){ // date picker
                        
                    } else {
                        
                    }
                }
            };
            listView.setReuseItems(true);
            add(listView);
        }
        
        @Override
        protected void onSubmit() {
            info("the form was submitted");
            Item item = (Item) getModelObject();
            info("summary: " + item.getSummary());
            info("detail: " + item.getDetail());
            info("detail: " + item.getDetail());
            for(Field f : item.getSpace().getMetadata().getFieldList()) {
                info(f.getName() + ": " + item.getValue(f.getName()));
            }
        }
        
    }
    
}
