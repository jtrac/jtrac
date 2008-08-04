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

import info.jtrac.domain.UserSpaceRole;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 * reused in space allocate and user allocate admin screens
 */
public class RoleDeAllocatePanel extends BasePanel {
    
    public RoleDeAllocatePanel(String id, List<UserSpaceRole> userSpaceRoles) {
        super(id);
        add(new ListView("roleKeys", userSpaceRoles) {                            
            protected void populateItem(ListItem roleKeyItem) {
                final UserSpaceRole usr = (UserSpaceRole) roleKeyItem.getModelObject();
                roleKeyItem.add(new Label("roleKey", usr.getRoleKey()));
                Button deallocate = new Button("deallocate") {
                    @Override
                    public void onSubmit() {
                        // avoid lazy loading problem
                        UserSpaceRole temp = getJtrac().loadUserSpaceRole(usr.getId());
                        if(temp == null) {
                            // very rare chance that this is a user in "guest mode"                            
                            return;
                        }
                        getJtrac().removeUserSpaceRole(temp);                                                                                      
                        JtracSession.get().refreshPrincipalIfSameAs(temp.getUser());
                        // TODO have some nice interface for this
                        if(getPage() instanceof UserAllocatePage) {
                            UserAllocatePage page = (UserAllocatePage) getPage();
                            setResponsePage(new UserAllocatePage(page.getUserId(), page.getPrevious()));
                        } else if(getPage() instanceof SpaceAllocatePage) {
                            SpaceAllocatePage page = (SpaceAllocatePage) getPage();
                            setResponsePage(new SpaceAllocatePage(page.getSpaceId(), page.getPrevious()));
                        }                        
                    }                   
                };
                // make it impossible to remove the first user ensuring there is always an admin
                if(usr.isSuperUser() && usr.getUser().getId() == 1) {
                    deallocate.setVisible(false);
                }
                // make it impossible to remove admin role for self
                if(usr.getUser().getId() == getPrincipal().getId() && usr.isSpaceAdmin()) {
                    deallocate.setVisible(false);
                }
                roleKeyItem.add(deallocate);                                 
            }                            
        });        
    }

}
