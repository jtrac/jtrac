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
import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.acegisecurity.context.HttpSessionContextIntegrationFilter;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.ui.AuthenticationDetailsSource;
import org.acegisecurity.ui.AuthenticationDetailsSourceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Custom Acegi Servlet Filter designed to plug along with other Acegi
 * Filters and implement our custom "anonymous" Authentication strategy
 * This allows users to browse projects that have "Guest Allowed"
 * without signing on.
 */
public class GuestProcessingFilter implements Filter {
    
    private Jtrac jtrac;
    private AuthenticationDetailsSource authenticationDetailsSource = new AuthenticationDetailsSourceImpl();
    
    public void setJtrac(Jtrac jtrac) {
        this.jtrac = jtrac;
    }       
    
    private static final Log logger = LogFactory.getLog(GuestProcessingFilter.class);
    
    public void init(FilterConfig filterConfig) {
        // ignored
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {        
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            List<Space> spaces = jtrac.findSpacesWhereGuestAllowed();
            if (spaces.size() > 0) {
                User guestUser = new User();
                guestUser.setLoginName("guest");
                guestUser.setName("Guest");
                guestUser.addSpaceWithRole(null, "ROLE_GUEST");
                for (Space space : spaces) {            
                    guestUser.addSpaceWithRole(space, "ROLE_GUEST");
                }        
                GuestAuthenticationToken authentication = new GuestAuthenticationToken(guestUser, guestUser.getAuthorities());
                authentication.setDetails(authenticationDetailsSource.buildDetails((HttpServletRequest) request));                
                SecurityContextHolder.getContext().setAuthentication(authentication);              
                if (logger.isDebugEnabled()) {
                    logger.debug("populated SecurityContextHolder with guest user: " + guestUser);
                }
                // this only happens once, see the hack in header.jsp for more
                request.setAttribute("principal", authentication.getPrincipal());
            }
        }       
        chain.doFilter(request, response);
    }

    public void destroy() {
        // ignored
    }
    
}
