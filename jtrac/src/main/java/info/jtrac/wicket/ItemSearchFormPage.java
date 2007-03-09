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
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.util.UserUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import wicket.Component;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.FormComponent;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.ListMultipleChoice;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.validation.AbstractValidator;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.BoundCompoundPropertyModel;

/**
 * dashboard page
 */
public class ItemSearchFormPage extends BasePage {        
    
    
    public ItemSearchFormPage(Space space) {                              
        add(new ItemSearchForm("form", space));        
    }
    
    public ItemSearchFormPage(User user) {               
        add(new ItemSearchForm("form", user));        
    }    
    
    /**
     * here we are returning to the filter criteria screen from
     * the search results screen
     */
    public ItemSearchFormPage(ItemSearch itemSearch) {        
        itemSearch.setCurrentPage(0);        
        add(new ItemSearchForm("form", itemSearch));      
    }    

    private class ItemSearchForm extends Form {
        
        private ItemSearch itemSearch;        
        private JtracFeedbackMessageFilter filter;        
        
        public ItemSearchForm(String id, User user) {
            super(id);
            itemSearch = new ItemSearch(user);
            addComponents();
        }        
        
        public ItemSearchForm(String id, Space space) {
            super(id);
            itemSearch = new ItemSearch(space);
            addComponents();
        }
        
        public ItemSearchForm(String id, ItemSearch itemSearch) {
            super(id);
            this.itemSearch = itemSearch;
            addComponents();
        }                    
        
