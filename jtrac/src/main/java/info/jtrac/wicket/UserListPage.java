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

import info.jtrac.domain.User;
import wicket.markup.html.link.Link;

/**
 * user management page
 */
public class UserListPage extends BasePage {
      
    public UserListPage() {
        
        super("Options Menu");      
        
        add(new HeaderPanel(null));
        
        border.add(new Link("create") {
            public void onClick() {                
                setResponsePage(new UserFormPage());
            }            
        });                
        
    }
    
}
