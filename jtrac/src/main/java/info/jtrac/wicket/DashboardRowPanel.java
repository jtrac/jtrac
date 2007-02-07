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
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import java.util.Collections;
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
    
    public DashboardRowPanel(String id, final UserSpaceRole usr, final Counts counts) {
        
        super(id);
        setOutputMarkupId(true);
        
        final Space space = usr.getSpace();
        final User user = usr.getUser();
        
        add(new Label("space", space.getName()));
        
        if(usr.isAbleToCreateNewItem()) {
            add(new Link("new") {
                public void onClick() {                
                    setResponsePage(new ItemFormPage(space));
                }
            });
        } else {
            add(new Label("new").setVisible(false));
        }

        add(new Link("search") {
            public void onClick() {
                setResponsePage(new ItemSearchFormPage(space));
            }
        });        
        
        add(new AjaxFallbackLink("link") {
            public void onClick(AjaxRequestTarget target) {
                Counts tempCounts = counts;                
                // avoid hitting the database again if re-expanding
                if (!tempCounts.isDetailed()) {                    
                    tempCounts = getJtrac().loadCountsForUserSpace(user, space);                    
                }
                DashboardRowExpandedPanel dashboardRow = new DashboardRowExpandedPanel("dashboardRow", usr, tempCounts);
                DashboardRowPanel.this.replaceWith(dashboardRow);
                target.addComponent(dashboardRow);
            }
        });          
        
        add(new Link("loggedByMe") {
            public void onClick() {
                ItemSearch itemSearch = new ItemSearch(space);
                itemSearch.setLoggedBySet(Collections.singleton(user.getId()));
                setResponsePage(new ItemListPage(itemSearch));
            }
        }.add(new Label("loggedByMe", new PropertyModel(counts, "loggedByMe"))));        
        
        
        add(new Link("assignedToMe") {
            public void onClick() {
                ItemSearch itemSearch = new ItemSearch(space);
                itemSearch.setAssignedToSet(Collections.singleton(user.getId()));
                setResponsePage(new ItemListPage(itemSearch));
            }
        }.add(new Label("assignedToMe", new PropertyModel(counts, "assignedToMe"))));
        
        add(new Link("total") {
            public void onClick() {
                ItemSearch itemSearch = new ItemSearch(space);                
                setResponsePage(new ItemListPage(itemSearch));
            }
        }.add(new Label("total", new PropertyModel(counts, "total"))));
      
    }
    
}
