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
import info.jtrac.domain.Space;
import info.jtrac.domain.State;
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
public class SpaceRolesPage extends BasePage {
      
    private WebPage previous;
    private Space space;
    
    private void addComponents() {        
        add(new HeaderPanel(null)); 
        border.add(new SpaceRolesForm("form"));
    }     
    
    public SpaceRolesPage(Space space, WebPage previous) {
        super("Edit Roles");
        this.space = space;
        this.previous = previous;
        addComponents();
    }
    
    private class SpaceRolesForm extends Form {
        
       private JtracFeedbackMessageFilter filter;
        
        public SpaceRolesForm(String id) {
            
            super(id);
            // label / heading =================================================
            add(new Label("label", space.getName() + " (" + space.getPrefixCode() + ")"));
            // states colspan ==================================================
            final Map<Integer, String> statesMap = space.getMetadata().getStates();
            SimpleAttributeModifier statesColspan = new SimpleAttributeModifier("colspan", (statesMap.size() - 1) + "");
            add(new WebMarkupContainer("statesColspan").add(statesColspan));
            // fields colspan ==================================================
            List<Field> fields = space.getMetadata().getFieldList();
            SimpleAttributeModifier fieldsColspan = new SimpleAttributeModifier("colspan", fields.size() + "");
            add(new WebMarkupContainer("fieldsColspan").add(fieldsColspan));
            // add state =======================================================
            add(new Button("addState") {
                @Override
                protected void onSubmit() {

                }
            }); 
            // add state =======================================================
            add(new Button("addRole") {
                @Override
                protected void onSubmit() {

                }
            });
            // states col headings =============================================
            List<Integer> stateKeys = new ArrayList(statesMap.keySet());            
            stateKeys.remove(State.NEW);
            add(new ListView("states", stateKeys) {
                protected void populateItem(ListItem listItem) {                    
                    listItem.add(new Label("state", statesMap.get(listItem.getModelObject())));
                }
            });
            // fields col headings =============================================
            add(new ListView("fields", fields) {
                protected void populateItem(ListItem listItem) {
                    Field f = (Field) listItem.getModelObject();
                    listItem.add(new Label("field", f.getLabel()));
                }
            });            
            // back ============================================================
            add(new Button("back") {
                @Override
                protected void onSubmit() {
                    setResponsePage(new SpaceFieldListPage(space, null, previous));
                }
            });            
            // save ============================================================
            add(new Button("save") {
                @Override
                protected void onSubmit() {

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
