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

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * logout page.  the session invalidation code is in HeaderPanel
 */
public class LogoutPage extends WebPage {              
    
    private static final Logger logger = LoggerFactory.getLogger(LogoutPage.class);
    
    public LogoutPage(PageParameters params) {
        String locale = params.getString("locale");
        if(locale != null) {
            getRequestCycle().getSession().setLocale(StringUtils.parseLocaleString(locale));
        }
        setVersioned(false);
        add(new IndividualHeadPanel().setRenderBodyOnly(true));
        add(new Label("title", getLocalizer().getString("logout.title", null)));
        String jtracVersion = JtracApplication.get().getJtrac().getReleaseVersion();
        add(new Label("version", jtracVersion));                  
    }    
    
}
