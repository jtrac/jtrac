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
import info.jtrac.domain.CountsHolder;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.util.SecurityUtils;
import java.util.Collections;
import org.springframework.util.StringUtils;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Fragment;
import wicket.model.PropertyModel;

/**
 * dashboard page
 */
public class DashboardPage extends BasePage {
    
    public DashboardPage() {
        
        super("Dashboard");
        setVersioned(false);
        
        add(new HeaderPanel(null));
                
        final User user = SecurityUtils.getPrincipal();
        if(user.getLocale()!= null && !getLocale().getDisplayName().equals(user.getLocale())) {
            getSession().setLocale(StringUtils.parseLocaleString(user.getLocale()));
        }
        final CountsHolder countsHolder = getJtrac().loadCountsForUser(user);        
        
        WebMarkupContainer hideLogged = new WebMarkupContainer("hideLogged");
        WebMarkupContainer hideAssigned = new WebMarkupContainer("hideAssigned");
        if(user.getId() == 0) {
            hideLogged.setVisible(false);
            hideAssigned.setVisible(false);
        }
        border.add(hideLogged);
        border.add(hideAssigned);
        
        border.add(new ListView("dashboardRows", user.getSpaceRoles()) {
            protected void populateItem(final ListItem listItem) {
                UserSpaceRole usr = (UserSpaceRole) listItem.getModelObject();
                Counts counts = countsHolder.getCounts().get(usr.getSpace().getId());
                if (counts == null) {
                    counts = new Counts(false); // this can happen if fresh space
                }
                DashboardRowPanel dashboardRow = new DashboardRowPanel("dashboardRow", usr, counts);
                listItem.add(dashboardRow);
            }
        });
        
        WebMarkupContainer total = new WebMarkupContainer("total");
        
        if(user.getSpaceRoles().size() > 1) {
            
            total.add(new Link("search") {
                public void onClick() {
                    setResponsePage(new ItemSearchFormPage(user));
                }
            });
            
            if(user.getId() > 0) {            
                total.add(new Link("loggedByMe") {
                    public void onClick() {
                        ItemSearch itemSearch = new ItemSearch(user);
                        itemSearch.setLoggedBySet(Collections.singleton(user.getId()));
                        setResponsePage(new ItemListPage(itemSearch));
                    }
                }.add(new Label("loggedByMe", new PropertyModel(countsHolder, "totalLoggedByMe"))));

                total.add(new Link("assignedToMe") {
                    public void onClick() {
                        ItemSearch itemSearch = new ItemSearch(user);
                        itemSearch.setAssignedToSet(Collections.singleton(user.getId()));
                        setResponsePage(new ItemListPage(itemSearch));
                    }
                }.add(new Label("assignedToMe", new PropertyModel(countsHolder, "totalAssignedToMe"))));
            } else {
                total.add(new WebMarkupContainer("loggedByMe").setVisible(false));
                total.add(new WebMarkupContainer("assignedToMe").setVisible(false));
            }
            
            total.add(new Link("total") {
                public void onClick() {
                    ItemSearch itemSearch = new ItemSearch(user);                    
                    setResponsePage(new ItemListPage(itemSearch));
                }
            }.add(new Label("total", new PropertyModel(countsHolder, "totalTotal"))));
                       
        } else {             
            total.setVisible(false);
        }   
        
        border.add(total); 
        
    }
    
}
