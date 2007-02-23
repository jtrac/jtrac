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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.FormComponent;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.validation.AbstractValidator;
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
                public void onSubmit() {
                    int affectedCount = getJtrac().loadCountOfRecordsHavingFieldNotNull(space, field);
                    if (affectedCount > 0) {
                        String heading = localize("space_field_delete.confirm") + " : " + field.getLabel() 
                            + " [" + field.getName().getDescription() + " - " + field.getName().getText() + "]";
                        String warning = localize("space_field_delete.line3");
                        String line1 = localize("space_field_delete.line1");
                        String line2 = localize("space_field_delete.line2", affectedCount + "");                        
                        ConfirmPage confirm = new ConfirmPage(SpaceFieldFormPage.this, heading, warning, new String[] {line1, line2}) {
                            public void onConfirm() {
                                // database will be updated, if we don't do this
                                // user may leave without committing metadata change                                
                                getJtrac().bulkUpdateFieldToNull(space, field);
                                space.getMetadata().removeField(field.getName().getText());       
                                getJtrac().storeSpace(space);
                                // synchronize metadata version or else if we save again we get Stale Object Exception
                                space.setMetadata(getJtrac().loadMetadata(space.getMetadata().getId()));
                                setResponsePage(new SpaceFieldListPage(space, null, previous));
                            }                        
                        };
                        setResponsePage(confirm);
                    } else {
                        // this is an unsaved space or there are no impacted items
                        space.getMetadata().removeField(field.getName().getText());
                        setResponsePage(new SpaceFieldListPage(space, null, previous));
                    }
                }                
            };
            delete.setDefaultFormProcessing(false);
            if(!space.getMetadata().getFields().containsKey(field.getName())) {
                delete.setVisible(false);
            } 
            add(delete);
            // internal name ===================================================
            add(new Label("name", new PropertyModel(field, "name.text")));
            // label ===========================================================
            final TextField label = new TextField("field.label");
            label.setRequired(true);
            label.add(new ErrorHighlighter());            
            add(label);
            // optional ========================================================
            add(new CheckBox("field.optional"));            
            // options =========================================================
            WebMarkupContainer hide = new WebMarkupContainer("hide");
            if(field.getName().getType() < 4) { // drop down type
                final Map<String, String> optionsMap;
                if (field.getOptions() == null) {
                    optionsMap = new HashMap<String, String>();
                } else {
                    optionsMap = field.getOptions();
                }
                final List<String> options = new ArrayList(optionsMap.keySet());
                ListView listView = new ListView("options", options) {
                    protected void populateItem(ListItem listItem) {
                        final String key = (String) listItem.getModelObject();
                        listItem.add(new Label("key", key));
                        listItem.add(new Label("value", optionsMap.get(key)));
                        listItem.add(new Button("up") {
                            @Override
                            public void onSubmit() {                                
                                int index = options.indexOf(key);
                                int swapIndex = index - 1;
                                if (swapIndex < 0) {
                                    if (options.size() > 1) {
                                        swapIndex = options.size() - 1;
                                    } else {
                                        swapIndex = 0;
                                    }
                                }
                                if (index != swapIndex) {
                                    Collections.swap(options, index, swapIndex);
                                }
                                Map<String, String> updated = new LinkedHashMap<String, String>(options.size());
                                for (String s : options) {
                                    updated.put(s, optionsMap.get(s));
                                }
                                field.setOptions(updated);
                                setResponsePage(new SpaceFieldFormPage(space, field, previous));
                            }                            
                        });
                        listItem.add(new Button("down") {
                            @Override
                            public void onSubmit() {
                                int index = options.indexOf(key);
                                int swapIndex = index + 1;
                                if (swapIndex == options.size() ) {
                                    swapIndex = 0;
                                }
                                if (index != swapIndex) {
                                    Collections.swap(options, index, swapIndex);
                                }
                                Map<String, String> updated = new LinkedHashMap<String, String>(options.size());
                                for (String s : options) {
                                    updated.put(s, optionsMap.get(s));
                                }
                                field.setOptions(updated);
                                setResponsePage(new SpaceFieldFormPage(space, field, previous));
                            }                            
                        });
                        listItem.add(new Button("edit") {
                            @Override
                            public void onSubmit() {    
                                setResponsePage(new SpaceFieldOptionPage(space, field, key, previous));
                            }                       
                        });
                    }                    
                };                
                hide.add(listView);
                TextField option = new TextField("option");
                // validation, does option already exist?
                option.add(new AbstractValidator() {
                    public void validate(FormComponent c) {
                        String s = (String) c.getConvertedInput();
                        if(field.hasOption(s)) {
                            error(c);
                        }
                    }
                    @Override
                    protected String resourceKey(FormComponent c) {                    
                        return "space_field_form.error.optionExists";
                    }                
                });
                option.add(new ErrorHighlighter());
                hide.add(option);
                hide.add(new Button("add") {
                    @Override
                    public void onSubmit() {
                        Field f = onSubmitBefore();
                        setResponsePage(new SpaceFieldFormPage(space, f, previous));
                    }                     
                });
            } else {
                hide.setVisible(false);    
            }
            add(hide);
            // done ============================================================
            add(new Button("done") {
                @Override
                public void onSubmit() {
                    Field field = onSubmitBefore();
                    // may be clone, overwrite anyway
                    space.getMetadata().add(field);                    
                    setResponsePage(new SpaceFieldListPage(space, field.getName().getText(), previous));
                }
            });
            // cancel ==========================================================
            add(new Link("cancel") {
                public void onClick() {
                    setResponsePage(new SpaceFieldListPage(space, field.getName().getText(), previous));
                }                
            });            
        }
     
        @Override
        protected void validate() {
            filter.reset();
            super.validate();          
        }        
        
        private Field onSubmitBefore() {
            SpaceFieldFormModel model = (SpaceFieldFormModel) getModelObject();
            Field field = model.getField();
            String option = model.getOption();
            if(option != null) {
                field.addOption(option);
            }
            return field;
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
