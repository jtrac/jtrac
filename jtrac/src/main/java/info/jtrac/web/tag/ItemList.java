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
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.HtmlUtils;

public class ItemList extends SimpleTagSupport {
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    private List<AbstractItem> items;
    private ItemSearch itemSearch;
    
    public void setItems(List<AbstractItem> items) {
        this.items = items;
    }
    
    public void setItemSearch(ItemSearch itemSearch) {
        this.itemSearch = itemSearch;
    }
    
    private static String fmt(String key, MessageSource messageSource, Locale locale) {
        try {
            return messageSource.getMessage("item_list." + key, null, locale);
        } catch (Exception e) {
            return "???item_list." + key + "???";
        }
    }    
    
    @Override
    public void doTag() {
        PageContext pageContext = (PageContext) getJspContext();
        request = (HttpServletRequest) pageContext.getRequest();        
        response = (HttpServletResponse) pageContext.getResponse();
        Locale loc = RequestContextUtils.getLocale(request);
        MessageSource ms = RequestContextUtils.getWebApplicationContext(request);         
        JspWriter out = pageContext.getOut();
        try {
            
            //=============================== PAGINATION =====================================
            String flowUrlParam = "_flowExecutionKey=" + request.getAttribute("flowExecutionKey");
            String flowUrl = "/flow?" + flowUrlParam;
            StringBuffer sb = new StringBuffer();
            long resultCount = itemSearch.getResultCount();
            String recordsFound = resultCount == 1 ? "recordFound" : "recordsFound";
            sb.append("<a href='" + encodeURL(flowUrl + "&_eventId=back'") + " title='" + fmt("modifySearch", ms, loc) + "'>" 
                    + resultCount + " " + fmt(recordsFound, ms, loc) + "</a>&nbsp;&nbsp;");
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
                    sb.append("<a href='" + encodeURL(pageUrl + (currentPage - 1)) + "'>&lt;&lt;</a>&nbsp;&nbsp;");
                }
                for(int i = 0; i < pageCount; i++) {
                    if (currentPage == i) {
                        sb.append((i + 1) +"&nbsp;&nbsp;");
                    } else {
                        sb.append("<a href='" + encodeURL(pageUrl + i) + "'>" + (i + 1) +"</a>&nbsp;&nbsp;");
                    }
                }
                if (currentPage == pageCount - 1) {
                    sb.append("&gt;&gt;");
                } else {
                    sb.append("<a href='" + encodeURL(pageUrl + (currentPage + 1)) + "'>&gt;&gt;</a>");
                }
                sb.append("</span>");
            }
            // write out record count + pagination
            out.println("<table class='jtrac bdr-collapse' width='100%'><tr><td>" + sb + "</td>");            
            out.println("<td align='right'><a href='" + encodeURL("/app/item_list_excel.htm?" + flowUrlParam) + "'>(" 
                    + fmt("exportToExcel", ms, loc) + ")</a></td></tr></table><p/>");
            
            //=============================== TABLE HEADER =====================================
            boolean showDetail = itemSearch.isShowDetail();
            boolean showHistory = itemSearch.isShowHistory();
            List<Field> fields = itemSearch.getFields();
            
            out.println("<table class='jtrac'>");
            out.println("<tr>");
            out.println("  <th>" + fmt("id", ms, loc) + "</th>");
            out.println("  <th>" + fmt("summary", ms, loc) + "</th>");
            
            if (showDetail) {
                out.println("  <th>" + fmt("detail", ms, loc) + "</th>");
            }
            
            out.println("  <th>" + fmt("loggedBy", ms, loc) + "</th>");
            out.println("  <th>" + fmt("status", ms, loc) + "</th>");
            out.println("  <th>" + fmt("assignedTo", ms, loc) + "</th>");
            for(Field field : fields) {
                out.println("  <th>" + field.getLabel() + "</th>");
            }
            out.println("  <th>" + fmt("timeStamp", ms, loc) + "</th>");
            out.println("</tr>");
            int count = 1;
            String itemUrl = flowUrl + "&_eventId=view&itemId=";
            String itemId = request.getParameter("itemId");
            int selected = 0;
            if (itemId != null) {
                selected = Integer.parseInt(itemId);
            }
            //=============================== ITERATE =====================================
            for(AbstractItem item : items) {
                String rowClass = "";
                String bookmark = "";
                if (selected != 0 && item.getId() == selected) {
                    rowClass = " class='selected'";
                    bookmark = "<a name='goto'/>";
                } else if (count % 2 == 0) {
                    rowClass = " class='alt'";
                }
                out.println("<tr" + rowClass + ">");
                String href = null;
                if (showHistory) {
                    href = encodeURL(itemUrl + item.getParent().getId());
                } else {
                    href = encodeURL(itemUrl + item.getId());
                }
                out.println("  <td>" + bookmark + "<a href='" + href + "'>" + item.getRefId() + "</a></td>");
                out.println("  <td>" + ( item.getSummary() == null ? "" : HtmlUtils.htmlEscape(item.getSummary()) ) + "</td>");
                
                if (showDetail) {
                    if (showHistory) {
                        History h = (History) item;
                        out.println("  <td>");
                        Attachment attachment = h.getAttachment();
                        if (attachment != null) {
                            String attHref = encodeURL("attachments/" + attachment.getFileName() +"?filePrefix=" + attachment.getFilePrefix());
                            out.println("<a target='_blank' href='" + attHref + "'>" + attachment.getFileName() + "</a>&nbsp;");
                        }
                        out.println(( h.getComment() == null ? "" : HtmlUtils.htmlEscape(h.getComment()) ) + "</td>");
                    } else {
                        out.println("  <td>" + (item.getDetail() == null ? "" : HtmlUtils.htmlEscape(item.getDetail())) + "</td>");
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
            // again write out record count + pagination
            out.println("<p/>" + sb);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    private String encodeURL(String url) {
        return response.encodeURL(request.getContextPath() + url);
    }
    
}
