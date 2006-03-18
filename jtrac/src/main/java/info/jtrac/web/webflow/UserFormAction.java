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

package info.jtrac.web.webflow;

import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.util.ValidationUtils;

import java.io.Serializable;

import static info.jtrac.Constants.*;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.webflow.Event;
import org.springframework.webflow.RequestContext;
import org.springframework.webflow.ScopeType;
import org.springframework.webflow.action.FormObjectAccessor;

/**
 * Multiaction that backs the "User Create" flow
 */
public class UserFormAction extends AbstractFormAction {
    
    public UserFormAction() {
        setFormObjectClass(UserForm.class);
        setFormObjectName("userForm");
        setFormObjectScope(ScopeType.FLOW);
        setValidator(new UserFormValidator());
    }
    
    /**
     * Form backing object
     */
    public static class UserForm implements Serializable {
        
        private transient User user = new User();
        private String passwordConfirm;
        
        public User getUser() {
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
        }
        
        public String getPasswordConfirm() {
            return passwordConfirm;
        }
        
        public void setPasswordConfirm(String passwordConfirm) {
            this.passwordConfirm = passwordConfirm;
        }
        
    }
    
    /**
     * A validator for our form backing object.
     */
    public static class UserFormValidator implements Validator {
        
        public boolean supports(Class clazz) {
            return UserForm.class.isAssignableFrom(clazz);
        }
        
        public void validate(Object o, Errors errors) {
            UserForm userForm = (UserForm) o;
            ValidationUtils.rejectIfEmpty(errors, "user.loginName", "user.name", "user.email");
            if (!ValidationUtils.isAllLowerCase(userForm.getUser().getLoginName())) {
                errors.rejectValue("loginName", "error.userForm.loginName.badchars", 
                        "Only lower case letters and numeric characters allowed.");
            }
            String password = userForm.getUser().getPassword();
            String passwordConfirm = userForm.getPasswordConfirm();
            if ((password != null && !password.equals(passwordConfirm)) ||
                    (passwordConfirm != null && !passwordConfirm.equals(password))) {
                errors.rejectValue("passwordConfirm", "error.userForm.passwordConfirm.notsame", "Does not match password");
            }
        }        
    }
    
    public Event userFormHandler(RequestContext context) throws Exception {
        UserForm userForm = (UserForm) getFormObject(context);
        User temp = jtrac.loadUser(userForm.getUser().getLoginName());
        if (temp != null) {
            Errors errors = getFormErrors(context);
            errors.rejectValue("user.loginName", "error.user.loginName.exists", "Login ID already exists");
            return error();
        }        
        jtrac.storeUser(userForm.getUser());
        return success();
    }
    
    public Event userListViewSetup(RequestContext context) {
        context.getRequestScope().put("users", jtrac.loadAllUsers());
        return success();
    }
    
}
