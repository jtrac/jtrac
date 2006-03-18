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

package info.jtrac.util;

import org.springframework.validation.Errors;
import org.springframework.webflow.RequestContext;

/**
 * Helper class that improves on the Spring ValidationUtils
 * for inserting error messages for form object fields
 * into the view "errors" object
 */
public class ValidationUtils {
    
    public static final String ERROR_EMPTY_CODE = "error.empty";
    public static final String ERROR_EMPTY_MSG = "Value Required.";
    
    public static void rejectIfEmpty(Errors errors, String... names) {
        for (String name : names) {
            org.springframework.validation.ValidationUtils.rejectIfEmpty(errors, name, ERROR_EMPTY_CODE, ERROR_EMPTY_MSG);
        }
    }
    
    public static String getParameter(RequestContext context, String name) {
        String value = (String) context.getRequestParameters().get(name);
        if (value == null || value.trim().equals("")) {
            return null;
        }
        return value;
    }
    
    public static boolean isAllUpperCase(String input) {
        if (input == null) {
            return false;
        }
        for (char c : input.toCharArray()) {
            if (!(Character.isUpperCase(c) || Character.isDigit(c))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAllLowerCase(String input) {
        if (input == null) {
            return false;
        }        
        for (char c : input.toCharArray()) {
            if (!(Character.isLowerCase(c) || Character.isDigit(c))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Only letters are allowed, not even numbers
     */
    public static boolean isTitleCase(String input) {
        if (input == null || input.length() == 0 || !Character.isUpperCase(input.charAt(0))) {
            return false;
        }
        if (input.length() > 1) {
            for (char c : input.substring(1).toCharArray()) {
                if (!(Character.isLowerCase(c))) {
                    return false;
                }
            }            
        }
        return true;        
    }
    
    
}
