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
import info.jtrac.domain.Item;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.util.SecurityUtils;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxFallbackLink;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.model.PropertyModel;

/**
 * panel for showing the total (un-expanded) statistics for
 * a single space, will be replaced by expanded view through ajax
 */
public class DashboardRowPanel extends BasePanel {    
    
    public DashboardRowPanel(String id, final Counts counts) {
        
        super(id);
        setOutputMarkupId(true);      
        
        add(new Label("space", new PropertyModel(counts, "space.name")));
        
        add(new Link("new") {
            public void onClick() {
                // space instance held in Counts may have originated from Acegi
                // so incompatible with open session in view, get proper one                
                Space space = getJtrac().loadSpace(counts.getSpace().getId());
                Item item = new Item();
                item.setSpace(space);
                setResponsePage(new ItemFormPage(item));
            }
        });

        add(new Link("search") {
            public void onClick() {
                // space instance held in Counts may have originated from Acegi
                // so incompatible with open session in view, get proper one                
                Space space = getJtrac().loadSpace(counts.getSpace().getId());
                setResponsePage(new ItemSearchPage(space));
            }
        });        
        
        add(new AjaxFallbackLink("link") {
            public void onClick(AjaxRequestTarget target) {
                Counts temp = counts;  // get non-final instance
                User user = SecurityUtils.getPrincipal();
                if (!temp.isDetailed()) {
                    // space instance held in Counts may have originated from Acegi
                    // so incompatible with open session in view, get proper one
                    Space space = getJtrac().loadSpace(temp.getSpace().getId());
                    temp = getJtrac().loadCountsForUserSpace(user, space);
                }
                DashboardRowExpandedPanel dashboardRow = new DashboardRowExpandedPanel("dashboardRow", temp);
                DashboardRowPanel.this.replaceWith(dashboardRow);
                target.addComponent(dashboardRow);
            }
        });          
        
        add(new Label("loggedByMe", new PropertyModel(counts, "loggedByMe")));
        add(new Label("assignedToMe", new PropertyModel(counts, "assignedToMe")));
        add(new Label("total", new PropertyModel(counts, "total")));
      
    }
    
}
