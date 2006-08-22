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
import info.jtrac.domain.Space;
import info.jtrac.util.ValidationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.webflow.Event;
import org.springframework.webflow.RequestContext;
import org.springframework.webflow.ScopeType;

/**
 * Multiaction that participates in the "Space Create / Edit" flow
 * for editing individual Fields
 */
public class FieldFormAction extends AbstractFormAction {
    
    public FieldFormAction() {
        setFormObjectClass(FieldForm.class);
        setFormObjectName("fieldForm");
        // setBindOnSetupForm(false);
        setFormObjectScope(ScopeType.FLOW);
        setValidator(new FieldFormValidator());
    }
    
    /**
     * Form backing object
     */
    public static class FieldForm implements Serializable {
        
        private Space space;
        private transient Field field;
        private String option;
        
        public Field getField() {
            return field;
        }        
        
        public void setField(Field field) {
            this.field = field;
        }
        
        public String getOption() {
            return option;
        }
        
        public void setOption(String option) {
            this.option = option;
        }

        public void setSpace(Space space) {
            this.space = space;
        }
        
    }
    
    /**
     * A validator for our form backing object.
     */
    public static class FieldFormValidator implements Validator {
        
        public boolean supports(Class clazz) {
            return FieldForm.class.isAssignableFrom(clazz);
        }
        
        public void validate(Object o, Errors errors) {
            FieldForm fieldForm = (FieldForm) o;
            ValidationUtils.rejectIfEmpty(errors, "field.label");
            String option = fieldForm.option;
            if (fieldForm.field.hasOption(option)) {
                errors.rejectValue("option", "fieldForm.option.exists", "Option already exists");
            }            
        }
        
    }
    
    public Event fieldUpdateHandler(RequestContext context) throws Exception {
        FieldForm fieldForm = (FieldForm) getFormObject(context);
        String option = fieldForm.option;
        if (option != null && !option.equals("")) {
            fieldForm.field.addOption(option);
            fieldForm.setOption(null);
        }
        return success();
    }
    
    public Event fieldOptionEditSetupHandler(RequestContext context) throws Exception {
        FieldForm fieldForm = (FieldForm) getFormObject(context);
        String optionKey = ValidationUtils.getParameter(context, "optionKey");
        String option = fieldForm.field.getCustomValue(optionKey);
        context.getRequestScope().put("option", option);
        context.getRequestScope().put("optionKey", optionKey);
        return success();
    }
    
    public Event fieldOptionEditHandler(RequestContext context) throws Exception {
        String optionKey = ValidationUtils.getParameter(context, "optionKey");
        String option = ValidationUtils.getParameter(context, "option");
        if (option == null) {
            Errors errors = getFormErrors(context);
            errors.reject("error.field.option.empty", "Option text cannot be empty");
            context.getRequestScope().put("option", option);
            context.getRequestScope().put("optionKey", optionKey);
            return error();
        }
        FieldForm fieldForm = (FieldForm) getFormObject(context);
        fieldForm.field.addOption(optionKey, option); // will overwrite
        return success();
    }
    
    public Event fieldOptionDeleteSetupHandler(RequestContext context) throws Exception {
        FieldForm fieldForm = (FieldForm) getFormObject(context);
        String optionKey = ValidationUtils.getParameter(context, "optionKey");
        String option = fieldForm.field.getCustomValue(optionKey);
        context.getRequestScope().put("optionKey", optionKey);
        context.getRequestScope().put("option", option);
        int affectedCount = 0;
        if (fieldForm.space.getId() > 0) {
            affectedCount = jtrac.findItemCount(fieldForm.space, fieldForm.field, optionKey);
        }        
        context.getRequestScope().put("affectedCount", affectedCount);
        return success();
    }    
    
    public Event fieldOptionDeleteHandler(RequestContext context) throws Exception {
        FieldForm fieldForm = (FieldForm) getFormObject(context);
        String optionKey = ValidationUtils.getParameter(context, "optionKey");
        fieldForm.field.getOptions().remove(optionKey);
        if (fieldForm.space.getId() > 0) {
            jtrac.removeFieldValues(fieldForm.space, fieldForm.field, optionKey);
            // database has been updated, if we don't do this
            // user may leave without committing metadata change
            logger.debug("saving space after option delete operation");        
            jtrac.storeMetadata(fieldForm.space.getMetadata());            
        }
        return success();
    }
    
    public Event fieldOptionUpHandler(RequestContext context) throws Exception {
        FieldForm fieldForm = (FieldForm) getFormObject(context);
        String optionKey = ValidationUtils.getParameter(context, "optionKey");
        Map<String, String> options = fieldForm.field.getOptions();
        List<String> keys = new ArrayList<String>(options.keySet());
        int index = keys.indexOf(optionKey);
        int swapIndex = index - 1;
        if (swapIndex < 0 && keys.size() > 1) {
            swapIndex = keys.size() - 1;
        }
        if (index != swapIndex) {
            Collections.swap(keys, index, swapIndex);
        }
        Map<String, String> updated = new LinkedHashMap<String, String>(keys.size());
        for (String s : keys) {
            updated.put(s, options.get(s));
        }
        fieldForm.field.setOptions(updated);
        return success();
    }    
    
    public Event fieldOptionDownHandler(RequestContext context) throws Exception {
        FieldForm fieldForm = (FieldForm) getFormObject(context);
        String optionKey = ValidationUtils.getParameter(context, "optionKey");
        Map<String, String> options = fieldForm.field.getOptions();
        List<String> keys = new ArrayList<String>(options.keySet());
        int index = keys.indexOf(optionKey);
        int swapIndex = index + 1;
        if (swapIndex == keys.size() ) {
            swapIndex = 0;
        }
        if (index != swapIndex) {
            Collections.swap(keys, index, swapIndex);
        }
        Map<String, String> updated = new LinkedHashMap<String, String>(keys.size());
        for (String s : keys) {
            updated.put(s, options.get(s));
        }
        fieldForm.field.setOptions(updated);        
        return success();
    }      
    
}
