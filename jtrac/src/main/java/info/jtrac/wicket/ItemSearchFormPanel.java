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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import wicket.Component;
import wicket.behavior.AbstractBehavior;
import wicket.markup.ComponentTag;
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
import wicket.model.PropertyModel;

/**
 * item search form panel
 */
public class ItemSearchFormPanel extends BasePanel {        
        
    public ItemSearchFormPanel(String id, Space space) {
        super(id);
        add(new ItemSearchForm("form", space));
    }       
    
    public ItemSearchFormPanel(String id, User user) {
        super(id);
        add(new ItemSearchForm("form", user));
    }      
    
    /**
     * here we are returning to the filter criteria screen from
     * the search results screen
     */
    public ItemSearchFormPanel(String id, ItemSearch itemSearch) {  
        super(id);
        itemSearch.setCurrentPage(0);        
        add(new ItemSearchForm("form", itemSearch));      
    }    
    
    private class SelectedHighlighter extends AbstractBehavior {
    
        FormComponent fc;        
        
        public SelectedHighlighter(FormComponent fc) {            
            this.fc = fc;
        }        
        
        @Override
        public void onComponentTag(Component c, ComponentTag tag) {
            Object o = fc.getModelObject();
            if(o == null) {
                return;
            }
            if(o instanceof Collection && ((Collection) o).size() == 0) {
                return;
            }
            tag.put("class", "selected");
        }
        
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
        
        private Component getSelecter(FormComponent fc) {
            return new WebMarkupContainer(fc.getId() + "Label").add(new SelectedHighlighter(fc));
        }                 
        
