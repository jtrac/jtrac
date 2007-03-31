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

/**
 * Helper class that improves on the Spring ValidationUtils
 * for inserting error messages for form object fields
 * into the view "errors" object
 */
public class ValidationUtils {
    
    public static final String ERROR_EMPTY_CODE = "error.empty";
    public static final String ERROR_EMPTY_MSG = "Value Required";
    
    public static void rejectIfEmpty(Errors errors, String... names) {
        for (String name : names) {
            org.springframework.validation.ValidationUtils.rejectIfEmpty(errors, name, ERROR_EMPTY_CODE);
        }
    }    
    
    public static boolean isAllUpperCase(String input) {
        if (input == null) {
            return false;
        }
        return input.matches("[A-Z0-9]+");
    }    
    
    public static boolean isValidLoginName(String input) {
        if (input == null) {
            return false;
        }
        return input.matches("[A-Za-z0-9._\\-]+");
    }     
    
    /**
     * Only letters are allowed, not even numbers
     * and CamelCase with dash as word separator
     */
    public static boolean isCamelDashCase(String input) {
        if (input == null) {
            return false;
        } 
        return input.matches("[A-Z][a-z]+(-[A-Z][a-z]+)*");
    }
    
    
}
