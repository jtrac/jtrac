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

import info.jtrac.domain.Role;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

/**
 * user allocate page
 */
public class UserAllocatePage extends BasePage {
      
    private WebPage previous;
    private long userId;
    private long selectedSpaceId;

    public long getUserId() {
        return userId;
    }

    public WebPage getPrevious() {
        return previous;
    }        
    
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
        
    /**
     * wicket form
     */
    private class UserAllocateForm extends Form {                
        
        private User user;
        private Space space;              
                        
        private RoleAllocatePanel roleAllocatePanel;      
        private Button allocateButton;                
        
        private void initRoleChoice(Space space) {
            List<String> roleKeys = user.getRoleKeys(space);
            List<String> list = space.getMetadata().getAllRoleKeys();
            list.removeAll(roleKeys);
            // very rare chance that user is in guest mode
            // don't allow possibility of this getting saved to DB!
            list.remove(Role.ROLE_GUEST);
            // if super user, no need for space level admin option
            if(user.isSuperUser()) {
                list.remove(Role.ROLE_ADMIN);
            }
            roleAllocatePanel.setChoices(list);
            allocateButton.setEnabled(true);
        }        
        
        public UserAllocateForm(String id) {
            
            super(id);                                                         
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(this);
            setModel(model);
            
            add(new FeedbackPanel("feedback"));
            
            user = getJtrac().loadUser(userId);
            
            add(new Label("label", user.getName() + " (" + user.getLoginName() + ")"));
            
            final Map<Long, List<UserSpaceRole>> spaceRolesMap = getJtrac().loadSpaceRolesMapForUser(userId);   
            
            User principal = getPrincipal();
            // this flag is used later also for the big "make admin for all spaces" button
            boolean isPrincipalSuperUser = principal.isSuperUser();
            
            List<Long> allowedSpaces = new ArrayList<Long>();
            
            if(isPrincipalSuperUser) {
                allowedSpaces = new ArrayList(spaceRolesMap.keySet());
            } else {
                // session user is not an admin, remove spaces that he should not see                
                for(Space s : principal.getSpacesWhereRoleIsAdmin()) {
                    long spaceId = s.getId();
                    if(spaceRolesMap.containsKey(spaceId)) {
                        allowedSpaces.add(spaceId);
                    }
                }
            }
            
            final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
            
            add(new ListView("spaces", allowedSpaces) {
                protected void populateItem(ListItem listItem) {
                    long spaceId = (Long) listItem.getModelObject();    
                    List<UserSpaceRole> usrs = spaceRolesMap.get(spaceId);
                    // space can be null for "all spaces" role (prefixCode used in map = "")
                    final Space space = usrs.get(0).getSpace();                     
                    if(space != null && space.getId() == selectedSpaceId) {
                        listItem.add(new SimpleAttributeModifier("class", "selected"));
                    } else if(listItem.getIndex() % 2 == 1) {
                        listItem.add(sam);
                    }                                                                                                         
                    if(space == null) {                        
                        listItem.add(new Label("name", localize("user_allocate_space.allSpaces"))); 
                        listItem.add(new WebMarkupContainer("prefixCode").setVisible(false));                        
                    } else {
                        listItem.add(new Label("name", space.getName()));
                        listItem.add(new Link("prefixCode") {
                            public void onClick() {
                                if(previous instanceof SpaceAllocatePage) { // prevent recursive stack buildup
                                    previous = null;
                                }                                
                                setResponsePage(new SpaceAllocatePage(space.getId(), UserAllocatePage.this, userId));
                            }
                        }.add(new Label("prefixCode", space.getPrefixCode()))); 
                    }
                    listItem.add(new RoleDeAllocatePanel("roleDeAllocatePanel", usrs));                    
                }
            });                       
            
            List<Space> spaces = getJtrac().findSpacesNotFullyAllocatedToUser(user.getId());
            
            if(!isPrincipalSuperUser) {
                // not super user, show only spaces which can admin
                Set<Space> set = new HashSet(spaces);                
                List<Space> allowed = new ArrayList<Space>();
                // also within these spaces may be fully allocated, so trim
                for(Space s : principal.getSpacesWhereRoleIsAdmin()) {
                    if(set.contains(s)) {
                        allowed.add(s);
                    }
                }
                spaces = allowed;
            }
            
            DropDownChoice spaceChoice = new DropDownChoice("space", spaces, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    Space space = (Space) o;
                    return space.getName() + " [" + space.getPrefixCode() + "]";
                }
                public String getIdValue(Object o, int i) {
                    return ((Space) o).getId() + "";
                }
            });            
            spaceChoice.setNullValid(true);
            
            add(spaceChoice);
            
            spaceChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
                protected void onUpdate(AjaxRequestTarget target) {
                    Space s = (Space) getFormComponent().getConvertedInput();
                    if (s == null) {
                        roleAllocatePanel.setChoices(new ArrayList<String>());
                        allocateButton.setEnabled(false);
                    } else {
                        Space temp = getJtrac().loadSpace(s.getId());
                        // populate choice, enable button etc
                        initRoleChoice(temp);
                    }
                    target.addComponent(roleAllocatePanel);
                    target.addComponent(allocateButton);
                }
            });                                                
            
            roleAllocatePanel = new RoleAllocatePanel("roleAllocatePanel");                    
            roleAllocatePanel.setOutputMarkupId(true);                                  
            add(roleAllocatePanel);                                   
            
            allocateButton = new Button("allocate") {
                @Override
                public void onSubmit() {  
                    List<String> roleKeys = roleAllocatePanel.getSelected();
                    if(space == null || roleKeys.size() == 0) {
                        return;
                    }
                    for(String roleKey : roleKeys) {
                        getJtrac().storeUserSpaceRole(user, space, roleKey);
                    }                    
                    JtracSession.get().refreshPrincipalIfSameAs(user);
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
            
            // make admin ======================================================                                                    
            
            WebMarkupContainer makeAdmin = new WebMarkupContainer("makeAdmin");
            if(user.isSuperUser() || !isPrincipalSuperUser) {
                makeAdmin.setVisible(false);
            } else {
                makeAdmin.add(new Button("makeAdmin") {
                    @Override
                    public void onSubmit() {     
                        getJtrac().storeUserSpaceRole(user, null, Role.ROLE_ADMIN);
                        JtracSession.get().refreshPrincipalIfSameAs(user);
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
