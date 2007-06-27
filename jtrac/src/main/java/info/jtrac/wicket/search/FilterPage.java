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

package info.jtrac.wicket.search;

import info.jtrac.domain.ColumnHeading;
import info.jtrac.domain.Field;
import info.jtrac.domain.FilterCriteria;
import info.jtrac.wicket.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class FilterPage extends BasePage {
    
    Map<String, FilterCriteria> map = new LinkedHashMap<String, FilterCriteria>();
    
    FilterCriteria filterCriteria = new FilterCriteria(null);
    
    public FilterPage() {
        final Form form = new Form("form");        
        add(form);
        form.setModel(new CompoundPropertyModel(filterCriteria));
        List<ColumnHeading> columnHeadings = filterCriteria.getColumnHeadings(getCurrentSpace());        
        DropDownChoice columnChoice = new DropDownChoice("columnHeading", columnHeadings, new IChoiceRenderer() {
            public Object getDisplayValue(Object o) {
                return ((ColumnHeading) o).getName();
            }
            public String getIdValue(Object o, int i) {
                return ((ColumnHeading) o).getName();
            }
        });         
        form.add(columnChoice);
        final AjaxListView listView = new AjaxListView("filters");
        form.add(new AjaxButton("add") {
            protected void onSubmit(AjaxRequestTarget target, Form unused) {
                if(filterCriteria.getColumnHeading() == null) {
                    return;
                }
                Item newItem = listView.addItem();                
                target.prependJavascript("var myTr = document.createElement('tr');"                        
                        + " myTr.id = '" + newItem.getMarkupId() + "';"
                        + " document.getElementById('container').appendChild(myTr);");                
                target.addComponent(newItem);                
            }            
        });
        form.add(listView);
    }
    
    public class AjaxListView extends RefreshingView {                
        
        public AjaxListView(String id) {
            super(id);            
        }
        
        public Item addItem() {
            String uniqueId = newChildId();
            Item item = newItem(uniqueId, map.size(), new Model(uniqueId));
            map.put(uniqueId, new FilterCriteria(filterCriteria.getColumnHeading()));
            populateItem(item);
            add(item);
            return item;
        }
        
        public void removeItem(Item item) {
            map.remove(item.getModelObject());            
        }
        
        protected Iterator getItemModels() {
            List<IModel> models = new ArrayList<IModel>();
            for(String s : map.keySet()) {
                models.add(new Model(s));
            }
            return models.iterator();
        }
        
        protected void populateItem(final Item item) {
            FilterCriteria filterCriteria = map.get(item.getModelObject());
            item.add(new Label("columnHeading", filterCriteria.getColumnHeading().getName()));
            item.add((new AjaxButton("remove") {
                protected void onSubmit(AjaxRequestTarget target, Form form) {
                    AjaxListView.this.removeItem(item);                
                    target.appendJavascript("var myTr = document.getElementById('" + item.getMarkupId() + "');"
                            + " myTr.parentNode.removeChild(myTr);");
                }
            })); 
            item.setOutputMarkupId(true);
        }        
    }
    
}
