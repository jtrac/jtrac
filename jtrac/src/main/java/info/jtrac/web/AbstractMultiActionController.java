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
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * base class for all Spring MVC MultiActionControllers
 * also contains code for getting hold of a Spring WebFlow context
 * from request, response
 */
public abstract class AbstractMultiActionController extends MultiActionController {        
    
    protected Jtrac jtrac;    
    
    public void setJtrac(Jtrac jtrac) {
        this.jtrac = jtrac;
    }    
    
}
