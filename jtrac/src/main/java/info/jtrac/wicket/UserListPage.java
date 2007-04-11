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

import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import java.util.ArrayList;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

/**
 * user management page
 */
public class UserListPage extends BasePage {
    
    private long selectedUserId;
    
    public void setSelectedUserId(long selectedUserId) {
        this.selectedUserId = selectedUserId;
    }
      
    public UserListPage() {              
        add(new Link("create") {
            public void onClick() {
                UserFormPage page = new UserFormPage();
                page.setPrevious(UserListPage.this);
                setResponsePage(page);
            }            
        });
        
        LoadableDetachableModel userListModel = new LoadableDetachableModel() {
            protected Object load() {
                logger.debug("loading user list from database");
                return getJtrac().findAllUsers();
            }
        };        
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
        ListView listView = new ListView("users", userListModel) {
            protected void populateItem(ListItem listItem) {                
                final User user = (User) listItem.getModelObject();                
                if (selectedUserId == user.getId()) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if(listItem.getIndex() % 2 == 1) {
                    listItem.add(sam);
                }                                 
                listItem.add(new Label("name", new PropertyModel(user, "name")));
                listItem.add(new Label("loginName", new PropertyModel(user, "loginName")));                                               
                listItem.add(new Label("email", new PropertyModel(user, "email")));
                listItem.add(new Label("locale", new PropertyModel(user, "locale")));
                listItem.add(new WebMarkupContainer("locked").setVisible(user.isLocked()));
                listItem.add(new Link("edit") {
                    public void onClick() {
                        UserFormPage page = new UserFormPage(user);
                        page.setPrevious(UserListPage.this);
                        setResponsePage(page);
                    }                    
                });                 
                listItem.add(new Link("allocate") {
                    public void onClick() {
                        setResponsePage(new UserAllocatePage(user.getId(), UserListPage.this));
                    }                    
                });
            }            
        };
        
        add(listView);
        
    }
    
}
