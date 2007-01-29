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

import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.ListMultipleChoice;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.markup.html.panel.Fragment;
import wicket.model.BoundCompoundPropertyModel;

/**
 * dashboard page
 */
public class ItemSearchFormPage extends BasePage {
     
    public class JtracListMultipleChoice extends ListMultipleChoice {
        
        public JtracListMultipleChoice(String id, List choices, IChoiceRenderer renderer) {
            super(id, choices, renderer);
        } 
    
        @Override
        protected java.lang.Object convertValue(String[] ids) {            
            List list = (List) super.convertValue(ids);
            return new HashSet(list);
        }        
    }
    
    public ItemSearchFormPage(Space space) {
        
        super("Item Search");        
        add(new HeaderPanel(space));        
        border.add(new FeedbackPanel("feedback"));
        border.add(new ItemSearchForm("form", space));
        
    }
    
    public ItemSearchFormPage(User user) {
        
        super("Item Search");        
        add(new HeaderPanel(null));        
        border.add(new FeedbackPanel("feedback"));
        border.add(new ItemSearchForm("form", user));
        
    }    
    
    /**
     * here we are returning to the filter criteria screen from
     * the search results screen
     */
    public ItemSearchFormPage(ItemSearch itemSearch) {
        
        super("Item Search");
        add(new HeaderPanel(itemSearch.getSpace()));
        itemSearch.setCurrentPage(0);
        border.add(new FeedbackPanel("feedback"));
        border.add(new ItemSearchForm("form", itemSearch));
        
    }    

    private class ItemSearchForm extends Form {
        
        private ItemSearch itemSearch;
        
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
            if (itemSearch.getSpace() == null) {
                Fragment f = new Fragment("severityPriority", "severityPriority");
                final Map<String, String> severityMap = itemSearch.getSeverityOptions();
                List<Integer> severityList = new ArrayList(severityMap.size());
                for(String s : severityMap.keySet()) {
                    severityList.add(new Integer(s));
                }
                ListMultipleChoice severitySetChoice = new JtracListMultipleChoice("severitySet", severityList, new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        return severityMap.get(o.toString());
                    }
                    public String getIdValue(Object o, int i) {
                        return o.toString();
                    }                
                });            
                f.add(severitySetChoice);
                final Map<String, String> priorityMap = itemSearch.getPriorityOptions();
                List<Integer> priorityList = new ArrayList(priorityMap.size());
                for(String s : priorityMap.keySet()) {
                    priorityList.add(new Integer(s));
                }
                ListMultipleChoice prioritySetChoice = new JtracListMultipleChoice("prioritySet", priorityList, new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        return priorityMap.get(o.toString());
                    }
                    public String getIdValue(Object o, int i) {
                        return o.toString();
                    }                
                });            
                f.add(prioritySetChoice);
                add(f);
            } else {
                add(new Label("severityPriority", ""));
            }
            // status ==========================================================
            final Map<Integer, String> statusMap = itemSearch.getStatusOptions();
            List<Integer> statusList = new ArrayList(statusMap.keySet());
            ListMultipleChoice statusSetChoice = new JtracListMultipleChoice("statusSet", statusList, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return statusMap.get(o);
                }
                public String getIdValue(Object o, int i) {
                    return o.toString();
                }                
            });            
            add(statusSetChoice);
        }
        
        @Override
        protected void onSubmit() {
            ItemSearch itemSearch = (ItemSearch) getModelObject();            
            setResponsePage(new ItemListPage(itemSearch));
        }        
            
    }
    
}
