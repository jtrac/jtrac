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
import info.jtrac.util.ValidationUtils;
import java.io.Serializable;
import java.util.List;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.FormComponent;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.validation.AbstractValidator;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.BoundCompoundPropertyModel;

/**
 * space role add / edit form
 */
public class SpaceRolePage extends BasePage {
      
    private WebPage previous;
    private Space space;        
    
    public SpaceRolePage(Space space, String roleKey, WebPage previous) {
        this.space = space;
        this.previous = previous;
        add(new SpaceRoleForm("form", roleKey));
    }
    
    private class SpaceRoleForm extends Form {                
        
        private String roleKey;
        
        public SpaceRoleForm(String id, final String roleKey) {
            
            super(id);          
            add(new FeedbackPanel("feedback"));            
            this.roleKey = roleKey;
            
            SpaceRoleModel modelObject = new SpaceRoleModel();                                
            modelObject.setRoleKey(roleKey);
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(modelObject);
            setModel(model);
            
            add(new Label("label", roleKey));
            
            // delete ==========================================================
            Button delete = new Button("delete") {
                @Override
                public void onSubmit() {
                    List<User> users = getJtrac().findUsersWithRoleForSpace(space.getId(), roleKey);
                    int affectedCount = users.size();
                    if (affectedCount > 0) {
                        String heading = localize("space_role_delete.confirm") + " : " + roleKey;
                        String warning = localize("space_role_delete.line3");
                        String line1 = localize("space_role_delete.line1", space.getName());
                        String line2 = localize("space_role_delete.line2");                        
                        ConfirmPage confirm = new ConfirmPage(SpaceRolePage.this, heading, warning, new String[] {line1, line2}) {
                            public void onConfirm() {                                        
                                getJtrac().bulkUpdateDeleteSpaceRole(space, roleKey);
                                space.getMetadata().removeRole(roleKey);     
                                getJtrac().storeSpace(space);
                                // synchronize metadata else when we save again we get Stale Object Exception
                                space.setMetadata(getJtrac().loadMetadata(space.getMetadata().getId()));
                                // current user may be allocated to this space with this role - refresh
                                refreshPrincipal();                                
                                setResponsePage(new SpacePermissionsPage(space, previous));
                            }                        
                        };
                        setResponsePage(confirm);
                    } else {
                        // this is an unsaved space / field or there are no impacted items
                        space.getMetadata().removeRole(roleKey);
                        setResponsePage(new SpacePermissionsPage(space, previous));
                    }
                }                
            };
            delete.setDefaultFormProcessing(false);
            if(space.getMetadata().getRoleCount() <= 1) {
                delete.setEnabled(false);
            }
            add(delete);
            // option ===========================================================
            final TextField field = new TextField("roleKey");
            field.setRequired(true);
            field.add(new ErrorHighlighter());
            // validation: format ok?
            field.add(new AbstractValidator() {
                public void validate(FormComponent c) {
                    String s = (String) c.getConvertedInput();
                    if(!ValidationUtils.isAllUpperCase(s)) {
                        error(c);
                    }
                }
                @Override
                protected String resourceKey(FormComponent c) {                    
                    return "space_role_form.error.role.invalid";
                }                
            });
            // validation: already exists?
            field.add(new AbstractValidator() {
                public void validate(FormComponent c) {
                    String s = (String) c.getConvertedInput();
                    if(space.getMetadata().getRoles().containsKey(s)) {
                        error(c);
                    }
                }
                @Override
                protected String resourceKey(FormComponent c) {                    
                    return "space_role_form.error.role.exists";
                }                
            });            
            add(field);
            // cancel ==========================================================
            add(new Link("cancel") {
                public void onClick() {
                    setResponsePage(new SpacePermissionsPage(space, previous));
                }                
            });            
        }
                
        @Override
        protected void onSubmit() {                    
            final SpaceRoleModel model = (SpaceRoleModel) getModelObject();
            if (roleKey == null) {
                space.getMetadata().addRole(model.getRoleKey());
                setResponsePage(new SpacePermissionsPage(space, previous));
            } else if (!roleKey.equals(model.getRoleKey())) {
                if (space.getId() > 0) {
                    String heading = localize("space_role_form_confirm.confirm", roleKey, model.getRoleKey());
                    String warning = localize("space_role_form_confirm.line2");
                    String line1 = localize("space_role_form_confirm.line1");                                   
                    ConfirmPage confirm = new ConfirmPage(SpaceRolePage.this, heading, warning, new String[] {line1}) {
                        public void onConfirm() {                                        
                            getJtrac().bulkUpdateRenameSpaceRole(space, roleKey, model.getRoleKey());
                            space.getMetadata().renameRole(roleKey, model.getRoleKey());     
                            getJtrac().storeSpace(space);
                            // synchronize metadata else when we save again we get Stale Object Exception
                            space.setMetadata(getJtrac().loadMetadata(space.getMetadata().getId()));
                            // current user may be allocated to this space with this role - refresh
                            refreshPrincipal();                                
                            setResponsePage(new SpacePermissionsPage(space, previous));
                        }                        
                    };
                    setResponsePage(confirm);                    
                } else {
                    space.getMetadata().renameRole(roleKey, model.getRoleKey());
                    setResponsePage(new SpacePermissionsPage(space, previous));
                }
            } else {
                setResponsePage(new SpacePermissionsPage(space, previous));
            }
            
        }     
                        
    }        
        
    /**
     * custom form backing object that wraps role key
     * required for the create / edit use case
     */
    private class SpaceRoleModel implements Serializable {
                
        private String roleKey;

        public String getRoleKey() {
            return roleKey;
        }

        public void setRoleKey(String roleKey) {
            this.roleKey = roleKey;
        }
               
    }
    
}
