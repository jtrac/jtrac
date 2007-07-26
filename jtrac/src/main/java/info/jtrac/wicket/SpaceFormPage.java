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
import info.jtrac.util.ValidationUtils;
import java.util.List;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.BoundCompoundPropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * space edit form
 */
public class SpaceFormPage extends BasePage {
      
    private WebPage previous;            

    public void setPrevious(WebPage previous) {
        this.previous = previous;
    }    
    
    public SpaceFormPage() {  
        Space space = new Space();    
        space.getMetadata().initRoles();
        add(new SpaceForm("form", space));
    }    
    
    public SpaceFormPage(Space space) {     
        add(new SpaceForm("form", space));
    }
    
    /**
     * wicket form
     */     
    private class SpaceForm extends Form {
              
        private Space space;
        private Space copyFrom;  
        
        private JtracFeedbackMessageFilter filter;          
        
        public Space getSpace() {
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
        
        public SpaceForm(String id, final Space space) {
            
            super(id);
            
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new JtracFeedbackMessageFilter();
            feedback.setFilter(filter);            
            add(feedback);
                        
            this.space = space;
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(this);
            setModel(model);
            
            // delete button only if edit ======================================
            Button delete = new Button("delete") {
                @Override
                public void onSubmit() {
                    String heading = localize("space_delete.confirm");
                    String warning = localize("space_delete.line3");
                    String line1 = localize("space_delete.line1");
                    String line2 = localize("space_delete.line2");
                    ConfirmPage confirm = new ConfirmPage(SpaceFormPage.this, heading, warning, new String[] {line1, line2}) {
                        public void onConfirm() {
                            getJtrac().removeSpace(space);
                            // logged in user may have been allocated to this space
                            SpaceFormPage.this.refreshPrincipal();
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
            add(name);
            add(new HeaderContributor(new IHeaderContributor() {
                public void renderHead(IHeaderResponse response) {
                    response.renderOnLoadJavascript("document.getElementById('" + name.getMarkupId() + "').focus()");
                }
            }));            
            // prefix Code =====================================================
            TextField prefixCode = new TextField("space.prefixCode");
            prefixCode.setRequired(true);
            prefixCode.add(new ErrorHighlighter());
            // validation: greater than 3 chars?
            prefixCode.add(new AbstractValidator() {
                protected void onValidate(IValidatable v) {
                    String s = (String) v.getValue();
                    if(s.length() < 3) {
                        error(v);
                    }
                }
                @Override
                protected String resourceKey() {                    
                    return "space_form.error.prefixCode.tooShort";
                }                
            });
            prefixCode.add(new AbstractValidator() {
                protected void onValidate(IValidatable v) {
                    String s = (String) v.getValue();
                    if(s.length() > 10) {
                        error(v);
                    }
                }
                @Override
                protected String resourceKey() {                    
                    return "space_form.error.prefixCode.tooLong";
                }                
            });             
            // validation: format ok?
            prefixCode.add(new AbstractValidator() {
                protected void onValidate(IValidatable v) {
                    String s = (String) v.getValue();
                    if(!ValidationUtils.isAllUpperCase(s)) {
                        error(v);
                    }
                }
                @Override
                protected String resourceKey() {                    
                    return "space_form.error.prefixCode.invalid";
                }                
            });            
            // validation: does space already exist with same prefixCode ?
            prefixCode.add(new AbstractValidator() {
                protected void onValidate(IValidatable v) {
                    String s = (String) v.getValue();
                    Space temp = getJtrac().loadSpace(s);
                    if(temp != null && temp.getId() != space.getId()) {
                        error(v);
                    }
                }
                @Override
                protected String resourceKey() {                    
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
            if(copyFrom != null) {
                Space temp = getJtrac().loadSpace(copyFrom.getId());
                space.getMetadata().setXmlString(temp.getMetadata().getXmlString());
            }
            setResponsePage(new SpaceFieldListPage(space, null, previous));
        }        
    }                
    
}
