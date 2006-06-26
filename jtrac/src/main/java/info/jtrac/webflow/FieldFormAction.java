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
import info.jtrac.util.ValidationUtils;

import java.io.Serializable;
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
            String option = fieldForm.getOption();
            if (fieldForm.getField().hasOption(option)) {
                errors.rejectValue("option", "fieldForm.option.exists", "Option already exists");
            }            
        }
        
    }
    
    public Event fieldUpdateHandler(RequestContext context) throws Exception {
        FieldForm fieldForm = (FieldForm) getFormObject(context);
        String option = fieldForm.getOption();
        if (option != null && !option.equals("")) {
            fieldForm.getField().addOption(option);
            fieldForm.setOption(null);
        }
        return success();
    }
    
}
