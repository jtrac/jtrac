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

package info.jtrac.webflow;

import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.util.ValidationUtils;

import java.io.Serializable;

import static info.jtrac.Constants.*;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.util.SecurityUtils;
import info.jtrac.util.UserUtils;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

/**
 * Multiaction that backs the "User Create / Edit" flow
 */
public class UserFormAction extends AbstractFormAction {
    
    public UserFormAction() {
        setFormObjectClass(UserForm.class);
        setFormObjectName("userForm");
        setFormObjectScope(ScopeType.REQUEST);
        setValidator(new UserFormValidator());
    }    
    
    @Override
    protected void initBinder(RequestContext request, DataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }     
    
    @Override
    public Object createFormObject(RequestContext context) {
        context.getRequestScope().put("locales", jtrac.getLocales());
        UserForm userForm = new UserForm();
        // note that form has a hidden field 'userId' to handle re-show form on errors
        String userId = ValidationUtils.getParameter(context, "userId");
        // if called as subflow, userId may be unintended request parameter from space allocate form
        // hence extra check for space in Flow scope
        if (userId != null && context.getFlowScope().get("space") == null) {
            User user = jtrac.loadUser(Integer.parseInt(userId));
            userForm.setUser(user);
            return userForm;
        }
        return userForm;
    }     
    
    /**
     * Form backing object
     */
    public static class UserForm implements Serializable {
        
        private transient User user;
        private String password;
        private String passwordConfirm;
        private boolean admin;
        
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
                errors.rejectValue("user.loginName", "user_form.loginId.error.invalid");
            }
            String password = userForm.getPassword();
            String passwordConfirm = userForm.getPasswordConfirm();
            if ((password != null && !password.equals(passwordConfirm)) ||
                    (passwordConfirm != null && !passwordConfirm.equals(password))) {
                errors.rejectValue("passwordConfirm", "user_form.passwordConfirm.error");
            }
        }        
    }
    
    public Event userFormHandler(RequestContext context) throws Exception {
        UserForm userForm = (UserForm) getFormObject(context);
        User user = userForm.getUser();
        User temp = jtrac.loadUser(user.getLoginName());
        if (temp != null && temp.getId() != user.getId()) {
            Errors errors = getFormErrors(context);
            errors.rejectValue("user.loginName", "user_form.loginId.error.exists");
            return error();
        }
        if (userForm.getPassword() != null || user.getId() == 0) {
            jtrac.storeUser(user, userForm.getPassword(), true);
        } else {
            jtrac.storeUser(user);
        }
        temp = SecurityUtils.getPrincipal();
        if (temp.getId() == user.getId()) {
            SecurityUtils.refreshSecurityContext();
            UserUtils.refreshLocale(context, user.getLocale());
        }
        return success();
    }
    
    public Event userAllocateSpaceSetup(RequestContext context) throws Exception {
        User user = (User) context.getFlowScope().get("user");
        if (user == null) {
            String userId = ValidationUtils.getParameter(context, "userId");
            int id = Integer.parseInt(userId);
            user = jtrac.loadUser(id);
        }
        context.getFlowScope().put("user", user);        
        context.getRequestScope().put("unallocatedSpaces", jtrac.findUnallocatedSpacesForUser(user.getId()));
        return success();
    }    
    
    public Event userAllocateSpaceRoleSetup(RequestContext context) {
        String spaceId = ValidationUtils.getParameter(context, "spaceId");
        if (spaceId == null) {
            // no spaces left, no navigation
            return error();
        }
        int id = Integer.parseInt(spaceId);        
        context.getFlowScope().put("space", jtrac.loadSpace(id));
        return success();
    }     
    
    public Event userAllocateHandler(RequestContext context) {
        User user = (User) context.getFlowScope().get("user");
        Space space = (Space) context.getFlowScope().get("space");
        String roleKey = ValidationUtils.getParameter(context, "roleKey");
        jtrac.storeUserSpaceRole(user, space, roleKey);
        String admin = ValidationUtils.getParameter(context, "admin");
        if (admin != null) {
            jtrac.storeUserSpaceRole(user, space, "ROLE_ADMIN");
        }        
        SecurityUtils.refreshSecurityContextIfPrincipal(user);
        return success();
    }    

    public Event userDeallocateHandler(RequestContext context) {
        String userSpaceRoleId = ValidationUtils.getParameter(context, "deallocate");
        int id = Integer.parseInt(userSpaceRoleId);
        UserSpaceRole userSpaceRole = jtrac.loadUserSpaceRole(id);        
        jtrac.removeUserSpaceRole(userSpaceRole);
        User user = jtrac.loadUser(userSpaceRole.getUser().getId());
        SecurityUtils.refreshSecurityContextIfPrincipal(user);
        context.getFlowScope().put("user", user);
        return success();
    } 
    
    public Event userMakeAdminHandler(RequestContext context) {        
        User user = (User) context.getFlowScope().get("user");        
        jtrac.storeUserSpaceRole(user, null, "ROLE_ADMIN");
        SecurityUtils.refreshSecurityContextIfPrincipal(user);
        return success();
    }     
    
}
