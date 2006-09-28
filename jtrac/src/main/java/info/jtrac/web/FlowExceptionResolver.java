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

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.webflow.ExternalContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.repository.PermissionDeniedFlowExecutionAccessException;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.ResponseInstruction;
import org.springframework.webflow.executor.mvc.FlowController;
import org.springframework.webflow.executor.support.FlowExecutorArgumentExtractor;
import org.springframework.webflow.support.ApplicationView;

/**
 * Spring MVC exception handler resolver designed to gracefully handle
 * the case where the user hit the browser back button during a WebFlow
 */
public class FlowExceptionResolver implements HandlerExceptionResolver{
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {        
        if (!e.getClass().isAssignableFrom(PermissionDeniedFlowExecutionAccessException.class)) {
            logger.debug("returning null cannot resolve type " + e.getClass());
            return null;
        } 
        logger.debug("user must have hit browser back button, trying to handle gracefully");
        return new ModelAndView("exception_flow");        
    }
    
}
