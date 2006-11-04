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

import info.jtrac.domain.User;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;

/**
 * routines to make working with Acegi easier
 */
public class SecurityUtils {
    
    /**
     * routine to refresh the security context only if user == principal
     * required to do the following
     * - authorization changes to take effect without having to logoff and re-login
     * - cosmetic changes (such as change to name) to take effect
     */
    public static void refreshSecurityContextIfPrincipal(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User u = (User) authentication.getPrincipal();
        if (u.getId() == user.getId()) {
            // forces the Acegi Security Context to reload            
            authentication.setAuthenticated(false);             
        }
    }
    
    public static void refreshSecurityContext() {
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
    }
    
}
