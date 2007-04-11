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

import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.BoundCompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

/**
 * space allocate page
 */
public class SpaceAllocatePage extends BasePage {
    
    private WebPage previous;
    private long spaceId;
    private long selectedUserId;

    public void setSelectedUserId(long selectedUserId) {
        this.selectedUserId = selectedUserId;
    }
    
    public long getSpaceId() {
        return spaceId;
    }

    public WebPage getPrevious() {
        return previous;
    }    
    
    public SpaceAllocatePage(long spaceId, WebPage previous) {        
        this.spaceId = spaceId;
        this.previous = previous;        
        add(new SpaceAllocateForm("form"));
    }
    
    public SpaceAllocatePage(long spaceId, WebPage previous, long selectedUserId) {
        this.spaceId = spaceId;
        this.previous = previous;
        this.selectedUserId = selectedUserId;
        add(new SpaceAllocateForm("form"));        
    }    
    
    private class SpaceAllocateForm extends Form {
        
        private User user;
        private String roleKey;

        public String getRoleKey() {
            return roleKey;
        }

        public void setRoleKey(String roleKey) {
            this.roleKey = roleKey;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }        
                
        public SpaceAllocateForm(String id) {
            
            super(id);
                                    
            if(selectedUserId > 0) {
                // pre-select newly created user for convenience
                user = getJtrac().loadUser(selectedUserId);
            }
            
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(this);
            setModel(model);
            
            final Space space = getJtrac().loadSpace(spaceId);
            
            add(new Label("label", space.getName() + " (" + space.getPrefixCode() + ")"));
            
            LoadableDetachableModel usrsModel = new LoadableDetachableModel() {
                protected Object load() {
                    logger.debug("loading user space roles list from database");
                    return getJtrac().findUserRolesForSpace(spaceId);
                }
            };                                    
            
            final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
            
            add(new ListView("usrs", usrsModel) {
                protected void populateItem(ListItem listItem) {
                    final UserSpaceRole usr = (UserSpaceRole) listItem.getModelObject();
                    if(selectedUserId == usr.getUser().getId()) {
                        listItem.add(new SimpleAttributeModifier("class", "selected"));
                    } else if(listItem.getIndex() % 2 == 1) {
                        listItem.add(sam);
                    }                                         
                    listItem.add(new Link("loginName") {
                        public void onClick() {                            
                            if(previous instanceof UserAllocatePage) { // prevent recursive stack buildup
                                previous = null;
                            }                               
                            setResponsePage(new UserAllocatePage(usr.getUser().getId(), SpaceAllocatePage.this, spaceId));
                        }
                    }.add(new Label("loginName", new PropertyModel(usr, "user.loginName"))));
                    listItem.add(new Label("name", new PropertyModel(usr, "user.name")));
                    listItem.add(new Label("roleKey", new PropertyModel(usr, "roleKey")));
                    listItem.add(new Button("deallocate") {
                        @Override
                        public void onSubmit() {
                            // avoid lazy loading problem
                            UserSpaceRole temp = getJtrac().loadUserSpaceRole(usr.getId());
                            getJtrac().removeUserSpaceRole(temp);
                            refreshPrincipal(temp.getUser());
                            setResponsePage(new SpaceAllocatePage(spaceId, previous));
                        }
                    });
                }
            });
            
            add(new Link("createNewUser") {
                @Override
                public void onClick() {
                    UserFormPage page = new UserFormPage();
                    page.setPrevious(SpaceAllocatePage.this);
                    setResponsePage(page);
                }
            });
            
            List<User> users = getJtrac().findUnallocatedUsersForSpace(space.getId());
            
            if(user == null && users.size() == 1) {
                // pre select the user for convenience
                user = users.get(0);
            }            
            
            DropDownChoice userChoice = new DropDownChoice("user", users, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    User u = (User) o;
                    return u.getName() + " (" + u.getLoginName() + ")";
                }
                public String getIdValue(Object o, int i) {
                    return ((User) o).getId() + "";
                }
            });
            userChoice.setNullValid(true);

            add(userChoice);
            
            List<String> roleKeys = new ArrayList(space.getMetadata().getRoles().keySet());
            
            if(roleKeys.size() == 1) {
                // pre select the role for convenience
                roleKey = roleKeys.get(0);
            }
            
            DropDownChoice roleKeyChoice = new DropDownChoice("roleKey", roleKeys);
            roleKeyChoice.setNullValid(true);
            add(roleKeyChoice);
            
            add(new Button("allocate") {
                @Override
                public void onSubmit() {                    
                    if(user == null || roleKey == null) {
                        return;
                    }
                    // avoid lazy init problem
                    User temp = getJtrac().loadUser(user.getId());
                    getJtrac().storeUserSpaceRole(temp, space, roleKey);
                    refreshPrincipal(temp);
                    setResponsePage(new SpaceAllocatePage(spaceId, previous, user.getId()));
                }
            });
            
            // cancel ==========================================================
            add(new Link("cancel") {
                public void onClick() {
                    if(previous == null) {
                        setResponsePage(SpaceListPage.class);
                        return;
                    }
                    if(previous instanceof SpaceListPage) {
                        ((SpaceListPage) previous).setSelectedSpaceId(space.getId());
                    }
                    if(previous instanceof UserAllocatePage) {
                        ((UserAllocatePage) previous).setSelectedSpaceId(space.getId());
                    }                    
                    setResponsePage(previous);
                }
            });
        }
        
    }
    
    
    
}
