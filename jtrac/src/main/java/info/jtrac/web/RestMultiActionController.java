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
import info.jtrac.util.XmlUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

/**
 * Spring MultiActionController that handles REST requests
 * returns XML messages
 */
public class RestMultiActionController extends AbstractMultiActionController {
    
    /**
     * custom MethodNameResolver is configured that checks the value of an expected
     * paramter called "method" in the request and formats the value that may be
     * in the form of  "namespace.subnamespace.action" into "namespaceSubnamespaceAction"
     * or more like a java method name
     */
    public RestMultiActionController() {
        setMethodNameResolver(new MethodNameResolver() {
            public String getHandlerMethodName(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
                String temp = request.getParameter("method");
                if (temp == null) {
                    return null;
                }
                StringBuffer sb = new StringBuffer();
                for(int i = 0; i < temp.length(); i++) {
                    char c = temp.charAt(i);
                    if (c == '.') {
                        i++;
                        c = temp.charAt(i);
                        sb.append(Character.toUpperCase(c));
                    } else {
                        sb.append(c);
                    }
                }
                return sb.toString();
            }
        });
    }
    
    public void versionGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Document d = XmlUtils.getNewDocument("version");
        Element root = d.getRootElement();
        root.addAttribute("number", jtrac.getReleaseVersion());
        root.addAttribute("timestamp", jtrac.getReleaseTimestamp());
        applyCacheSeconds(response, 0, true);
        response.setContentType("text/xml");
        d.write(response.getWriter());
    }
    
    public void itemGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String refId = request.getParameter("refId");
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
