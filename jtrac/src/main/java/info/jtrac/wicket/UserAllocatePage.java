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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.BoundCompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 * user allocate page
 */
public class UserAllocatePage extends BasePage {
      
    private WebPage previous;
    private long userId;
    private long selectedSpaceId;    

    public void setSelectedSpaceId(long selectedSpaceId) {
        this.selectedSpaceId = selectedSpaceId;
    }    
    
    public UserAllocatePage(long userId, WebPage previous) {
        this.userId = userId;
        this.previous = previous;        
        add(new UserAllocateForm("form"));
    }
    
    public UserAllocatePage(long userId, WebPage previous, long selectedSpaceId) {
        this.userId = userId;
        this.previous = previous;
        this.selectedSpaceId = selectedSpaceId;
        add(new UserAllocateForm("form"));
    }    
    
    private class UserAllocateForm extends Form {                
        
        private Space space;
        private String roleKey;

        public Space getSpace() {
            return space;
        }

        public void setSpace(Space space) {
            this.space = space;
        }

        public String getRoleKey() {
            return roleKey;
        }

        public void setRoleKey(String roleKey) {
            this.roleKey = roleKey;
        }        
                        
        private DropDownChoice roleKeyChoice;        
        private Button allocateButton;
                
        /**
         * function that attempts to pre-select roleKey for convenience
         * used on form init and also on Ajax onChange event for Space choice
         */
        private void initRoleChoice(Space space) {
            List<String> roleKeys = new ArrayList(space.getMetadata().getRoles().keySet());
            if(roleKeys.size() == 1) {
                // pre select role for convenience
                roleKey = roleKeys.get(0);
            }
            roleKeyChoice.setChoices(roleKeys);                    
            roleKeyChoice.setEnabled(true);
            allocateButton.setEnabled(true);            
        }
        
        public UserAllocateForm(String id) {
            
            super(id);                                                         
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(this);
            setModel(model);
            
            add(new FeedbackPanel("feedback"));
            
            final User user = getJtrac().loadUser(userId);
            
            add(new Label("label", user.getName() + " (" + user.getLoginName() + ")"));
            
            List<UserSpaceRole> usrs = new ArrayList(user.getUserSpaceRoles());
            
            final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
            
            add(new ListView("usrs", usrs) {
                protected void populateItem(ListItem listItem) {
                    final UserSpaceRole usr = (UserSpaceRole) listItem.getModelObject();
                    if(usr.getSpace() != null && usr.getSpace().getId() == selectedSpaceId) {
                        listItem.add(new SimpleAttributeModifier("class", "selected"));
                    } else if(listItem.getIndex() % 2 == 1) {
                        listItem.add(sam);
                    }                                        
                    WebMarkupContainer spaceSpan = new WebMarkupContainer("space");
                    listItem.add(spaceSpan);
                    if(usr.getSpace() == null) {                        
                        spaceSpan.setVisible(false);                    
                    } else {
                        spaceSpan.add(new Label("name", usr.getSpace().getName()));
                        spaceSpan.add(new Link("prefixCode") {
                            public void onClick() {
                                if(previous instanceof SpaceAllocatePage) { // prevent recursive stack buildup
                                    previous = null;
                                }                                
                                setResponsePage(new SpaceAllocatePage(usr.getSpace().getId(), UserAllocatePage.this, userId));
                            }
                        }.add(new Label("prefixCode", usr.getSpace().getPrefixCode())));
                    } 
                    listItem.add(new Label("roleKey", new PropertyModel(usr, "roleKey")));
                    Button deallocate = new Button("deallocate") {
                        @Override
                        public void onSubmit() {
                            getJtrac().removeUserSpaceRole(usr);
                            refreshPrincipal(usr.getUser());
                            setResponsePage(new UserAllocatePage(userId, previous));
                        }                   
                    };
                    // make it impossible to remove the first user ensuring there is always an admin
                    if(usr.getUser().getId() == 1 && "ROLE_ADMIN".equals(usr.getRoleKey())) {
                        deallocate.setVisible(false);
                    }
                    listItem.add(deallocate);
                }
            });                       
            
            List<Space> spaces = getJtrac().findUnallocatedSpacesForUser(user.getId());            
            
            DropDownChoice spaceChoice = new DropDownChoice("space", spaces, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return ((Space) o).getName();
                }
                public String getIdValue(Object o, int i) {
                    return ((Space) o).getId() + "";
                }
            });            
            spaceChoice.setNullValid(true);
            
            roleKeyChoice = new DropDownChoice("roleKey");            
            roleKeyChoice.setOutputMarkupId(true);
            roleKeyChoice.setEnabled(false);
            roleKeyChoice.setRequired(true);
            roleKeyChoice.setNullValid(true);
            roleKeyChoice.add(new ErrorHighlighter());
            add(roleKeyChoice);
            
            allocateButton = new Button("allocate") {
                @Override
                public void onSubmit() {                    
                    if(space == null || roleKey == null) {
                        return;
                    }
                    getJtrac().storeUserSpaceRole(user, space, roleKey);
                    refreshPrincipal(user);
                    setResponsePage(new UserAllocatePage(userId, previous, space.getId()));
                }                   
            };
            allocateButton.setOutputMarkupId(true);
            allocateButton.setEnabled(false);
            add(allocateButton);           
            
            if(spaces.size() == 1) {
                // pre select space for convenience
                space = spaces.get(0);
                // see if the role can be pre selected also at least populate choice, enable button etc
                initRoleChoice(space);
            }            
            
            spaceChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
                protected void onUpdate(AjaxRequestTarget target) {
                    Space space = (Space) getFormComponent().getConvertedInput();
                    if (space == null) {
                        roleKeyChoice.setEnabled(false);
                        allocateButton.setEnabled(false);
                    } else {
                        Space temp = getJtrac().loadSpace(space.getId());
                        // populate choice, enable button etc
                        initRoleChoice(temp);
                    }
                    target.addComponent(roleKeyChoice);
                    target.addComponent(allocateButton);
                }
            });            
            
            add(spaceChoice);                                
            
            WebMarkupContainer makeAdmin = new WebMarkupContainer("makeAdmin");
            if(user.isAdminForAllSpaces()) {
                makeAdmin.setVisible(false);
            } else {
                makeAdmin.add(new Button("makeAdmin") {
                    @Override
                    public void onSubmit() {     
                        getJtrac().storeUserSpaceRole(user, null, "ROLE_ADMIN");
                        refreshPrincipal(user);
                        setResponsePage(new UserAllocatePage(userId, previous));
                    }                   
                });
            }
            add(makeAdmin);
            
            // cancel ==========================================================
            add(new Link("cancel") {              
                public void onClick() {
                    if(previous == null) {
                        setResponsePage(UserListPage.class);
                        return;
                    }                   
                    if(previous instanceof UserListPage) {
                        ((UserListPage) previous).setSelectedUserId(userId);
                    } 
                    if(previous instanceof SpaceAllocatePage) {
                        ((SpaceAllocatePage) previous).setSelectedUserId(userId);
                    }                     
                    setResponsePage(previous);
                }                
            });            
        }              
                        
    }        
        

    
}
