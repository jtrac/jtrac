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

import info.jtrac.domain.Counts;
import info.jtrac.domain.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxFallbackLink;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.PropertyModel;

/**
 * panel for expanded view of statistics for a single space
 * some complication to handle a table with "rowspan" involved
 */
public class DashboardRowExpandedPanel extends BasePanel {    
    
    public DashboardRowExpandedPanel(String id, final Counts counts) {        
        
        super(id);
        setOutputMarkupId(true);
        
        final Map<Integer, String> states = new TreeMap(counts.getSpace().getMetadata().getStates());    
        states.remove(State.NEW);
        int rowspan = states.size() + 1; // add one totals row also
        SimpleAttributeModifier sam = new SimpleAttributeModifier("rowspan", rowspan + "");
        List<Integer> stateKeys = new ArrayList<Integer>(states.keySet());
        
        int first = stateKeys.get(0);
        
        add(new Label("space", counts.getSpace().getName()).add(sam));
        
        add(new Link("new") {
            public void onClick() {
                
            }
        }.add(sam));

        add(new Link("search") {
            public void onClick() {
                
            }
        }.add(sam));
        
        add(new AjaxFallbackLink("link") {
            public void onClick(AjaxRequestTarget target) {
                DashboardRowPanel dashboardRow = new DashboardRowPanel("dashboardRow", counts);
                DashboardRowExpandedPanel.this.replaceWith(dashboardRow);
                target.addComponent(dashboardRow);
            }
        }.add(sam));
        
        add(new Label("status", states.get(first)));
        add(new Label("loggedByMe", counts.getLoggedByMeForState(first)));
        add(new Label("assignedToMe", counts.getAssignedToMeForState(first)));
        add(new Label("total", counts.getTotalForState(first)));                      
        
        stateKeys.remove(0);
        
        add(new ListView("rows", stateKeys) {
            protected void populateItem(ListItem listItem) {
                Integer i = (Integer) listItem.getModelObject();
                listItem.add(new Label("status", states.get(i)));
                listItem.add(new Label("loggedByMe", counts.getLoggedByMeForState(i)));
                listItem.add(new Label("assignedToMe", counts.getAssignedToMeForState(i)));
                listItem.add(new Label("total", counts.getTotalForState(i)));                
            }
            
        });
        
        add(new Label("loggedByMeTotal", new PropertyModel(counts, "loggedByMe")));
        add(new Label("assignedToMeTotal", new PropertyModel(counts, "assignedToMe")));
        add(new Label("totalTotal", new PropertyModel(counts, "total")));
        
    }
    
}