        private void addComponents() {
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(itemSearch);
            setModel(model);
            // feedback panel ==================================================
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter =  new JtracFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);            
            // summary / text search ===========================================            
            final TextField summary = new TextField("summary");
            summary.setOutputMarkupId(true);
            // validation: is Lucene search query ok?
            summary.add(new AbstractValidator() {
                public void validate(FormComponent c) {
                    String s = (String) c.getConvertedInput();                    
                    if(s != null && !getJtrac().validateTextSearchQuery(s)) {
                        error(c);
                    }
                }
                @Override
                protected String resourceKey(FormComponent c) {                    
                    return "item_search_form.error.summary.invalid";
                } 
            });
            summary.add(new ErrorHighlighter());
            add(summary);
            ItemSearchFormPage.this.getBodyContainer().addOnLoadModifier(new AbstractReadOnlyModel() {
                public Object getObject(Component ignored) {
                    return "document.getElementById('" + summary.getMarkupId() + "').focus()";
                }
            }, summary);
            add(new Link("link") {
                public void onClick() {
                    setResponsePage(ItemRefIdFormPage.class);
                }
            });
            // page size =======================================================
            List<Integer> sizes = Arrays.asList(new Integer[] { 5, 10, 15, 25, 50, 100, -1 });
            final String noLimit = getLocalizer().getString("item_search_form.noLimit", null);
            DropDownChoice pageSizeChoice = new DropDownChoice("pageSize", sizes, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return ((Integer) o) == -1 ? noLimit : o.toString();
                }
                public String getIdValue(Object o, int i) {
                    return o.toString();
                }
            });
            add(pageSizeChoice);
            // show detail =====================================================
            add(new CheckBox("showDetail"));
            // show history ====================================================
            add(new CheckBox("showHistory"));
            // severity / priority =============================================
            WebMarkupContainer sp = new WebMarkupContainer("severityPriority");
            if (itemSearch.getSpace() == null) {                
                final Map<String, String> severityMap = itemSearch.getSeverityOptions();
                List<Integer> severityList = new ArrayList(severityMap.size());
                for(String s : severityMap.keySet()) {
                    severityList.add(new Integer(s));
                }
                ListMultipleChoice severityListChoice = new ListMultipleChoice("severityList", severityList, new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        return severityMap.get(o.toString());
                    }
                    public String getIdValue(Object o, int i) {
                        return o.toString();
                    }                
                });            
                sp.add(severityListChoice);
                final Map<String, String> priorityMap = itemSearch.getPriorityOptions();
                List<Integer> priorityList = new ArrayList(priorityMap.size());
                for(String s : priorityMap.keySet()) {
                    priorityList.add(new Integer(s));
                }
                ListMultipleChoice priorityListChoice = new ListMultipleChoice("priorityList", priorityList, new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        return priorityMap.get(o.toString());
                    }
                    public String getIdValue(Object o, int i) {
                        return o.toString();
                    }                
                });            
                sp.add(priorityListChoice);
            } else {
                sp.setVisible(false);
            }
            add(sp);
            // status ==========================================================           
            final Map<Integer, String> statusMap = itemSearch.getStatusOptions();
            List<Integer> statusList = new ArrayList(statusMap.keySet());
            ListMultipleChoice statusListChoice = new ListMultipleChoice("statusList", statusList, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return statusMap.get(o);
                }
                public String getIdValue(Object o, int i) {
                    return o.toString();
                }                
            });            
            add(statusListChoice);
            // =================================================================
            List<User> users = null;
            if (itemSearch.getSpace() == null) {
                User user = getPrincipal();
                users = getJtrac().findUsersForUser(user);
            } else {
                users = getJtrac().findUsersForSpace(itemSearch.getSpace().getId());
            }
            IChoiceRenderer userChoiceRenderer = new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return ((User) o).getName();
                }
                public String getIdValue(Object o, int i) {
                    return ((User) o).getId() + "";
                }                
            };
            // loggedBy ========================================================
            ListMultipleChoice loggedByChoice = new ListMultipleChoice("loggedByList", users, userChoiceRenderer);
            add(loggedByChoice);
            // assignedTo ======================================================
            ListMultipleChoice assignedToChoice = new ListMultipleChoice("assignedToList", users, userChoiceRenderer);            
            add(assignedToChoice);
            // dates ===========================================================
            String createdDateLabel = getLocalizer().getString("item_search_form.createdDate", null);
            String modifiedDateLabel = getLocalizer().getString("item_search_form.historyUpdatedDate", null);
            add(new DatePicker("createdDateStart", model, "createdDateStart", false, createdDateLabel));
            add(new DatePicker("createdDateEnd", model, "createdDateEnd", false, createdDateLabel));
            add(new DatePicker("modifiedDateStart", model, "modifiedDateStart", false, modifiedDateLabel));
            add(new DatePicker("modifiedDateEnd", model, "modifiedDateEnd", false, modifiedDateLabel));
            // spaces ===========================================================
            WebMarkupContainer spaces = new WebMarkupContainer("spaces");
            if (itemSearch.getSpace() == null) {
                final Map<Long, String> spaceOptions = UserUtils.getSpaceNamesMap(itemSearch.getUser());
                List<Long> spaceIds = new ArrayList(spaceOptions.keySet());
                ListMultipleChoice spaceChoice = new ListMultipleChoice("spaceList", spaceIds, new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        return spaceOptions.get(o);
                    }
                    public String getIdValue(Object o, int i) {
                        return o.toString();
                    }
                });
                spaces.add(spaceChoice);
            } else {
                spaces.setVisible(false);
            }
            add(spaces);
            // custom drop downs ===============================================
            if (itemSearch.getSpace() != null) {
                ListView listView = new ListView("customDropDowns", itemSearch.getDropDownFields()) {
                    protected void populateItem(ListItem listItem) {
                        Field field = (Field) listItem.getModelObject();
                        listItem.add(new Label("label", field.getLabel()));
                        final Map<String, String> options = field.getOptions();
                        List<Integer> optionKeys = new ArrayList<Integer>(options.size());
                        // the types have to match perfectly for binding TODO - remove JSP-ishness
                        for(String s : options.keySet()) {
                            optionKeys.add(new Integer(s));
                        }
                        ListMultipleChoice spaceChoice = new ListMultipleChoice("field", optionKeys, new IChoiceRenderer() {
                            public Object getDisplayValue(Object o) {
                                return options.get(o.toString());
                            }
                            public String getIdValue(Object o, int i) {
                                return o.toString();
                            }
                        });
                        listItem.add(model.bind(spaceChoice, field.getName().getText() + "List"));
                    }                    
                };
                add(listView);
            } else {
                WebMarkupContainer customDropDowns = new WebMarkupContainer("customDropDowns");
                customDropDowns.setVisible(false);
                add(customDropDowns);
            }
            // custom dates ====================================================
            if (itemSearch.getSpace() != null) {
                ListView listView = new ListView("customDates", itemSearch.getDateFields()) {
                    protected void populateItem(ListItem listItem) {
                        Field field = (Field) listItem.getModelObject();
                        listItem.add(new Label("label", field.getLabel()));
                        listItem.add(new DatePicker("fieldStart", model, field.getName().getText() + "Start", false, field.getLabel()));
                        listItem.add(new DatePicker("fieldEnd", model, field.getName().getText() + "End", false, field.getLabel()));
                    }                    
                };
                listView.setReuseItems(true);
                add(listView);
            } else {
                WebMarkupContainer customDates = new WebMarkupContainer("customDates");
                customDates.setVisible(false);
                add(customDates);
            }
            // custom text =====================================================
            if (itemSearch.getSpace() != null) {
                ListView listView = new ListView("customTexts", itemSearch.getTextFields()) {
                    protected void populateItem(ListItem listItem) {
                        Field field = (Field) listItem.getModelObject();
                        listItem.add(new Label("label", field.getLabel()));
                        TextField textField = new TextField("field");
                        listItem.add(model.bind(textField, field.getName().getText()));
                    }                    
                };
                listView.setReuseItems(true);
                add(listView);
            } else {
                WebMarkupContainer customTexts = new WebMarkupContainer("customTexts");
                customTexts.setVisible(false);
                add(customTexts);
            }            
        }
        
        @Override
        protected void validate() {
            filter.reset();
            super.validate();          
        }         
        
        @Override
        protected void onSubmit() {
            ItemSearch itemSearch = (ItemSearch) getModelObject();            
            setResponsePage(new ItemListPage(itemSearch));
        }        
            
    }
    
}
