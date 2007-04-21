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

package info.jtrac.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

/**
 * spring multiactioncontroller, for backwards compatibility with old email links
 */
public class DefaultMultiActionController extends AbstractMultiActionController {     
    
    public ModelAndView itemViewHandler(HttpServletRequest request, HttpServletResponse response) {
        String itemId = request.getParameter("itemId");
        return new ModelAndView("redirect:/app/item/" + itemId);
    } 
    
}
