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
import info.jtrac.domain.ItemUser;
import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.util.SecurityUtils;
import info.jtrac.util.UserUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import wicket.Component;
import wicket.feedback.FeedbackMessage;
import wicket.feedback.IFeedbackMessageFilter;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.ListMultipleChoice;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.upload.FileUpload;
import wicket.markup.html.form.upload.FileUploadField;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.markup.html.panel.Fragment;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.BoundCompoundPropertyModel;

/**
 * Create / Edit item form page
 */
public class ItemFormPage extends BasePage {        
    
    private MyFilter filter;
    
    private class MyFilter implements IFeedbackMessageFilter {
        
        private Set<String> previous = new HashSet<String>();
        
        public void reset() {
            previous.clear();
        }
        
        public boolean accept(FeedbackMessage fm) {
            if(!previous.contains(fm.getMessage())) {
                previous.add(fm.getMessage());
                return true;
            }
            return false;
        }
    }
    
    public ItemFormPage(Space space) {
        super("Edit Item");
        add(new HeaderPanel(space));
        Item item = new Item();
        item.setSpace(space);        
        FeedbackPanel feedback = new FeedbackPanel("feedback");
        filter = new MyFilter();
        feedback.setFilter(filter);
        border.add(feedback);        
        border.add(new ItemForm("form", item));
    }
    
    private class ItemForm extends Form {
        
        private FileUploadField fileUploadField;
        
        public ItemForm(String id, Item item) {
            super(id);
            setMultiPart(true);
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(item);
            setModel(model);
            final Component summaryField = new TextField("summary").setRequired(true).add(new ErrorHighlighter()).setOutputMarkupId(true);
            add(summaryField);
            ItemFormPage.this.getBodyContainer().addOnLoadModifier(new AbstractReadOnlyModel() {
                public Object getObject(Component component) {
                    return "document.getElementById('" + summaryField.getMarkupId() + "').focus()";
                }
            }, summaryField);
            add(new TextArea("detail").setRequired(true).add(new ErrorHighlighter()));
            // custom fields
            List<Field> fields = item.getSpace().getMetadata().getFieldList();
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
                        choice.add(new ErrorHighlighter());
                        choice.setLabel(new AbstractReadOnlyModel() {
                            public Object getObject(Component c) {
                                return field.getLabel();
                            }
                        });                        
                        if (!field.isOptional()) {
                            choice.setRequired(true);
                        }
                        f.add(model.bind(choice, field.getNameText()));
                        listItem.add(f);
                    } else if (field.getName().getType() == 6){ // date picker                        
                        listItem.add(new DatePicker("field", model, field));
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
                            public Object getObject(Component c) {
                                return field.getLabel();
                            }
                        });                         
                        f.add(model.bind(textField, field.getNameText()));
                        listItem.add(f);
                    }
                }
            };
            listView.setReuseItems(true);
            add(listView);
            Space space = item.getSpace();
            List<UserSpaceRole> userSpaceRoles = getJtrac().findUserRolesForSpace(space.getId());
            List<User> assignable = UserUtils.filterUsersAbleToTransitionFrom(userSpaceRoles, space, State.OPEN);
            DropDownChoice choice = new DropDownChoice("assignedTo", assignable, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return ((User) o).getName();
                }
                public String getIdValue(Object o, int i) {
                    return ((User) o).getId() + "";
                }                
            });
            choice.setNullValid(true);
            choice.setRequired(true);
            choice.add(new ErrorHighlighter());            
            add(choice);
            List<ItemUser> choices = UserUtils.convertToItemUserList(userSpaceRoles);
            ListMultipleChoice itemUsers = new JtracCheckBoxMultipleChoice("itemUsers", choices, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return ((ItemUser) o).getUser().getName();
                }
                public String getIdValue(Object o, int i) {
                    return ((ItemUser) o).getUser().getId() + "";
                }               
            });
            add(itemUsers);
            fileUploadField = new FileUploadField("file");
            // TODO file size limit
            add(fileUploadField);            
        }
        
        @Override
        protected void validate() {
            filter.reset();
            super.validate();
        }
        
        @Override
        protected void onSubmit() {
            final FileUpload fileUpload = fileUploadField.getFileUpload();
            Item item = (Item) getModelObject();
            User user = SecurityUtils.getPrincipal();
            item.setLoggedBy(user);
            item.setStatus(State.OPEN);
            getJtrac().storeItem(item, null);
            setResponsePage(new ItemViewPage(item));
        }
        
    }
    
}
