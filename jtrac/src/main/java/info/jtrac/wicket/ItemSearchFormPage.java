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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.BoundCompoundPropertyModel;

/**
 * dashboard page
 */
public class ItemSearchFormPage extends BasePage {
      
    public ItemSearchFormPage(Space space) {
        
        super("Item Search");        
        add(new HeaderPanel(space));        
        border.add(new FeedbackPanel("feedback"));
        border.add(new ItemSearchForm("form", space));
        
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
            // sort column =====================================================
            List<String> sortFieldNames = new ArrayList<String>();
            if (itemSearch.getSpace() != null) {
                sortFieldNames.add("id");
            }
            for(Field field : itemSearch.getFields()) {
                sortFieldNames.add(field.getName().getText());
            }
            final Map<String, Field> fieldMap = itemSearch.getFieldMap();
            DropDownChoice sortFieldNameChoice = new DropDownChoice("sortFieldName", sortFieldNames, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    if (o.toString().equals("id")) {
                        return getLocalizer().getString("item_search_form.id", null);
                    }
                    return fieldMap.get(o.toString()).getLabel();
                }
                public String getIdValue(Object o, int i) {
                    return o.toString();
                }
            });
            add(sortFieldNameChoice);
            // sort descending =================================================
            add(new CheckBox("sortDescending"));
            // show detail =====================================================
            add(new CheckBox("showDetail"));
            // show history ====================================================
            add(new CheckBox("showHistory"));
            
        }
        
        @Override
        protected void onSubmit() {
            ItemSearch itemSearch = (ItemSearch) getModelObject();            
            setResponsePage(new ItemListPage(itemSearch));
        }        
            
    }
    
}
