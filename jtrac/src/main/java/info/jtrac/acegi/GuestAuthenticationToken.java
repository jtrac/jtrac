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

import java.io.Serializable;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.providers.AbstractAuthenticationToken;

/**
 * Custom Acegi Authentication Token designed to plug along with Acegi authentication
 * providers and implement our custom "anonymous" Authentication strategy
 * This allows users to browse projects that have "Guest Allowed"
 * without signing on.
 */
public class GuestAuthenticationToken extends AbstractAuthenticationToken implements Serializable {
    
    Object principal;
    
    public GuestAuthenticationToken(Object principal, GrantedAuthority[] authorities) {
        super(authorities);
        this.principal = principal;
    }
    
    public Object getPrincipal() {
        return principal;
    }
    
    public Object getCredentials() {
        return "";
    }    
}
