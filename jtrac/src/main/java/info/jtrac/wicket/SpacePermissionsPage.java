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

import info.jtrac.domain.Field;
import info.jtrac.domain.Role;
import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import info.jtrac.domain.WorkflowRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import wicket.behavior.SimpleAttributeModifier;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.Form;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;

/**
 * space roles and workflow form
 */
public class SpacePermissionsPage extends BasePage {
      
    private WebPage previous;
    private Space space;
    
    private void addComponents() {        
        add(new SpacePermissionsForm("form"));
    }     
    
    public SpacePermissionsPage(Space space, WebPage previous) {
        this.space = space;
        this.previous = previous;
        addComponents();
    }
    
    private class SpacePermissionsForm extends Form {
        
       private JtracFeedbackMessageFilter filter;
        
        public SpacePermissionsForm(String id) {
            
            super(id);
            // label / heading =================================================
            add(new Label("label", space.getName() + " (" + space.getPrefixCode() + ")"));
            // states colspan ==================================================
            final Map<Integer, String> statesMap = space.getMetadata().getStates();
            SimpleAttributeModifier statesColspan = new SimpleAttributeModifier("colspan", (statesMap.size() - 1) + "");
            add(new WebMarkupContainer("statesColspan").add(statesColspan));
            // fields colspan ==================================================
            final List<Field> fields = space.getMetadata().getFieldList();
            SimpleAttributeModifier fieldsColspan = new SimpleAttributeModifier("colspan", fields.size() + "");
            add(new WebMarkupContainer("fieldsColspan").add(fieldsColspan));
            // add state =======================================================
            add(new Button("addState") {
                @Override
                public void onSubmit() {
                    setResponsePage(new SpaceStatePage(space, -1, previous));
                }
            }); 
            // add state =======================================================
            add(new Button("addRole") {
                @Override
                public void onSubmit() {
                    setResponsePage(new SpaceRolePage(space, null, previous));
                }
            });
            // states col headings =============================================
            final List<Integer> stateKeysNoNew = new ArrayList(statesMap.keySet());
            stateKeysNoNew.remove(State.NEW);
            add(new ListView("stateHeads", stateKeysNoNew) {
                protected void populateItem(ListItem listItem) {
                    Integer stateKey = (Integer) listItem.getModelObject();
                    listItem.add(new Label("state", statesMap.get(stateKey)));
                }
            });
            // fields col headings =============================================
            add(new ListView("fieldHeads", fields) {
                protected void populateItem(ListItem listItem) {
                    Field f = (Field) listItem.getModelObject();
                    listItem.add(new Label("field", f.getLabel()));
                }
            });
            // rows init =======================================================
            List<Integer> stateKeys = new ArrayList(statesMap.keySet());
            final List<Role> roles = new ArrayList(space.getMetadata().getRoleList());
            final SimpleAttributeModifier rowspan = new SimpleAttributeModifier("rowspan", roles.size() + "");
            final SimpleAttributeModifier yes = new SimpleAttributeModifier("src", "../resources/status-green.png");
            final SimpleAttributeModifier no = new SimpleAttributeModifier("src", "../resources/status-grey.png");            
            final SimpleAttributeModifier readonly = new SimpleAttributeModifier("src", "../resources/field-readonly.png");
            final SimpleAttributeModifier mandatory = new SimpleAttributeModifier("src", "../resources/field-mandatory.png");            
            final SimpleAttributeModifier optional = new SimpleAttributeModifier("src", "../resources/field-optional.png");
            final SimpleAttributeModifier hidden = new SimpleAttributeModifier("src", "../resources/field-hidden.png");
            //==================================================================
            add(new ListView("states", stateKeys) {               
                protected void populateItem(ListItem listItem) {
                    final boolean firstState = listItem.getIndex() == 0;
                    final String stateClass = listItem.getIndex() % 2 == 1 ? "bdr-bottom alt" : "bdr-bottom";
                    final Integer stateKeyRow = (Integer) listItem.getModelObject();
                    listItem.add(new ListView("roles", roles) {
                        protected void populateItem(ListItem listItem) {
                            String roleClass = listItem.getIndex() % 2 == 1 ? " alt" : "";
                            String lastRole = listItem.getIndex() == roles.size() - 1 ? " bdr-bottom" : "";
                            listItem.add(new SimpleAttributeModifier("class", "center" + roleClass + lastRole));                            
                            final Role role = (Role) listItem.getModelObject();
                            if(listItem.getIndex() == 0) {
                                SimpleAttributeModifier rowClass = new SimpleAttributeModifier("class", stateClass);
                                listItem.add(new Label("state", statesMap.get(stateKeyRow)).add(rowspan).add(rowClass));
                                WebMarkupContainer editState = new WebMarkupContainer("editState");
                                editState.add(rowspan).add(rowClass);
                                Button editStateButton = new Button("editState") {
                                    @Override
                                    public void onSubmit() {
                                        setResponsePage(new SpaceStatePage(space, stateKeyRow, previous));
                                    }                                    
                                };
                                editState.add(editStateButton);
                                if(stateKeyRow == State.NEW) { // user can customize state names, even for Closed
                                    editStateButton.setVisible(false);
                                }
                                listItem.add(editState);
                            } else {
                                listItem.add(new WebMarkupContainer("state").setVisible(false));
                                listItem.add(new WebMarkupContainer("editState").setVisible(false));
                            }
                            listItem.add(new Label("role", role.getName()));
                            Button editRoleButton = new Button("editRole") {
                                @Override
                                public void onSubmit() {
                                    setResponsePage(new SpaceRolePage(space, role.getName(), previous));
                                }                                
                            };
                            listItem.add(editRoleButton);
                            if(!firstState) {
                                editRoleButton.setVisible(false);
                            }
                            listItem.add(new ListView("stateHeads", stateKeysNoNew) {
                                protected void populateItem(ListItem listItem) {
                                    final Integer stateKeyCol = (Integer) listItem.getModelObject();
                                    Button stateButton = new Button("state") {
                                        @Override
                                        public void onSubmit() {
                                            space.getMetadata().toggleTransition(role.getName(), stateKeyRow, stateKeyCol);
                                            setResponsePage(new SpacePermissionsPage(space, previous));
                                        }                                          
                                    };
                                    if(stateKeyRow == State.NEW && stateKeyCol != State.OPEN) {
                                        stateButton.setVisible(false);
                                    }
                                    State state = role.getStates().get(stateKeyRow);
                                    if(state != null && state.getTransitions().contains(stateKeyCol)) {
                                        stateButton.add(yes);                                        
                                    } else {
                                        stateButton.add(no);
                                    }
                                    listItem.add(stateButton);                                    
                                }                                
                            });
                            listItem.add(new ListView("fieldHeads", fields) {
                                protected void populateItem(ListItem listItem) {
                                    final Field field = (Field) listItem.getModelObject();
                                    Button fieldButton = new Button("field") {
                                        @Override
                                        public void onSubmit() {
                                            space.getMetadata().switchMask(stateKeyRow, role.getName(), field.getName().getText());
                                            setResponsePage(new SpacePermissionsPage(space, previous));
                                        }                                          
                                    };
                                    State state = role.getStates().get(stateKeyRow);                                    
                                    int mask = state.getFields().get(field.getName());
                                    switch(mask) {
                                        case State.MASK_MANDATORY : fieldButton.add(mandatory); break;
                                        case State.MASK_OPTIONAL : fieldButton.add(optional); break;
                                        case State.MASK_READONLY : fieldButton.add(readonly); break;
                                        case State.MASK_HIDDEN : fieldButton.add(hidden); break;
                                    }
                                    listItem.add(fieldButton);                                   
                                }                                
                            });                            
                        }                        
                    });
                }                
            });
            // back ============================================================
            add(new Button("back") {
                @Override
                public void onSubmit() {
                    setResponsePage(new SpaceFieldListPage(space, null, previous));
                }
            });            
            // save ============================================================
            add(new Button("save") {
                @Override
                public void onSubmit() {
                    boolean isNewSpace = space.getId() == 0;
                    getJtrac().storeSpace(space);
                    // current user may be allocated to this space, and e.g. name could have changed
                    refreshPrincipal();
                    if(isNewSpace) {
                        setResponsePage(new SpaceAllocatePage(space.getId(), previous));
                    } else if(previous == null) {
                        setResponsePage(new OptionsPage());
                    } else {                        
                        if (previous instanceof SpaceListPage) {
                            ((SpaceListPage) previous).setSelectedSpaceId(space.getId());
                        }                      
                        setResponsePage(previous);
                    }                    
                }
            });
            // cancel ==========================================================
            add(new Link("cancel") {
                public void onClick() {
                    setResponsePage(previous);
                }                
            });
            WorkflowRenderer workflow = new WorkflowRenderer(space.getMetadata().getRoles(), space.getMetadata().getStates());
            add(new Label("workflow", workflow.getAsHtml()).setEscapeModelStrings(false));
        }                        
                
    }                
    
}
