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
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxFallbackLink;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.PropertyModel;

/**
 * panel for expanded view of statistics for a single space 
 */
public class DashboardRowExpandedPanel extends BasePanel {    
    
    public DashboardRowExpandedPanel(String id, final UserSpaceRole usr, final Counts counts) {        
        
        super(id);
        setOutputMarkupId(true);
        
        final Space space = usr.getSpace();
        final User user = usr.getUser();
        
        final Map<Integer, String> states = new TreeMap(space.getMetadata().getStates());    
        states.remove(State.NEW);
        int rowspan = states.size() + 1; // add one totals row also
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("rowspan", rowspan + "");
        List<Integer> stateKeys = new ArrayList<Integer>(states.keySet());                                                
        
        add(new ListView("rows", stateKeys) {
            
            protected void populateItem(ListItem listItem) {                                
                
                if (listItem.getIndex() == 0) { // rowspan output only for first row            
                    
                    listItem.add(new Label("space", space.getName()).add(sam));
                                        
                    WebMarkupContainer newColumn = new WebMarkupContainer("new");
                    newColumn.add(sam);   
                    listItem.add(newColumn);
                    
                    if(usr.isAbleToCreateNewItem()) {
                        newColumn.add(new Link("new") {
                            public void onClick() {
                                setCurrentSpace(space);
                                setResponsePage(new ItemFormPage(space));
                            }
                        });

                    } else {
                        newColumn.add(new WebMarkupContainer("new").setVisible(false));
                    }

                    listItem.add(new Link("search") {
                        public void onClick() {
                            setCurrentSpace(space);
                            setResponsePage(new ItemSearchFormPage(space));
                        }
                    }.add(sam));

                    listItem.add(new AjaxFallbackLink("link") {
                        public void onClick(AjaxRequestTarget target) {
                            DashboardRowPanel dashboardRow = new DashboardRowPanel("dashboardRow", usr, counts);
                            DashboardRowExpandedPanel.this.replaceWith(dashboardRow);
                            target.addComponent(dashboardRow);
                        }
                    }.add(sam)); 
                    
                } else {
                    listItem.add(new WebMarkupContainer("space").setVisible(false));
                    listItem.add(new WebMarkupContainer("new").setVisible(false));
                    listItem.add(new WebMarkupContainer("search").setVisible(false));
                    listItem.add(new WebMarkupContainer("link").setVisible(false));
                }
                
                final Integer i = (Integer) listItem.getModelObject();
                listItem.add(new Label("status", states.get(i)));
                
                if(user.getId() > 0) {                
                    listItem.add(new Link("loggedByMe") {
                        public void onClick() {
                            ItemSearch itemSearch = new ItemSearch(space);
                            itemSearch.setLoggedByList(Collections.singletonList(user));
                            itemSearch.setStatusList(Collections.singletonList(i));
                            setResponsePage(new ItemListPage(itemSearch));
                        }
                    }.add(new Label("loggedByMe", counts.getLoggedByMeForState(i))));

                    listItem.add(new Link("assignedToMe") {
                        public void onClick() {
                            ItemSearch itemSearch = new ItemSearch(space);
                            itemSearch.setAssignedToList(Collections.singletonList(user));
                            itemSearch.setStatusList(Collections.singletonList(i));
                            setResponsePage(new ItemListPage(itemSearch));
                        }
                    }.add(new Label("assignedToMe", counts.getAssignedToMeForState(i))));
                } else {
                    listItem.add(new WebMarkupContainer("loggedByMe").setVisible(false));
                    listItem.add(new WebMarkupContainer("assignedToMe").setVisible(false));                    
                }
                
                listItem.add(new Link("total") {
                    public void onClick() {
                        ItemSearch itemSearch = new ItemSearch(space);                        
                        itemSearch.setStatusList(Collections.singletonList(i));
                        setResponsePage(new ItemListPage(itemSearch));
                    }
                }.add(new Label("total", counts.getTotalForState(i))));                
            }
            
        });
        
        // sub totals ==========================================================
        
        if(user.getId() > 0) {        
            add(new Link("loggedByMeTotal") {
                public void onClick() {
                    ItemSearch itemSearch = new ItemSearch(space);
                    itemSearch.setLoggedByList(Collections.singletonList(user));
                    setResponsePage(new ItemListPage(itemSearch));
                }
            }.add(new Label("loggedByMe", new PropertyModel(counts, "loggedByMe"))));

            add(new Link("assignedToMeTotal") {
                public void onClick() {
                    ItemSearch itemSearch = new ItemSearch(space);
                    itemSearch.setAssignedToList(Collections.singletonList(user));
                    setResponsePage(new ItemListPage(itemSearch));
                }
            }.add(new Label("assignedToMe", new PropertyModel(counts, "assignedToMe"))));
        } else {
            add(new WebMarkupContainer("loggedByMeTotal").setVisible(false));
            add(new WebMarkupContainer("assignedToMeTotal").setVisible(false));               
        }
        
        add(new Link("totalTotal") {
            public void onClick() {
                ItemSearch itemSearch = new ItemSearch(space);                
                setResponsePage(new ItemListPage(itemSearch));
            }
        }.add(new Label("total", new PropertyModel(counts, "total"))));
        
    }
    
}
