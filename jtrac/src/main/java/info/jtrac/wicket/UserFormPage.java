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
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.WebPage;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.FormComponent;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.validation.AbstractValidator;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.BoundCompoundPropertyModel;

/**
 * user edit form
 */
public class UserFormPage extends BasePage {
      
    private WebPage previous;        
    
    private JtracFeedbackMessageFilter filter;

    public void setPrevious(WebPage previous) {
        this.previous = previous;
    }   
    
    private void addComponents(User user) {
        FeedbackPanel feedback = new FeedbackPanel("feedback");
        filter = new JtracFeedbackMessageFilter();
        feedback.setFilter(filter);
        add(feedback);  
        add(new UserForm("form", user));
    }
    
    public UserFormPage() {  
        User user = new User();
        user.setLocale(getJtrac().getDefaultLocale());
        addComponents(user);
    }    
    
    public UserFormPage(User user) {
        addComponents(user);
    }
    
    private class UserForm extends Form {
        
        private transient User user;
        private String password;
        private String passwordConfirm;
        private boolean sendNotifications;
        
        public User getUser() {
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
        
        public UserForm(String id, final User user) {
            
            super(id);
            this.user = user;
            final BoundCompoundPropertyModel model = new BoundCompoundPropertyModel(this);
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
            // validation: does user already exist with same loginName?
            loginName.add(new AbstractValidator() {
                public void validate(FormComponent c) {
                    String s = (String) c.getConvertedInput();
                    User temp = getJtrac().loadUser(s);
                    if(temp != null && temp.getId() != user.getId()) {
                        error(c);
                    }
                }
                @Override
                protected String resourceKey(FormComponent c) {                    
                    return "user_form.loginId.error.exists";
                }                
            });
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
            // hide e-mail message if edit =====================================
            WebMarkupContainer hide = new WebMarkupContainer("hide");
            if(user.getId() > 0) {
                hide.setVisible(false);
            }
            add(hide);
            // password ========================================================
            final TextField passwordField = new TextField("password");            
            add(passwordField);
            // confirm password ================================================
            TextField confirmPasswordField = new TextField("passwordConfirm");
            confirmPasswordField.add(new ErrorHighlighter());
            // validation: do the passwords match?
            confirmPasswordField.add(new AbstractValidator() {
                public void validate(FormComponent c) {
                    String b = (String) c.getConvertedInput();
                    String a = (String) passwordField.getConvertedInput();
                    if((a != null && !a.equals(b)) || (b!= null && !b.equals(a))) {
                        error(c);
                    }
                }
                @Override
                protected String resourceKey(FormComponent c) {                    
                    return "user_form.passwordConfirm.error";
                }                
            });
            add(confirmPasswordField);            
            // send notifications ==============================================
            add(new CheckBox("sendNotifications"));
            // cancel link
            add(new Link("cancel") {
                public void onClick() {
                    if(previous == null) {
                        setResponsePage(new OptionsPage());
                    } else {
                        if (previous instanceof UserListPage) {
                            ((UserListPage) previous).setSelectedUserId(user.getId());
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
            if(password != null) {
                getJtrac().storeUser(user, password, sendNotifications);
            } else {
                getJtrac().storeUser(user);
            }
            refreshPrincipal(user);
            if(previous == null) {
                UserListPage page = new UserListPage();
                page.setSelectedUserId(user.getId());
                setResponsePage(page);
            } else {                
                if (previous instanceof UserListPage) {
                    ((UserListPage) previous).setSelectedUserId(user.getId());
                }
                if (previous instanceof SpaceAllocatePage) {
                    SpaceAllocatePage sap = (SpaceAllocatePage) previous;
                    // TODO refactor this better, but for now this is so that the newly created user 
                    // appears in the drop down for allocation and is preselected
                    previous = new SpaceAllocatePage(sap.getSpaceId(), sap.getPrevious(), user.getId());
                }
                setResponsePage(previous);
            }
        }        
    }        
    
}
