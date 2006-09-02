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
import info.jtrac.util.SecurityUtils;
import info.jtrac.util.ValidationUtils;
import info.jtrac.webflow.FieldFormAction.FieldForm;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.DataBinder;

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
    protected void initBinder(RequestContext request, DataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }     
    
    @Override
    public Object loadFormObject(RequestContext context) {
        context.getFlowScope().put("_flowId", "space");
        String spaceId = ValidationUtils.getParameter(context, "spaceId");
        Space space = null;
        if (spaceId != null) {
            space = jtrac.loadSpace(Integer.parseInt(spaceId));
            // cloning
            Space clone = new Space();
            clone.setId(space.getId());
            clone.setPrefixCode(space.getPrefixCode() + "");
            clone.setDescription(space.getDescription() == null ? null : space.getDescription() + "");
            clone.setSpaceSequence(space.getSpaceSequence()); // or else Hibernate orphans the old one
            Metadata m = new Metadata();
            m.setId(space.getMetadata().getId()); // or else Hibernate orphans the old one           
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
            if (prefixCode.length() > 10) {
                errors.rejectValue("prefixCode", "error.space.prefixCode.toolong",
                        "Length should be less than 10 characters.");
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
    
    public Event fieldUpHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String fieldName = ValidationUtils.getParameter(context, "fieldName");
        List<Field.Name> fieldOrder = space.getMetadata().getFieldOrder();
        int index = fieldOrder.indexOf(Field.convertToName(fieldName));
        int swapIndex = index - 1;
        if (swapIndex < 0) {
            if (fieldOrder.size() > 1) {        
                swapIndex = fieldOrder.size() - 1;
            } else {
                swapIndex = 0;
            }
        }
        if (index != swapIndex) {
            Collections.swap(fieldOrder, index, swapIndex);
        }                
        context.getRequestScope().put("selectedFieldName", fieldName);
        return success();
    }
    
    public Event fieldDownHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String fieldName = ValidationUtils.getParameter(context, "fieldName");
        List<Field.Name> fieldOrder = space.getMetadata().getFieldOrder();
        int index = fieldOrder.indexOf(Field.convertToName(fieldName));
        int swapIndex = index + 1;
        if (swapIndex == fieldOrder.size() ) {
            swapIndex = 0;
        }
        if (index != swapIndex) {
            Collections.swap(fieldOrder, index, swapIndex);
        }
        context.getRequestScope().put("selectedFieldName", fieldName);
        return success();
    }    
    
    public Event fieldAddHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String fieldType = ValidationUtils.getParameter(context, "fieldType");
        if (fieldType == null) {
            // no fields left, just return to the space details screen
            return error();
        }
        int type = Integer.parseInt(fieldType);
        FieldForm fieldForm = new FieldForm();
        Field field = space.getMetadata().getNextAvailableField(type);
        // set intelligent defaults to make adding new field to space easier
        field.initOptions();
        fieldForm.setField(field);
        space.getMetadata().add(field);
        context.getFlowScope().put("fieldForm", fieldForm);                    
        return success();
    }   
    
    public Event fieldEditHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String fieldName = ValidationUtils.getParameter(context, "fieldName");    
        Field field = space.getMetadata().getField(fieldName);
        FieldForm fieldForm = new FieldForm();
        fieldForm.setField(field);
        context.getFlowScope().put("fieldForm", fieldForm);
        return success();
    }
    
    public Event fieldUpdateHandler(RequestContext context) {        
        FieldForm fieldForm = (FieldForm) context.getFlowScope().get("fieldForm");
        Field field = fieldForm.getField();        
        context.getRequestScope().put("selectedFieldName", field.getName());
        return success();
    }
    
    public Event fieldDeleteSetupHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String fieldName = ValidationUtils.getParameter(context, "fieldName");    
        Field field = space.getMetadata().getField(fieldName);
        context.getRequestScope().put("field", field);
        int affectedCount = 0;
        if (space.getId() > 0) {
             affectedCount = jtrac.findItemCount(space, field);
        }
        context.getRequestScope().put("affectedCount", affectedCount);
        return success();
    }  
    
    public Event fieldDeleteHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String fieldName = ValidationUtils.getParameter(context, "fieldName");            
        Field field = space.getMetadata().getField(fieldName);
        space.getMetadata().removeField(fieldName);
        if (space.getId() > 0) {
            jtrac.removeField(space, field);
            // database has been updated, if we don't do this
            // user may leave without committing metadata change
            logger.debug("saving space after field delete operation");
            jtrac.storeMetadata(space.getMetadata());
        }     
        return success();
    }      
    
    public Event stateAddHandler(RequestContext context) throws Exception {
        Space space = (Space) context.getFlowScope().get("space");
        String state = ValidationUtils.getParameter(context, "state");
        if (!ValidationUtils.isCamelDashCase(state)) {
            Errors errors = getFormErrors(context);
            errors.reject("error.spaceRoles.state.badchars", 
                    "State name has to start with a capital letter followed by lower-case letters.");
            context.getRequestScope().put("state", state);
            return error();
        }
        space.getMetadata().addState(state);        
        return success();
    }
    
    public Event roleAddHandler(RequestContext context) throws Exception {
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
    
    public Event stateEditSetupHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String stateKey = ValidationUtils.getParameter(context, "stateKey");
        context.getRequestScope().put("state", space.getMetadata().getStates().get(Integer.parseInt(stateKey)));
        context.getRequestScope().put("stateKey", stateKey);
        return success();
    }    
    
    public Event stateEditHandler(RequestContext context) throws Exception {
        String state = ValidationUtils.getParameter(context, "state");
        String stateKey = ValidationUtils.getParameter(context, "stateKey");
        if (!ValidationUtils.isCamelDashCase(state)) {
            Errors errors = getFormErrors(context);
            errors.reject("error.spaceRoles.state.badchars", 
                    "State name has to be Camel-Case with dashes ('-') to separate words e.g. 'Fixed', 'On-Hold' or 'Work-In-Progress'");
            context.getRequestScope().put("state", state);
            context.getRequestScope().put("stateKey", stateKey);
            return error();
        }                
        Space space = (Space) context.getFlowScope().get("space");
        space.getMetadata().getStates().put(Integer.parseInt(stateKey), state);
        return success();
    }  
    
    public Event editTransitionHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String stateKey = ValidationUtils.getParameter(context, "stateKey");
        String roleKey = ValidationUtils.getParameter(context, "roleKey");
        String transitionKey = ValidationUtils.getParameter(context, "transitionKey");
        space.getMetadata().toggleTransition(Integer.parseInt(stateKey), roleKey, Integer.parseInt(transitionKey));
        return success();
    }
    
    public Event editMaskHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String stateKey = ValidationUtils.getParameter(context, "stateKey");
        String roleKey = ValidationUtils.getParameter(context, "roleKey");
        String fieldKey = ValidationUtils.getParameter(context, "fieldKey");
        space.getMetadata().switchMask(Integer.parseInt(stateKey), roleKey, fieldKey);        
        return success();
    }    
    
    public Event spaceSaveHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        jtrac.storeSpace(space);
        return success();
    }
    
    public Event spaceAllocateSetup(RequestContext context) {
        context.getFlowScope().put("_flowId", "space");
        Space space = (Space) context.getFlowScope().get("space");
        if (space == null) {
            String spaceId = ValidationUtils.getParameter(context, "spaceId");
            int id = Integer.parseInt(spaceId);
            space = jtrac.loadSpace(id);           
            context.getFlowScope().put("space", space);
        }
        List<UserRole> userRoles = jtrac.findUserRolesForSpace(space.getId());
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
        String admin = ValidationUtils.getParameter(context, "admin");
        jtrac.storeUserSpaceAllocation(user, space, roleKey);
        if (admin != null) {
            jtrac.storeUserSpaceAllocation(user, space, "ROLE_ADMIN");
        }
        SecurityUtils.refreshSecurityContextIfPrincipal(user);
        return success();
    }    

    public Event spaceDeallocateHandler(RequestContext context) {
        String userId = ValidationUtils.getParameter(context, "deallocateUserId");
        String roleKey = ValidationUtils.getParameter(context, "deallocateRoleKey");
        int id = Integer.parseInt(userId);
        User user = jtrac.loadUser(id);
        Space space = (Space) context.getFlowScope().get("space");
        jtrac.removeUserSpaceAllocation(user, space, roleKey);
        SecurityUtils.refreshSecurityContextIfPrincipal(user);
        return success();
    }     
    
}
