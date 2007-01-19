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

package info.jtrac.wicket;

import info.jtrac.Jtrac;
import wicket.Component;
import wicket.MarkupContainer;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.border.Border;

/**
 * base class for all wicket pages, this provides
 * a way to access the spring managed service layer
 * also takes care of the standard template for all
 * pages which is a wicket border
 */
public abstract class BasePage extends WebPage {
    
    protected Border border;        
    
    protected Jtrac getJtrac() {
        return ((JtracApplication) getApplication()).getJtrac();
    }  
    
    public BasePage(String title) {
        add(new Label("title", title));
        border = new TemplateBorder();
        add(border);    
    }
    
}
