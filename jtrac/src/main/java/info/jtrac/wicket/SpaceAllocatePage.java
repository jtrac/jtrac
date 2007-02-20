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
import info.jtrac.util.SecurityUtils;
import java.util.ArrayList;
import java.util.List;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.BoundCompoundPropertyModel;
import wicket.model.PropertyModel;

/**
 * space allocate page
 */
public class SpaceAllocatePage extends BasePage {
      
    private WebPage previous;
    private Space space;        
    
    public SpaceAllocatePage(Space s, WebPage previous) {
        super("Edit State");
        this.space = getJtrac().loadSpace(s.getId());
        this.previous = previous;
        add(new HeaderPanel(null));
        border.add(new SpaceAllocateForm("form"));
    }
    
    private class SpaceAllocateForm extends Form {                        
        
        public SpaceAllocateForm(String id) {
            
            super(id);                                
            
            UserSpaceRole usr = new UserSpaceRole();            
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(usr);
            setModel(model);
            
            add(new Label("label", space.getName() + " (" + space.getPrefixCode() + ")"));
            
            List<UserSpaceRole> usrs = getJtrac().findUserRolesForSpace(space.getId());
            
            final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
            
            add(new ListView("usrs", usrs) {
                protected void populateItem(ListItem listItem) {
                    if(listItem.getIndex() % 2 == 1) {
                        listItem.add(sam);
                    }                    
                    final UserSpaceRole usr = (UserSpaceRole) listItem.getModelObject();
                    listItem.add(new Label("loginName", new PropertyModel(usr, "user.loginName")));
                    listItem.add(new Label("name", new PropertyModel(usr, "user.name")));
                    listItem.add(new Label("roleKey", new PropertyModel(usr, "roleKey")));
                    listItem.add(new Button("deallocate") {
                        @Override
                        protected void onSubmit() {
                            // avoid lazy loading problem
                            UserSpaceRole temp = getJtrac().loadUserSpaceRole(usr.getId());
                            getJtrac().removeUserSpaceRole(temp);
                            SecurityUtils.refreshSecurityContextIfPrincipal(temp.getUser());
                            setResponsePage(new SpaceAllocatePage(space, previous));
                        }                   
                    });
                }
            });
            
            add(new Button("createNewUser") {
                @Override
                protected void onSubmit() {     
                    UserFormPage page = new UserFormPage();
                    page.setPrevious(SpaceAllocatePage.this);
                    setResponsePage(page);
                }                   
            });            
            
            List<User> users = getJtrac().findUnallocatedUsersForSpace(space.getId());
            
            DropDownChoice userChoice = new DropDownChoice("user", users, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return ((User) o).getName();
                }
                public String getIdValue(Object o, int i) {
                    return ((User) o).getId() + "";
                }
            });            
            userChoice.setNullValid(true);                        
            add(userChoice);
                        
            List<String> roleKeys = new ArrayList(space.getMetadata().getRoles().keySet());
            
            DropDownChoice roleKeyChoice = new DropDownChoice("roleKey", roleKeys);
            roleKeyChoice.setNullValid(true);
            add(roleKeyChoice);
            
            add(new Button("allocate") {
                @Override
                protected void onSubmit() {     
                    UserSpaceRole usr = (UserSpaceRole) SpaceAllocateForm.this.getModelObject();
                    if(usr.getUser() == null || usr.getRoleKey() == null) {
                        return;
                    }
                    // avoid lazy init problem
                    User temp = getJtrac().loadUser(usr.getUser().getId());
                    getJtrac().storeUserSpaceRole(temp, space, usr.getRoleKey());
                    SecurityUtils.refreshSecurityContextIfPrincipal(temp);
                    setResponsePage(new SpaceAllocatePage(space, previous));
                }                   
            });            
            
            // cancel ==========================================================
            add(new Link("cancel") {
                public void onClick() {
                    setResponsePage(previous);
                }                
            });            
        }              
                        
    }        
        

    
}
