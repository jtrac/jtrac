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

package info.jtrac.wicket.yui;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * test panel
 */
public class TestPanel extends Panel {                    
    
    private Component nameField;
    
    public String getFocusScript() {
        return "document.getElementById('" + nameField.getMarkupId() + "').focus();";
    }    
    
    public TestPanel(String id) {
        super(id);
        add(new TestForm("form"));
    }      
    
    private class TestForm extends Form {
        
        private String name;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }                       
        
        public TestForm(String id) {
            super(id);
            setOutputMarkupId(true);
            final FeedbackPanel feedback = new FeedbackPanel("feedback");
            feedback.setOutputMarkupId(true);
            add(feedback);
            setModel(new CompoundPropertyModel(this));
            add(nameField = new TextField("name").setRequired(true).setOutputMarkupId(true));
            add(new TextField("description"));
            add(new AjaxSubmitButton("submit", this) {
                @Override
                protected void onError(AjaxRequestTarget target, Form form) {
                    target.addComponent(feedback);
                    target.appendJavascript(getFocusScript());
                }
                protected void onSubmit(AjaxRequestTarget target, Form form) {
                    setResponsePage(new TestPage2(name, description));
                }
            });            
        }        
        
    }
    
}
