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
import info.jtrac.domain.State;
import info.jtrac.util.ValidationUtils;
import java.io.Serializable;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.BoundCompoundPropertyModel;
import wicket.validation.IValidatable;
import wicket.validation.validator.AbstractValidator;

/**
 * space state add / edit form
 */
public class SpaceStatePage extends BasePage {
      
    private WebPage previous;
    private Space space;        
    
    public SpaceStatePage(Space space, int stateKey, WebPage previous) {
        this.space = space;
        this.previous = previous;
        add(new SpaceStateForm("form", stateKey));
    }
    
    private class SpaceStateForm extends Form {                
        
        private int stateKey;
        
        public SpaceStateForm(String id, final int stateKey) {
            
            super(id);          
            add(new FeedbackPanel("feedback"));            
            this.stateKey = stateKey;
            
            SpaceStateModel modelObject = new SpaceStateModel();
            // stateKey is -1 if add new state
            final String stateName = space.getMetadata().getStates().get(stateKey);            
            modelObject.setStateName(stateName);
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(modelObject);
            setModel(model);
            
            add(new Label("label", stateName));
            
            // delete ==========================================================
            Button delete = new Button("delete") {
                @Override
                public void onSubmit() {
                    int affectedCount = getJtrac().loadCountOfRecordsHavingStatus(space, stateKey);
                    if (affectedCount > 0) {
                        String heading = localize("space_state_delete.confirm") + " : " + stateName;
                        String warning = localize("space_state_delete.line3");
                        String line1 = localize("space_state_delete.line1");
                        String line2 = localize("space_state_delete.line2", affectedCount + "");                        
                        ConfirmPage confirm = new ConfirmPage(SpaceStatePage.this, heading, warning, new String[] {line1, line2}) {
                            public void onConfirm() {                                        
                                getJtrac().bulkUpdateStatusToOpen(space, stateKey);
                                space.getMetadata().removeState(stateKey);      
                                getJtrac().storeSpace(space);
                                // synchronize metadata else when we save again we get Stale Object Exception
                                space.setMetadata(getJtrac().loadMetadata(space.getMetadata().getId()));
                                setResponsePage(new SpacePermissionsPage(space, previous));
                            }                        
                        };
                        setResponsePage(confirm);
                    } else {
                        // this is an unsaved space / field or there are no impacted items
                        space.getMetadata().removeState(stateKey);
                        setResponsePage(new SpacePermissionsPage(space, previous));
                    }
                }                
            };
            delete.setDefaultFormProcessing(false);
            if(stateKey == State.OPEN || stateKey == -1) {
                delete.setEnabled(false);
            }            
            add(delete);
            // option ===========================================================
            final TextField field = new TextField("stateName");
            field.setRequired(true);
            field.add(new ErrorHighlighter());
            // validation: format ok?
            field.add(new AbstractValidator() {
                protected void onValidate(IValidatable v) {
                    String s = (String) v.getValue();
                    if(!ValidationUtils.isCamelDashCase(s)) {
                        error(v);
                    }
                }
                @Override
                protected String resourceKey() {                    
                    return "space_state_form.error.state.invalid";
                }                
            });
            // validation: already exists?
            field.add(new AbstractValidator() {
                protected void onValidate(IValidatable v) {
                    String s = (String) v.getValue();
                    if(space.getMetadata().getStates().containsValue(s)) {
                        error(v);
                    }
                }
                @Override
                protected String resourceKey() {                    
                    return "space_state_form.error.state.exists";
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
            SpaceStateModel model = (SpaceStateModel) getModelObject();
            if (stateKey == -1) {
                space.getMetadata().addState(model.getStateName());
            } else {
                space.getMetadata().getStates().put(stateKey, model.getStateName());
            }            
            setResponsePage(new SpacePermissionsPage(space, previous));
        }     
                        
    }        
        
    /**
     * custom form backing object that wraps state key
     * required for the create / edit use case
     */
    private class SpaceStateModel implements Serializable {
                
        private String stateName;

        public String getStateName() {
            return stateName;
        }

        public void setStateName(String stateName) {
            this.stateName = stateName;
        }        
               
    }
    
}
