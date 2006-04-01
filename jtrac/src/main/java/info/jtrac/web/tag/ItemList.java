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

import info.jtrac.domain.Field;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ItemList extends SimpleTagSupport {
    
    private List<Item> items;
    private ItemSearch itemSearch;
    
    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    public void setItemSearch(ItemSearch itemSearch) {
        this.itemSearch = itemSearch;
    }        
    
    @Override
    public void doTag() {
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        JspWriter out = pageContext.getOut();
        try {
            out.println("<table class='jtrac'>");
            out.println("<tr>");
            out.println("  <th>ID</th>");
            out.println("  <th>Summary</th>");
            out.println("  <th>Logged By</th>");
            out.println("  <th>Status</th>");
            out.println("  <th>Assigned To</th>");
            for(Field field : itemSearch.getFields()) {
                out.println("  <th>" + field.getLabel() + "</th>");
            }
            out.println("  <th>Time Stamp</th>");
            out.println("</tr>");
            int count = 1;
            for(Item item : items) {
                out.println("<tr" + ( count % 2 == 0 ? " class='alt'" : "" ) + ">");
                String href = response.encodeURL("flow.htm?_flowId=itemView&itemId=" + item.getId());
                out.println("  <td><a href='" + href + "'>" + item.getRefId() + "</a></td>");
                out.println("  <td>" + item.getSummary() + "</td>");
                out.println("  <td>" + item.getLoggedBy().getName() + "</td>");
                out.println("  <td>" + item.getStatusValue() + "</td>");
                out.println("  <td>" + ( item.getAssignedTo() == null ? "" : item.getAssignedTo().getName() ) + "</td>");
                for(Field field : itemSearch.getFields()) {
                    out.println("  <td>" + item.getCustomValue(field.getName()) + "</td>");
                }
                out.println("  <td>" + item.getTimeStamp() + "</td>");
                out.println("</tr>");
                count++;
            }
            out.println("</table>");
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }        
    }
    
}
