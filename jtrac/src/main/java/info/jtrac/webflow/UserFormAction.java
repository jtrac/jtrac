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
import info.jtrac.domain.SpaceRole;
import info.jtrac.util.SecurityUtils;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.DataBinder;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.webflow.Event;
import org.springframework.webflow.RequestContext;
import org.springframework.webflow.ScopeType;

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
    public Object loadFormObject(RequestContext context) {
        UserForm userForm = new UserForm();
        String userId = ValidationUtils.getParameter(context, "userId");
        // if called as subflow, userId may be vestigial from space allocate form
        // hence extra check for space in Flow scope
        if (userId != null && context.getFlowScope().get("space") == null) {
            User user = jtrac.loadUser(Integer.parseInt(userId));
            user.setPassword(null);
            userForm.setUser(user);
            return userForm;
        }
        return userForm;
    }     
    
    /**
     * Form backing object
     */
    public static class UserForm implements Serializable {
        
        private transient User user = new User();
        private String passwordConfirm;
        private boolean admin;
        
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
                errors.rejectValue("user.loginName", "error.userForm.user.loginName.badchars", 
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
        User user = userForm.getUser();
        User temp = jtrac.loadUser(user.getLoginName());
        if (temp != null && temp.getId() != user.getId()) {
            Errors errors = getFormErrors(context);
            errors.rejectValue("user.loginName", "error.user.loginName.exists", "Login ID already exists");
            return error();
        }
        // note that jtrac service layer takes care of the object relationships before persisting UI edit
        jtrac.storeUser(user);
        SecurityUtils.refreshSecurityContextIfPrincipal(user);
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
        jtrac.storeUserSpaceAllocation(user, space, roleKey);
        String admin = ValidationUtils.getParameter(context, "admin");
        if (admin != null) {
            jtrac.storeUserSpaceAllocation(user, space, "ROLE_ADMIN");
        }        
        SecurityUtils.refreshSecurityContextIfPrincipal(user);
        return success();
    }    

    public Event userDeallocateHandler(RequestContext context) {
        String spaceRoleId = ValidationUtils.getParameter(context, "deallocate");
        int id = Integer.parseInt(spaceRoleId);
        SpaceRole spaceRole = jtrac.loadSpaceRole(id);
        User user = (User) context.getFlowScope().get("user");        
        jtrac.removeUserSpaceAllocation(user, spaceRole);
        SecurityUtils.refreshSecurityContextIfPrincipal(user);
        return success();
    } 
    
    public Event userMakeAdminHandler(RequestContext context) {        
        User user = (User) context.getFlowScope().get("user");        
        jtrac.storeUserSpaceAllocation(user, null, "ROLE_ADMIN");
        SecurityUtils.refreshSecurityContextIfPrincipal(user);
        return success();
    }     
    
}
