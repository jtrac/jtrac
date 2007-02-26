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

import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.protocol.http.WebResponse;

/**
 * header navigation
 */
public class HeaderPanel extends BasePanel {    
    
    public HeaderPanel() {
        super("header");
        
        final User user = ((JtracSession) getSession()).getUser();
        final Space space = ((JtracSession) getSession()).getCurrentSpace();
        
        add(new Link("dashboard") {
            public void onClick() {
                setResponsePage(DashboardPage.class);
            }            
        });
        
        if (space == null) {
            add(new Label("space", "").setVisible(false));
            add(new Label("new", "").setVisible(false));
            add(new Link("search") {
                public void onClick() {
                    List<Space> spaces = new ArrayList(user.getSpaces());
                    // if only one space don't use generic search screen
                    if(spaces.size() == 1) {
                        setResponsePage(new ItemSearchFormPage(spaces.get(0)));
                    } else {
                        setResponsePage(new ItemSearchFormPage(user));
                    }                                         
                }            
            });            
        } else {
            add(new Label("space", space.getName()));            
            if (user.getPermittedTransitions(space, State.NEW).size() > 0) {            
                add(new Link("new") {
                    public void onClick() {
                        setResponsePage(new ItemFormPage(space));
                    }            
                });
            } else {
                add(new WebMarkupContainer("new").setVisible(false));       
            }
            
            add(new Link("search") {
                public void onClick() {
                    setResponsePage(new ItemSearchFormPage(space));
                }            
            });            
        }
        
        if(user.getId() == 0) {
            add(new WebMarkupContainer("options").setVisible(false));
            add(new WebMarkupContainer("logout").setVisible(false));
            add(new Link("login") {
                public void onClick() {
                    setResponsePage(LoginPage.class);
                }            
            });
            add(new WebMarkupContainer("user").setVisible(false));
        } else {
            add(new Link("options") {
                public void onClick() {
                    setResponsePage(OptionsPage.class);
                }            
            }); 
            add(new Link("logout") {
                public void onClick() {                                        
                    Cookie cookie = new Cookie("jtrac", user.getLoginName() 
                        + ":" + getJtrac().encodeClearText(user.getPassword()));
                    ((WebResponse) getRequestCycle().getResponse()).clearCookie(cookie);                    
                    getSession().invalidate();
                    logger.debug("invalidated session and cleared cookie");                    
                    setResponsePage(LogoutPage.class);
                }            
            });
            add(new WebMarkupContainer("login").setVisible(false));
            add(new Label("user", user.getName()));
        }             
        
    }
    
}
