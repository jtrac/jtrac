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

import info.jtrac.domain.User;
import info.jtrac.util.UserUtils;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acegisecurity.Authentication;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Extension of Acegi class to set the users locale from database profile
 */
public class JtracAuthenticationProcessingFilter extends AuthenticationProcessingFilter {
    
    private final Log logger = LogFactory.getLog(getClass());
    
    @Override
    public void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        super.onSuccessfulAuthentication(request, response, authentication);
        User user = (User) authentication.getPrincipal();
        logger.debug("successful authentication post processing for user " + user);
        UserUtils.refreshLocale(request, response, user.getLocale());
    }
    
}
