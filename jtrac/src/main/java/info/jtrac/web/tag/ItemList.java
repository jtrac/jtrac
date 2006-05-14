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

import info.jtrac.domain.AbstractItem;
import info.jtrac.domain.Attachment;
import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.ItemSearch;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ItemList extends SimpleTagSupport {
    
    private List<AbstractItem> items;
    private ItemSearch itemSearch;
    
    public void setItems(List<AbstractItem> items) {
        this.items = items;
    }
    
    public void setItemSearch(ItemSearch itemSearch) {
        this.itemSearch = itemSearch;
    }        
    
    @Override
    public void doTag() {
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();        
        JspWriter out = pageContext.getOut();
        try {                        
            
            // pagination
            String flowUrlParam = "_flowExecutionKey=" + request.getAttribute("flowExecutionKey");
            String flowUrl = "flow.htm?" + flowUrlParam;
            StringBuffer sb = new StringBuffer();
            long resultCount = itemSearch.getResultCount(); 
            String plural = resultCount == 1 ? "" : "s";
            sb.append("<a href='" + flowUrl + "&_eventId=back' title='Modify Search'>" + resultCount + " record" + plural + " found.</a>&nbsp;&nbsp;");
            int pageSize = itemSearch.getPageSize();
            int pageCount = 0;
            if (pageSize != -1) {
                pageCount = (int) Math.ceil((double) resultCount / pageSize);
            }
            if (pageCount > 1) {
                String pageUrl = flowUrl + "&_eventId=page&page=";
                sb.append("<span class='page-links'>");                
                int currentPage = itemSearch.getCurrentPage();
                if (currentPage == 0) {
                    sb.append("&lt;&lt;&nbsp;&nbsp;");
                } else {
                    sb.append("<a href='" + response.encodeURL(pageUrl + (currentPage - 1)) + "'>&lt;&lt;</a>&nbsp;&nbsp;");
                }
                for(int i = 0; i < pageCount; i++) {                    
                    if (currentPage == i) {
                        sb.append((i + 1) +"&nbsp;&nbsp;");
                    } else {
                        sb.append("<a href='" + response.encodeURL(pageUrl + i) + "'>" + (i + 1) +"</a>&nbsp;&nbsp;");
                    }
                }
                if (currentPage == pageCount - 1) {
                    sb.append("&gt;&gt;");
                } else {
                    sb.append("<a href='" + response.encodeURL(pageUrl + (currentPage + 1)) + "'>&gt;&gt;</a>");  
                }                
                sb.append("</span>");
            }
            // write out record count + pagination
            out.println("<table class='jtrac bdr-collapse' width='100%'><tr><td>" + sb + "</td>");
                                    
            out.println("<td align='right'><a href='item_list_excel.htm?" + flowUrlParam + "'>(export to excel)</a></td></tr></table><p/>");
            
            boolean showDetail = itemSearch.isShowDetail();
            boolean showHistory = itemSearch.isShowHistory();
            List<Field> fields = itemSearch.getFields();
            
            out.println("<table class='jtrac'>");
            out.println("<tr>");
            out.println("  <th>ID</th>");
            out.println("  <th>Summary</th>");
            
            if (showDetail) {
                out.println("  <th>Detail</th>");
            }
            
            out.println("  <th>Logged By</th>");
            out.println("  <th>Status</th>");
            out.println("  <th>Assigned To</th>");
            for(Field field : fields) {
                out.println("  <th>" + field.getLabel() + "</th>");
            }
            out.println("  <th>Time Stamp</th>");
            out.println("</tr>");
            int count = 1;
            String itemUrl = flowUrl + "&_eventId=view&itemId=";
            for(AbstractItem item : items) {
                out.println("<tr" + ( count % 2 == 0 ? " class='alt'" : "" ) + ">");
                String href = null;
                if (showHistory) {
                    href = response.encodeURL(itemUrl + item.getParent().getId());
                } else {
                    href = response.encodeURL(itemUrl + item.getId());
                }                
                out.println("  <td><a href='" + href + "'>" + item.getRefId() + "</a></td>");
                out.println("  <td>" + ( item.getSummary() == null ? "" : item.getSummary() ) + "</td>");
                
                if (showDetail) {
                    if (showHistory) {
                        History h = (History) item;
                        out.println("  <td>");                        
                        Attachment attachment = h.getAttachment();
                        if (attachment != null) {
                            String attHref = response.encodeURL("attachments/" + attachment.getFileName() +"?filePrefix=" + attachment.getFilePrefix());
                            out.println("<a target='_blank' href='" + attHref + "'>" + attachment.getFileName() + "</a>&nbsp;");             
                        }                                                
                        out.println(( h.getComment() == null ? "" : h.getComment() ) + "</td>");
                    } else {
                        out.println("  <td>" + item.getDetail() + "</td>");
                    }
                }                
                
                out.println("  <td>" + item.getLoggedBy().getName() + "</td>");
                out.println("  <td>" + item.getStatusValue() + "</td>");
                out.println("  <td>" + ( item.getAssignedTo() == null ? "" : item.getAssignedTo().getName() ) + "</td>");
                for(Field field : fields) {
                    out.println("  <td>" + item.getCustomValue(field.getName()) + "</td>");
                }
                out.println("  <td>" + item.getTimeStamp() + "</td>");
                out.println("</tr>");
                count++;
            }
            out.println("</table>");
            // re write out record count + pagination
            out.println("<p/>" + sb);            
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }        
    }
    
}
