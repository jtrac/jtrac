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

package info.jtrac.acegi;

import info.jtrac.Jtrac;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import java.util.List;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Custom Acegi Authentication Provider designed to plug along with other Aceg
 * providers and implement our custom "anonymous" Authentication strategy
 * This allows users to browse projects that have "Guest Allowed"
 * without signing on.
 */
public class GuestAuthenticationProvider implements AuthenticationProvider {        
    
    private final Log logger = LogFactory.getLog(getClass());
    
    public boolean supports(Class clazz) {
        return (GuestAuthenticationToken.class.isAssignableFrom(clazz));
    }    
    
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        return authentication;
    }
    
}
