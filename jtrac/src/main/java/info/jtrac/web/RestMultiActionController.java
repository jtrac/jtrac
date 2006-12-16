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

import info.jtrac.domain.Item;
import info.jtrac.exception.InvalidRefIdException;
import info.jtrac.util.ItemUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring MultiActionController that handles REST requests
 * returns XML messages
 */
public class RestMultiActionController extends AbstractMultiActionController {
    
    private String getPathParameter(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        logger.debug("pathInfo = '" + pathInfo + "'");
        int index = pathInfo.lastIndexOf('/');
        String parameter = null;
        if (index != -1) {
            parameter = pathInfo.substring(index + 1);
        }
        logger.debug("parameter extracted from request path = '" + parameter + "'");
        return parameter;
    }
    
    public void itemHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String refId = getPathParameter(request);
        Item item = null;
        try {
            item = jtrac.loadItemByRefId(refId);
        } catch (InvalidRefIdException e) {
            // TODO
        }
        // TODO if item == null
        applyCacheSeconds(response, 0, true);
        response.setContentType("text/xml");      
        if (item != null) {
            ItemUtils.getAsXml(item).write(response.getWriter());
        }              
    } 
    
    
}