        private void addComponents() {
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(itemSearch);
            setModel(model);
            // feedback panel ==================================================
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter =  new JtracFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);            
            add(new Link("link") {
                public void onClick() {
                    ItemSearchFormPanel.this.replaceWith(new ItemRefIdFormPanel("panel"));
                }
            });            
            // summary / text search ===========================================            
            final TextField summary = new TextField("summary") {
                @Override
                public void onAttach() {
                    super.onAttach();
                    getWebPage().getBodyContainer().addOnLoadModifier(new AbstractReadOnlyModel() {
                        public Object getObject(Component ignored) {
                            return "document.getElementById('" + getMarkupId() + "').focus()";
                        }
                    }, this);
                }
            };
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
            add(getSelecter(summary));
            add(summary);
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
            add(new WebMarkupContainer("pageSizeLabel").add(new AbstractBehavior() {
                @Override
                public void onComponentTag(Component c, ComponentTag tag) {
                    if(itemSearch.getPageSize() != 25) {
                        tag.put("class", "selected");
                    }
                }                
            }));
            // show detail =====================================================
            add(new CheckBox("showDetail"));
            add(new WebMarkupContainer("showDetailLabel").add(new AbstractBehavior() {
                @Override
                public void onComponentTag(Component c, ComponentTag tag) {
                    if(itemSearch.isShowDetail()) {
                        tag.put("class", "selected");
                    }
                }                
            }));                      
            // show history ====================================================
            add(new CheckBox("showHistory"));
            add(new WebMarkupContainer("showHistoryLabel").add(new AbstractBehavior() {
                @Override
                public void onComponentTag(Component c, ComponentTag tag) {
                    if(itemSearch.isShowHistory()) {
                        tag.put("class", "selected");
                    }
                }                
            }));             
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
                sp.add(getSelecter(severityListChoice));
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
                sp.add(getSelecter(priorityListChoice));
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
            add(getSelecter(statusListChoice));
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
            add(getSelecter(loggedByChoice));
            // assignedTo ======================================================
            ListMultipleChoice assignedToChoice = new ListMultipleChoice("assignedToList", users, userChoiceRenderer);            
            add(assignedToChoice);
            add(getSelecter(assignedToChoice));
            // dates ===========================================================
            String createdDateLabel = getLocalizer().getString("item_search_form.createdDate", null);
            String modifiedDateLabel = getLocalizer().getString("item_search_form.historyUpdatedDate", null);            
            add(new DatePicker("createdDateStart", model, "createdDateStart", false, createdDateLabel));
            // TODO refactor this better when date picker component improves
            add(new WebMarkupContainer("createdDateStartLabel").add(new AbstractBehavior() {
                @Override
                public void onComponentTag(Component c, ComponentTag tag) {
                    if(itemSearch.getCreatedDateStart() != null) {
                        tag.put("class", "selected");
                    }
                }                
            }));
            add(new DatePicker("createdDateEnd", model, "createdDateEnd", false, createdDateLabel));
            add(new WebMarkupContainer("createdDateEndLabel").add(new AbstractBehavior() {
                @Override
                public void onComponentTag(Component c, ComponentTag tag) {
                    if(itemSearch.getCreatedDateEnd() != null) {
                        tag.put("class", "selected");
                    }
                }                
            }));
            add(new DatePicker("modifiedDateStart", model, "modifiedDateStart", false, modifiedDateLabel));
            add(new WebMarkupContainer("modifiedDateStartLabel").add(new AbstractBehavior() {
                @Override
                public void onComponentTag(Component c, ComponentTag tag) {
                    if(itemSearch.getModifiedDateStart() != null) {
                        tag.put("class", "selected");
                    }
                }                
            }));
            add(new DatePicker("modifiedDateEnd", model, "modifiedDateEnd", false, modifiedDateLabel));
            add(new WebMarkupContainer("modifiedDateEndLabel").add(new AbstractBehavior() {
                @Override
                public void onComponentTag(Component c, ComponentTag tag) {
                    if(itemSearch.getModifiedDateEnd() != null) {
                        tag.put("class", "selected");
                    }
                }                
            }));
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
                spaces.add(getSelecter(spaceChoice));
            } else {
                spaces.setVisible(false);
            }
            add(spaces);
            // custom drop downs ===============================================
            if (itemSearch.getSpace() != null) {
                ListView listView = new ListView("customDropDowns", itemSearch.getDropDownFields()) {
                    protected void populateItem(ListItem listItem) {
                        Field field = (Field) listItem.getModelObject();
                        final Map<String, String> options = field.getOptions();
                        List<Integer> optionKeys = new ArrayList<Integer>(options.size());
                        // the types have to match perfectly for binding TODO - remove JSP-ishness
                        for(String s : options.keySet()) {
                            optionKeys.add(new Integer(s));
                        }
                        final ListMultipleChoice fieldChoice = new ListMultipleChoice("field", optionKeys, new IChoiceRenderer() {
                            public Object getDisplayValue(Object o) {
                                return options.get(o.toString());
                            }
                            public String getIdValue(Object o, int i) {
                                return o.toString();
                            }
                        });
                        listItem.add(model.bind(fieldChoice, field.getName().getText() + "List"));
                        Label label = new Label("label", field.getLabel());
                        label.add(new AbstractBehavior() {
                            @Override
                            public void onComponentTag(Component c, ComponentTag tag) {
                                Object o = fieldChoice.getModelObject();
                                if(o != null && ((Collection) o).size() > 0) {
                                    tag.put("class", "selected");
                                }
                            }                
                        });
                        listItem.add(label);                        
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
                        final Field field = (Field) listItem.getModelObject();
                        Label label = new Label("label", field.getLabel());
                        listItem.add(label);
                        // TODO improve and specific to on/after or on/before
                        listItem.add(new WebMarkupContainer("startLabel").add(new AbstractBehavior() {
                            @Override
                            public void onComponentTag(Component c, ComponentTag tag) {
                                PropertyModel modelStart = new PropertyModel(itemSearch, field.getName().getText() + "Start");                                
                                if(modelStart.getObject(null) != null) {
                                    tag.put("class", "selected");
                                }
                            }                
                        }));
                        listItem.add(new WebMarkupContainer("endLabel").add(new AbstractBehavior() {
                            @Override
                            public void onComponentTag(Component c, ComponentTag tag) {
                                PropertyModel modelEnd = new PropertyModel(itemSearch, field.getName().getText() + "End");                                
                                if(modelEnd.getObject(null) != null) {                                    
                                    tag.put("class", "selected");
                                }
                            }                
                        }));                        
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
                        Label label = new Label("label", field.getLabel());
                        listItem.add(label);
                        final TextField textField = new TextField("field");
                        listItem.add(model.bind(textField, field.getName().getText()));
                        label.add(new AbstractBehavior() {
                            @Override
                            public void onComponentTag(Component c, ComponentTag tag) {
                                if(textField.getModelObject() != null) {
                                    tag.put("class", "selected");
                                }
                            }                
                        });
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
