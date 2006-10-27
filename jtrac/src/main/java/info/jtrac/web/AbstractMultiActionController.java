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

import info.jtrac.Jtrac;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.FlowExecutorImpl;

/**
 * base class for all Spring MVC MultiActionControllers
 * also contains code for getting hold of a Spring WebFlow context
 * from request, response
 */
public abstract class AbstractMultiActionController extends MultiActionController {
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    protected Jtrac jtrac;
    private FlowExecutionRepository repository;
    
    public void setJtrac(Jtrac jtrac) {
        this.jtrac = jtrac;
    }

    public void setFlowExecutor(FlowExecutor flowExecutor) {
        FlowExecutorImpl executor = (FlowExecutorImpl) flowExecutor;
        this.repository = executor.getExecutionRepository();
    }
    
    /**
     * this is the bridge between the Spring WebFlow world and the Spring MVC world
     * needed for getting access to "stateful" objects within a flow
     */  
    protected FlowExecution getFlowExecution(HttpServletRequest request, HttpServletResponse response) {        
        ExternalContext externalContext = new ServletExternalContext(getServletContext(), request, response);
        ExternalContextHolder.setExternalContext(externalContext);
        String flowExecutionKey = request.getParameter("_flowExecutionKey");
        return repository.getFlowExecution(repository.parseFlowExecutionKey(flowExecutionKey));        
    }
    
}
