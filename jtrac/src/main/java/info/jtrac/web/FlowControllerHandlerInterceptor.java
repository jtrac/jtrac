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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.webflow.executor.mvc.FlowController;

/**
 * custom HandlerInterceptor that is only interested in post processing the
 * Spring Web Flow "flowController" handling of the request
 * works in conjunction with the FlowExceptionResolver
 */
public class FlowControllerHandlerInterceptor extends HandlerInterceptorAdapter {
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (handler.getClass().equals(FlowController.class)) {
            Object flowExecutionKey = modelAndView.getModel().get("flowExecutionKey");
            request.getSession().setAttribute("lastRequestParameterMap", request.getParameterMap());
            request.getSession().setAttribute("lastFlowExecutionKey", flowExecutionKey);
            if (logger.isDebugEnabled()) {
                logger.debug("lastFlowExecutionKey session attribute set as: " + flowExecutionKey);
            }            
        }
    }


}
