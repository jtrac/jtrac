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

import info.jtrac.domain.Field;
import info.jtrac.domain.Metadata;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.domain.UserRole;
import info.jtrac.util.ValidationUtils;
import info.jtrac.webflow.FieldFormAction.FieldForm;
import java.util.List;
import org.acegisecurity.context.SecurityContextHolder;

import org.springframework.validation.Errors;
import org.springframework.webflow.Event;
import org.springframework.webflow.RequestContext;
import org.springframework.webflow.ScopeType;

/**
 * Multiaction that backs the "Space Create" flow
 */
public class SpaceFormAction extends AbstractFormAction {
    
    public SpaceFormAction() {
        setFormObjectClass(Space.class);
        setFormObjectName("space");
        setFormObjectScope(ScopeType.FLOW);
    }
    
    @Override
    public Object loadFormObject(RequestContext context) {
        String spaceId = ValidationUtils.getParameter(context, "spaceId");
        Space space = null;
        if (spaceId != null) {
            space = jtrac.loadSpace(Integer.parseInt(spaceId));
            // cloning
            Space clone = new Space();
            clone.setId(space.getId());
            clone.setPrefixCode(space.getPrefixCode() + "");
            clone.setDescription(space.getDescription() == null ? null : space.getDescription() + "");
            Metadata m = new Metadata();
            m.setXml(space.getMetadata().getXml());
            clone.setMetadata(m);
            return clone;
        } else {
            space = new Space();
            space.getMetadata().initRoles();
            return space;
        }
    }    
    
    public Event spaceFormHandler(RequestContext context) throws Exception {
        // have to manually bind.  First get space from scope
        Space space = (Space) context.getFlowScope().get("space");
        Errors errors = getFormErrors(context);
        // manual binding
        String prefixCode = ValidationUtils.getParameter(context, "prefixCode");
        space.setPrefixCode(prefixCode);
        String description = ValidationUtils.getParameter(context, "description");
        space.setDescription(description);
        if (prefixCode == null) {
            errors.rejectValue("prefixCode", ValidationUtils.ERROR_EMPTY_CODE, ValidationUtils.ERROR_EMPTY_MSG);
        } else {
            if (prefixCode.length() < 3) {
                errors.rejectValue("prefixCode", "error.space.prefixCode.tooshort",
                        "Length should be at least 3 characters.");
            }
            if (!ValidationUtils.isAllUpperCase(prefixCode)) {
                errors.rejectValue("prefixCode", "error.space.prefixCode.badchars",
                        "Only capital letters and numeric characters allowed.");
            }
        }
        if (errors.hasErrors()) {
            return error();
        }
        Space temp = jtrac.loadSpace(space.getPrefixCode());
        if (temp != null && temp.getId() != space.getId()) {            
            errors.rejectValue("prefixCode", "error.space.prefixCode.exists", "Space already exists");
            return error();
        } 
        return success();
    }
    
    public Event spaceFieldAddHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String fieldType = (String) context.getRequestParameters().get("fieldType");
        if (fieldType == null) {
            // no fields left, just return to the space details screen
            return error();
        }
        int type = Integer.parseInt(fieldType);
        FieldForm fieldForm = new FieldForm();
        fieldForm.setField(space.getMetadata().getNextAvailableField(type));
        context.getFlowScope().put("fieldForm", fieldForm);                    
        return success();
    }   
    
    public Event spaceFieldEditHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String fieldName = (String) context.getRequestParameters().get("fieldName");    
        Field field = space.getMetadata().getField(fieldName).getClone();
        FieldForm fieldForm = new FieldForm();
        fieldForm.setField(field);            
        context.getFlowScope().put("fieldForm", fieldForm);
        return success();
    }
    
    public Event spaceFieldUpdateHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        FieldForm fieldForm = (FieldForm) context.getFlowScope().get("fieldForm");
        Field field = fieldForm.getField();
        space.getMetadata().add(field); // will overwrite with clone if applicable
        context.getRequestScope().put("selectedFieldName", field.getName());
        return success();
    }
    
    public Event spaceStateAddHandler(RequestContext context) throws Exception {
        Space space = (Space) context.getFlowScope().get("space");
        String state = ValidationUtils.getParameter(context, "state");
        if (!ValidationUtils.isTitleCase(state)) {
            Errors errors = getFormErrors(context);
            errors.reject("error.spaceRoles.state.badchars", 
                    "State name has to start with a capital letter followed by lower-case letters.");
            context.getRequestScope().put("state", state);
            return error();
        }
        space.getMetadata().addState(state);        
        return success();
    }
    
    public Event spaceRoleAddHandler(RequestContext context) throws Exception {
        Space space = (Space) context.getFlowScope().get("space");
        String role = ValidationUtils.getParameter(context, "role");             
        if (!ValidationUtils.isAllUpperCase(role)) {
            Errors errors = getFormErrors(context);
            errors.reject("error.spaceRoles.role.badchars", 
                    "Role name has to be all capital letters or digits.");
            context.getRequestScope().put("role", role);
            return error();
        }        
        space.getMetadata().addRole(role);
        return success();
    }
    
    public Event spaceSaveHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        jtrac.storeSpace(space);
        return success();
    }
    
    public Event spaceAllocateSetup(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        if (space == null) {
            String spaceId = ValidationUtils.getParameter(context, "spaceId");
            int id = Integer.parseInt(spaceId);
            space = jtrac.loadSpace(id);           
            context.getFlowScope().put("space", space);
        }
        List<UserRole> userRoles = jtrac.findUsersForSpace(space.getId());
        context.getRequestScope().put("userRoles", userRoles);
        context.getRequestScope().put("unallocatedUsers", jtrac.findUnallocatedUsersForSpace(space.getId()));
        return success();
    }
    
    public Event spaceAllocateHandler(RequestContext context) {
        String userId = ValidationUtils.getParameter(context, "userId");
        if (userId == null) {
            // no users left, no navigation
            return error();            
        }
        int id = Integer.parseInt(userId);
        User user = jtrac.loadUser(id);
        Space space = (Space) context.getFlowScope().get("space");
        String roleKey = ValidationUtils.getParameter(context, "roleKey");
        user.addSpaceRole(space, roleKey);
        jtrac.storeUser(user);
        // effectively forces the Acegi Security Context to reload
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        return success();
    }    
    
}
