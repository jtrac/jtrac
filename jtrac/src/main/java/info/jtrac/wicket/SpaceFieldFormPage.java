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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.BoundCompoundPropertyModel;
import wicket.model.PropertyModel;

/**
 * space field edit form
 */
public class SpaceFieldFormPage extends BasePage {
      
    private WebPage previous;
    private Space space;
    
    private void addComponents(Field field) {        
        add(new HeaderPanel(null)); 
        border.add(new SpaceFieldForm("form", field));
    }     
    
    public SpaceFieldFormPage(Space space, Field field, WebPage previous) {
        super("Edit Field");
        this.space = space;
        this.previous = previous;
        addComponents(field);
    }
    
    private class SpaceFieldForm extends Form {
        
       private JtracFeedbackMessageFilter filter;
        
        public SpaceFieldForm(String id, final Field field) {
            
            super(id);
            
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new JtracFeedbackMessageFilter();
            feedback.setFilter(filter);            
            add(feedback);
            
            SpaceFieldFormModel modelObject = new SpaceFieldFormModel();
            modelObject.setField(field);
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(modelObject);
            setModel(model);
            
            // delete button only if edit ======================================
            Button delete = new Button("delete") {
                @Override
                protected void onSubmit() {
                    String heading = getLocalizer().getString("space_delete.confirm", null);
                    String warning = getLocalizer().getString("space_delete.line3", null);
                    String line1 = getLocalizer().getString("space_delete.line1", null);
                    String line2 = getLocalizer().getString("space_delete.line2", null);
                    ConfirmPage confirm = new ConfirmPage(SpaceFieldFormPage.this, heading, warning, new String[] {line1, line2}) {
                        public void onConfirm() {
                            getJtrac().removeSpace(space);
                            setResponsePage(new SpaceListPage());
                        }                        
                    };
                    setResponsePage(confirm);
                }                
            };
            delete.setDefaultFormProcessing(false);
//            if(space.getId() <= 0) {
//                delete.setVisible(false);
//            } 
            add(delete);
            // internal name ===================================================
            add(new Label("name", new PropertyModel(field, "name.text")));
            // label ===========================================================
            final TextField label = new TextField("field.label");
            label.setRequired(true);
            label.add(new ErrorHighlighter());
            label.setOutputMarkupId(true);
            add(label);
            // optional ========================================================
            add(new CheckBox("field.optional"));            
            // options =========================================================
            WebMarkupContainer hide = new WebMarkupContainer("hide");
            if(field.getName().getType() < 4) { // drop down type
                final Map<String, String> optionsMap = field.getOptions();
                List<String> options = new ArrayList(optionsMap.keySet());
                ListView listView = new ListView("options", options) {
                    protected void populateItem(ListItem listItem) {
                        String key = (String) listItem.getModelObject();
                        listItem.add(new Label("key", key));
                        listItem.add(new Label("value", optionsMap.get(key)));
                        listItem.add(new Button("up"));
                        listItem.add(new Button("down"));
                        listItem.add(new Button("edit"));
                    }                    
                };
                hide.add(listView);
                hide.add(new TextField("option"));
                hide.add(new Button("add"));
            } else {
                hide.setVisible(false);    
            }
            add(hide);
            // cancel ==========================================================
            add(new Link("cancel") {
                public void onClick() {
                    setResponsePage(previous);
                }                
            });            
        }
     
        @Override
        protected void validate() {
            filter.reset();
            super.validate();          
        }        
        
        @Override
        protected void onSubmit() {
            SpaceFieldFormModel model = (SpaceFieldFormModel) getModelObject();            
        }        
    }        
        
    /**
     * custom form backing object that wraps Field
     * required for the create / edit use case
     */
    private class SpaceFieldFormModel implements Serializable {
        
        private transient Field field;
        private String option;

        public Field getField() {
            if(field == null) {
                field = new Field();
            }
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }
               
    }
    
}
