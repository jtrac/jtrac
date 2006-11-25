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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acegisecurity.Authentication;
import org.acegisecurity.ui.rememberme.TokenBasedRememberMeServices;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Extension of Acegi class to set the users locale from database profile
 * this gets into action only when the remember-me auto login happens
 */
public class JtracRememberMeServices extends TokenBasedRememberMeServices {
    
    private final Log logger = LogFactory.getLog(getClass());
    
    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = super.autoLogin(request, response);
        if (authentication == null) {
            return null;
        }
        User user = (User) authentication.getPrincipal();
        logger.debug("successful remember-me auto-login authentication post processing for user " + user);
        UserUtils.refreshLocale(request, response, user.getLocale());
        return authentication;
    }
    
}
