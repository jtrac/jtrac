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

package net.jdev.jtrac.web.tag;

import info.jtrac.domain.Item;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.jdev.jtrac.entity.Item;
import net.jdev.jtrac.entity.Tracker;

public class ItemView extends SimpleTagSupport {
    
    private Item item;
    
    public void setItem(Item item) {
        this.item = item;
    }
    
    
    public void doTag() {
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        JspWriter out = pageContext.getOut();
        try {
            // out.print(item.getAsHtml(tracker, historyId, response));
        } catch (Exception e) {
            System.out.println("*** error in ItemView: " + e);
            e.printStackTrace();
        }
        
    }
    
}
