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

package info.jtrac.web.tag;

import info.jtrac.domain.ItemUser;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class MultiSelect extends SimpleTagSupport {
    
    private Set<ItemUser> selected;
    private List<UserSpaceRole> list;
    private String name;
    
    public void setName(String name) {
        this.name = name;
    }
    public void setList(List<UserSpaceRole> list) {
        this.list = list;
    }
    public void setSelected(Set<ItemUser> selected) {
        this.selected = selected;
    }
    
    @Override
    public void doTag() {
        try {
            PageContext pageContext = (PageContext) getJspContext();
            JspWriter out = pageContext.getOut();
            if (list != null) {
                out.println("<div class='multiselect'>");
                StringBuffer sb = new StringBuffer();
                boolean hasSelected = false;

                for(UserSpaceRole usr : list) {
                    User user = usr.getUser();
                    if (selected != null && selected.contains(new ItemUser(user))) {
                        hasSelected = true;
                        out.print("<input type='checkbox' name='" + name + "' value='" + user.getId() + "'");
                        out.println(" checked='true'/>" + user.getName() + "<br/>");
                    } else {
                        sb.append("<input type='checkbox' name='" + name + "' value='" + user.getId() + "'");
                        sb.append("/>" + user.getName() + "<br/>\n");
                    }
                }          
                
                if (hasSelected) {
                    out.println("<hr/>");
                }
                out.print(sb);
                sb.append("</div>");
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
}