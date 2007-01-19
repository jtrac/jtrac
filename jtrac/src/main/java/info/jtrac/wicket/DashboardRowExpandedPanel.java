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
import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxFallbackLink;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;

/**
 * panel for expanded view of statistics for a single space
 * some complication to handle a table with "rowspan" involved
 */
public class DashboardRowExpandedPanel extends BasePanel {    
    
    public DashboardRowExpandedPanel(String id, final Counts counts) {        
        
        super(id);
        setOutputMarkupId(true);    

        SimpleAttributeModifier sam = new SimpleAttributeModifier("rowspan", counts.getTotalMap().size() + "");
        
        Map<Integer, String> states = new TreeMap(counts.getSpace().getMetadata().getStates());    
        states.remove(State.NEW);        
        List<Integer> stateKeys = new ArrayList<Integer>(states.keySet());
        
        int first = stateKeys.get(0);
        
        add(new Label("space", "SPACE").add(sam));
        add(new Label("new", "NEW").add(sam));
        add(new Label("search", "SEARCH").add(sam));
        
        add(new AjaxFallbackLink("link") {
            public void onClick(AjaxRequestTarget target) {
                DashboardRowPanel dashboardRow = new DashboardRowPanel("dashboardRow", counts);
                DashboardRowExpandedPanel.this.replaceWith(dashboardRow);
                target.addComponent(dashboardRow);
            }
        }.add(sam));
        
        add(new Label("status", first + ""));
        add(new Label("loggedByMe", counts.getLoggedByMeMap().get(first) + ""));
        add(new Label("assignedToMe", counts.getAssignedToMeMap().get(first) + ""));
        add(new Label("total", counts.getTotalMap().get(first) + ""));                      
        
        stateKeys.remove(0);
        
        add(new ListView("rows", stateKeys) {
            protected void populateItem(ListItem listItem) {
                Integer i = (Integer) listItem.getModelObject();
                listItem.add(new Label("status", i + ""));
                listItem.add(new Label("loggedByMe", counts.getLoggedByMeMap().get(i) + ""));
                listItem.add(new Label("assignedToMe", counts.getAssignedToMeMap().get(i) + ""));
                listItem.add(new Label("total", counts.getTotalMap().get(i) + ""));                
            }
            
        });
        
    }
    
}
