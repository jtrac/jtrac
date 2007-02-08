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

import info.jtrac.domain.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import wicket.Component;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.TextField;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.BoundCompoundPropertyModel;

/**
 * user edit form
 */
public class UserFormPage extends BasePage {
      
    private JtracFeedbackMessageFilter filter;
    
    private void addComponents(User user) {
        FeedbackPanel feedback = new FeedbackPanel("feedback");
        filter = new JtracFeedbackMessageFilter();
        feedback.setFilter(filter);
        border.add(feedback);  
        add(new HeaderPanel(null)); 
        border.add(new UserForm("form", user));
    }
    
    public UserFormPage() {
        super("Options Menu");   
        User user = new User();
        user.setLocale(getJtrac().getDefaultLocale());
        addComponents(user);
    }    
    
    private class UserForm extends Form {
        
        public UserForm(String id, User user) {
            
            super(id);
            UserFormModel modelObject = new UserFormModel();
            modelObject.setUser(user);
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(modelObject);
            setModel(model);
            
            // login name ======================================================
            final TextField loginName = new TextField("user.loginName");
            loginName.setRequired(true);
            loginName.add(new ErrorHighlighter());
            loginName.setOutputMarkupId(true);
            UserFormPage.this.getBodyContainer().addOnLoadModifier(new AbstractReadOnlyModel() {
                public Object getObject(Component c) {
                    return "document.getElementById('" + loginName.getMarkupId() + "').focus()";
                }
            }, loginName);
            add(loginName);
            // name ============================================================
            add(new TextField("user.name").setRequired(true).add(new ErrorHighlighter()));
            // email ===========================================================
            add(new TextField("user.email").setRequired(true).add(new ErrorHighlighter()));
            // locale ==========================================================
            final Map<String, String> locales = getJtrac().getLocales();
            List<String> localeKeys = new ArrayList<String>(locales.keySet());
            DropDownChoice localeChoice = new DropDownChoice("user.locale", localeKeys, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return locales.get(o);
                }
                public String getIdValue(Object o, int i) {
                    return o.toString();
                }                
            });
            add(localeChoice);
            // password ========================================================
            add(new TextField("password").add(new ErrorHighlighter()));
            // confirm password ================================================
            add(new TextField("passwordConfirm").add(new ErrorHighlighter()));            
            // send notifications ==============================================
            add(new CheckBox("sendNotifications"));
        }
                        
    }
        
    /**
     * form backing object
     */
    private class UserFormModel implements Serializable {
        
        private transient User user;
        private String password;
        private String passwordConfirm;
        private boolean sendNotifications;
        
        public User getUser() {
            if (user == null) {
                user = new User();
            }
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }        
        
        public String getPasswordConfirm() {
            return passwordConfirm;
        }
        
        public void setPasswordConfirm(String passwordConfirm) {
            this.passwordConfirm = passwordConfirm;
        }

        public boolean isSendNotifications() {
            return sendNotifications;
        }
        
        public void setSendNotifications(boolean sendNotifications) {
            this.sendNotifications = sendNotifications;
        }  
               
    }
    
}
