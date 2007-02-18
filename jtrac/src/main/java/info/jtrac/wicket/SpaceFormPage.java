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
import java.io.Serializable;
import java.util.List;
import wicket.Component;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.WebPage;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.FormComponent;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.validation.AbstractValidator;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.BoundCompoundPropertyModel;

/**
 * space edit form
 */
public class SpaceFormPage extends BasePage {
      
    private WebPage previous;            

    public void setPrevious(WebPage previous) {
        this.previous = previous;
    }   
    
    private void addComponents(Space space) {
        add(new HeaderPanel(null)); 
        border.add(new SpaceForm("form", space));
    }
    
    public SpaceFormPage() {
        super("Edit Space");   
        Space space = new Space();    
        space.getMetadata().initRoles();
        addComponents(space);
    }    
    
    public SpaceFormPage(Space space) {
        super("Edit Space");        
        addComponents(space);
    }
    
    private class SpaceForm extends Form {
        
       private JtracFeedbackMessageFilter filter;
        
        public SpaceForm(String id, final Space space) {
            
            super(id);
            
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new JtracFeedbackMessageFilter();
            feedback.setFilter(filter);            
            add(feedback);
            
            SpaceFormModel modelObject = new SpaceFormModel();
            modelObject.setSpace(space);
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(modelObject);
            setModel(model);
            
            // delete button only if edit ======================================
            Button delete = new Button("delete") {
                @Override
                protected void onSubmit() {
                    String heading = localize("space_delete.confirm");
                    String warning = localize("space_delete.line3");
                    String line1 = localize("space_delete.line1");
                    String line2 = localize("space_delete.line2");
                    ConfirmPage confirm = new ConfirmPage(SpaceFormPage.this, heading, warning, new String[] {line1, line2}) {
                        public void onConfirm() {
                            getJtrac().removeSpace(space);
                            setResponsePage(new SpaceListPage());
                        }                        
                    };
                    setResponsePage(confirm);
                }                
            };
            delete.setDefaultFormProcessing(false);
            if(space.getId() <= 0) {
                delete.setVisible(false);
            } 
            add(delete);
            // display name ====================================================
            final TextField name = new TextField("space.name");
            name.setRequired(true);
            name.add(new ErrorHighlighter());
            name.setOutputMarkupId(true);
            SpaceFormPage.this.getBodyContainer().addOnLoadModifier(new AbstractReadOnlyModel() {
                public Object getObject(Component c) {
                    return "document.getElementById('" + name.getMarkupId() + "').focus()";
                }
            }, name);
            add(name);
            // prefix Code =====================================================
            TextField prefixCode = new TextField("space.prefixCode");
            prefixCode.setRequired(true);
            prefixCode.add(new ErrorHighlighter());
            // validation: does space already exist with same prefixCode ?
            prefixCode.add(new AbstractValidator() {
                public void validate(FormComponent c) {
                    String s = (String) c.getConvertedInput();
                    Space temp = getJtrac().loadSpace(s);
                    if(temp != null && temp.getId() != space.getId()) {
                        error(c);
                    }
                }
                @Override
                protected String resourceKey(FormComponent c) {                    
                    return "space_form.error.prefixCode.exists";
                }                
            });            
            add(prefixCode);
            // description =====================================================
            add(new TextArea("space.description"));
            // guest allowed ===================================================
            add(new CheckBox("space.guestAllowed"));
            // hide copy from option if edit ===================================
            WebMarkupContainer hide = new WebMarkupContainer("hide");            
            if(space.getId() > 0) {
                hide.setVisible(false);
            } else {
                List<Space> spaces = getJtrac().findAllSpaces();
                DropDownChoice choice = new DropDownChoice("copyFrom", spaces, new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        return ((Space) o).getName();
                    }
                    public String getIdValue(Object o, int i) {
                        return ((Space) o).getId() + "";
                    }                
                });
                choice.setNullValid(true);                
                hide.add(choice);
            }
            add(hide);
            // cancel link =====================================================
            add(new Link("cancel") {
                public void onClick() {
                    if(previous == null) {
                        setResponsePage(new OptionsPage());
                    } else {
                        if (previous instanceof SpaceListPage) {
                            ((SpaceListPage) previous).setSelectedSpaceId(space.getId());
                        }                      
                        setResponsePage(previous);
                    }                    
                }                
            });
        }
     
        @Override
        protected void validate() {
            filter.reset();
            super.validate();          
        }        
        
        @Override
        protected void onSubmit() {
            SpaceFormModel model = (SpaceFormModel) getModelObject();
            setResponsePage(new SpaceFieldListPage(model.getSpace(), null, previous));
        }        
    }        
        
    /**
     * custom form backing object that wraps Space and adds some fields
     * required for the create / edit use case
     */
    private class SpaceFormModel implements Serializable {
        
        private transient Space space;
        private Space copyFrom;

        public Space getSpace() {
            if(space == null) {
                space = new Space();
            }
            return space;
        }

        public void setSpace(Space space) {
            this.space = space;
        }

        public Space getCopyFrom() {
            return copyFrom;
        }

        public void setCopyFrom(Space copyFrom) {
            this.copyFrom = copyFrom;
        }                           
               
    }
    
}
