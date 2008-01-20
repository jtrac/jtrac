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

import javax.servlet.http.Cookie;

/**
 * utilities for http, web related stuff etc
 */
public class WebUtils {

    public static String getDebugStringForCookie(Cookie cookie) {
        return  "domain: '" + cookie.getDomain() + "', " 
                + "name: '" + cookie.getName() + "', " 
                + "path: '" + cookie.getPath() + "', " 
                + "value: '" + cookie.getValue() + "', " 
                + "secure: '" + cookie.getSecure() + "', " 
                + "version: '" + cookie.getVersion() + "', " 
                + "maxAge: '" + cookie.getMaxAge() + "', " 
                + "comment: '" + cookie.getComment() + "'";
    }
    
}
