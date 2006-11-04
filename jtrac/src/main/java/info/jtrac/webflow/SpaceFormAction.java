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
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.util.SecurityUtils;
import info.jtrac.util.ValidationUtils;
import info.jtrac.webflow.FieldFormAction.FieldForm;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.DataBinder;

import org.springframework.validation.Errors;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

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
    public Object createFormObject(RequestContext context) {
        context.getFlowScope().put("_flowId", "space");
        String spaceId = ValidationUtils.getParameter(context, "spaceId");
        Space space = null;
        if (spaceId != null) {
            space =  jtrac.loadSpace(Integer.parseInt(spaceId));
            space.getMetadata().getXmlString();  // hack: ensure nothing left to be lazy loaded!
            return space;
        } else {
            space = new Space();
            space.getMetadata().initRoles();
            return space;
        }
    }    
    
    public Event spaceFormHandler(RequestContext context) throws Exception {        
        Space space = (Space) getFormObject(context);        
        Errors errors = getFormErrors(context);               
        if (space.getPrefixCode() == null) {
            errors.rejectValue("prefixCode", ValidationUtils.ERROR_EMPTY_CODE, ValidationUtils.ERROR_EMPTY_MSG);
        } else {
            if (space.getPrefixCode().length() < 3) {
                errors.rejectValue("prefixCode", "error.space.prefixCode.tooshort",
                        "Length should be at least 3 characters.");
            }
            if (space.getPrefixCode().length() > 10) {
                errors.rejectValue("prefixCode", "error.space.prefixCode.toolong",
                        "Length should not be greater than 10 characters.");
            }            
            if (!ValidationUtils.isAllUpperCase(space.getPrefixCode())) {
                errors.rejectValue("prefixCode", "error.space.prefixCode.badchars",
                        "Only capital letters and numeric characters allowed.");
            }
        }
        if (space.getName() == null) {
            errors.rejectValue("name", ValidationUtils.ERROR_EMPTY_CODE, ValidationUtils.ERROR_EMPTY_MSG);
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
        context.getRequestScope().put("selectedFieldName", field.getNameText());
        return success();
    }  
    
    public Event fieldDeleteHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String fieldName = ValidationUtils.getParameter(context, "fieldName");            
        Field field = space.getMetadata().getField(fieldName);        
        if (space.getId() > 0) {
            int affectedCount = jtrac.loadCountOfRecordsHavingFieldNotNull(space, field);
            if (affectedCount > 0) {
                context.getRequestScope().put("affectedCount", affectedCount);
                return new Event(this, "confirm");
            }
        }
        // this is an unsaved space or there are no impacted items
        space.getMetadata().removeField(fieldName);
        return success();
    }
    
    public Event fieldDeleteConfirmHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String fieldName = ValidationUtils.getParameter(context, "fieldName");            
        Field field = space.getMetadata().getField(fieldName);
        // database will be updated, if we don't do this
        // user may leave without committing metadata change
        logger.debug("saving space after field delete operation");
        jtrac.bulkUpdateFieldToNull(space, field);
        space.getMetadata().removeField(fieldName);       
        jtrac.storeSpace(space);
        // horrible hack, but otherwise if we save again we get the dreaded Stale Object Exception
        space.setMetadata(jtrac.loadMetadata(space.getMetadata().getId()));
        return success();
    }        
    
    //=============================== STATES ===================================
    
    public Event stateFormSetupHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String stateKey = ValidationUtils.getParameter(context, "stateKey");
        if (stateKey != null) {
            context.getRequestScope().put("stateKey", stateKey);
            context.getRequestScope().put("state", space.getMetadata().getStates().get(Integer.parseInt(stateKey)));
        }
        return success();
    }
    
    public Event stateFormHandler(RequestContext context) throws Exception {
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
        if (stateKey == null) {
            space.getMetadata().addState(state);
        } else {
            space.getMetadata().getStates().put(Integer.parseInt(stateKey), state);
        }
        return success();
    } 
    
    public Event stateDeleteHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String stateKey = ValidationUtils.getParameter(context, "stateKey");
        int status = Integer.parseInt(stateKey);
        if (space.getId() > 0) {
            int affectedCount = jtrac.loadCountOfRecordsHavingStatus(space, status);
            if (affectedCount > 0) {
                context.getRequestScope().put("affectedCount", affectedCount);
                context.getRequestScope().put("stateKey", stateKey);
                context.getRequestScope().put("state", ValidationUtils.getParameter(context, "state"));
                return new Event(this, "confirm");
            }
        }
        // this is an unsaved space or there are no impacted items
        space.getMetadata().removeState(status);
        return success();
    }
    
    public Event stateDeleteConfirmHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String stateKey = ValidationUtils.getParameter(context, "stateKey");
        int status = Integer.parseInt(stateKey);
        // database will be updated, if we don't do this
        // user may leave without committing metadata change
        logger.debug("saving space after field delete operation");
        jtrac.bulkUpdateStatusToOpen(space, status);
        space.getMetadata().removeState(status);       
        jtrac.storeSpace(space);
        // horrible hack, but otherwise if we save again we get the dreaded Stale Object Exception
        space.setMetadata(jtrac.loadMetadata(space.getMetadata().getId()));
        return success();
    }    
    
    //================================= ROLES ==================================
    
    public Event roleFormSetupHandler(RequestContext context) {        
        String roleKey = ValidationUtils.getParameter(context, "roleKey");
        if (roleKey != null) {
            context.getRequestScope().put("oldRoleKey", roleKey);
            context.getRequestScope().put("roleKey", roleKey);
        }
        return success();
    }    
 
    public Event roleFormHandler(RequestContext context) throws Exception {
        Space space = (Space) context.getFlowScope().get("space");
        String roleKey = ValidationUtils.getParameter(context, "roleKey");
        String oldRoleKey = ValidationUtils.getParameter(context, "oldRoleKey");        
        // needed for errors or if confirm rename screen to be shown
        context.getRequestScope().put("oldRoleKey", oldRoleKey);
        context.getRequestScope().put("roleKey", roleKey);        
        if (!ValidationUtils.isAllUpperCase(roleKey)) {
            Errors errors = getFormErrors(context);
            errors.reject("error.spaceRoles.role.name.badchars", "Role name has to be all capital letters or digits.");
            return error();
        }
        if (oldRoleKey == null) {
            space.getMetadata().addRole(roleKey);
        } else if (!oldRoleKey.equals(roleKey)) {
            if (space.getId() > 0) {
                return new Event(this, "confirm");
            } else {
                space.getMetadata().renameRole(oldRoleKey, roleKey);
                jtrac.updateSpaceRole(oldRoleKey, roleKey, space);
            }
        }
        return success();
    }     
    
    public Event roleFormConfirmHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String roleKey = ValidationUtils.getParameter(context, "roleKey");
        String oldRoleKey = ValidationUtils.getParameter(context, "oldRoleKey");
        // TODO next 3 lines should ideally be in a transaction
        jtrac.updateSpaceRole(oldRoleKey, roleKey, space);
        space.getMetadata().renameRole(oldRoleKey, roleKey);
        jtrac.storeSpace(space);
        // horrible hack, but otherwise if we save again we get the dreaded Stale Object Exception
        space.setMetadata(jtrac.loadMetadata(space.getMetadata().getId()));        
        // refresh role information for logged on user
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        return success();
    }
    
    public Event roleDeleteHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String roleKey = ValidationUtils.getParameter(context, "roleKey");
        if (space.getId() > 0) {
            List<User> users = jtrac.findUsersWithRoleForSpace(space.getId(), roleKey);
            if (users.size() > 0) {
                String oldRoleKey = ValidationUtils.getParameter(context, "oldRoleKey");
                context.getRequestScope().put("oldRoleKey", oldRoleKey);
                context.getRequestScope().put("roleKey", roleKey);                 
                return new Event(this, "confirm");
            }
        }
        // this is an unsaved space or there are no impacted users
        space.getMetadata().removeRole(roleKey);                   
        return success();
    }
    
    public Event roleDeleteConfirmHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        String roleKey = ValidationUtils.getParameter(context, "roleKey");
        // database will be updated, if we don't do this
        // user may leave without committing metadata change
        logger.debug("saving space after role delete operation");
        jtrac.bulkUpdateDeleteSpaceRole(space, roleKey);
        space.getMetadata().removeRole(roleKey);        
        jtrac.storeSpace(space);
        // horrible hack, but otherwise if we save again we get the dreaded Stale Object Exception
        space.setMetadata(jtrac.loadMetadata(space.getMetadata().getId()));
        SecurityUtils.refreshSecurityContext();
        return success();
    }    
    
    //======================== TRANSITION / MASK ===============================
    
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
    
    //============================ SAVE ========================================
    
    public Event spaceSaveHandler(RequestContext context) {
        Space space = (Space) context.getFlowScope().get("space");
        jtrac.storeSpace(space);
        return success();
    }
    
    //========================== ALLOCATE ======================================
    
    public Event spaceAllocateSetup(RequestContext context) {
        context.getFlowScope().put("_flowId", "space");
        Space space = (Space) context.getFlowScope().get("space");
        if (space == null) {
            String spaceId = ValidationUtils.getParameter(context, "spaceId");
            int id = Integer.parseInt(spaceId);
            space = jtrac.loadSpace(id);           
            context.getFlowScope().put("space", space);
        }        
        context.getRequestScope().put("userSpaceRoles", jtrac.findUserRolesForSpace(space.getId()));
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
        jtrac.storeUserSpaceRole(user, space, roleKey);
        if (admin != null) {
            jtrac.storeUserSpaceRole(user, space, "ROLE_ADMIN");
        }
        SecurityUtils.refreshSecurityContextIfPrincipal(user);
        return success();
    }    

    public Event spaceDeallocateHandler(RequestContext context) {        
        String userSpaceRoleId = ValidationUtils.getParameter(context, "deallocate");        
        long id = Long.parseLong(userSpaceRoleId);
        UserSpaceRole userSpaceRole = jtrac.loadUserSpaceRole(id);
        jtrac.removeUserSpaceRole(userSpaceRole);
        SecurityUtils.refreshSecurityContextIfPrincipal(userSpaceRole.getUser());
        return success();
    }   
    
}
