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

package info.jtrac.util;

import info.jtrac.domain.Attachment;
import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.util.HtmlUtils;

/**
 * Utilities to convert an Item into HTML etc.
 * The getAsHtml() routine is used to diplay an item - within a tag lib for JSP
 * And we are able to re-use this to send HTML e-mail etc.
 */
public final class ItemUtils {
    
    private static String fixWhiteSpace(String text) {
        if (text == null) {
            return "";
        }
        String temp = HtmlUtils.htmlEscape(text);  
        return temp.replaceAll("\n", "<br/>").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }
    
    public static String getAsHtml(Item item, HttpServletRequest request, HttpServletResponse response) {
        StringBuffer sb = new StringBuffer();
        sb.append("<table width='100%' class='jtrac'>");
        sb.append("<tr class='alt'>");
        sb.append("  <td class='label'>ID</td>");
        sb.append("  <td colspan='5'>" + item.getRefId() + "</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("  <td class='label' width='20%'>Status</td>");
        sb.append("  <td>" + item.getStatusValue() + "</td>");
        sb.append("  <td class='label'>Logged By</td>");
        sb.append("  <td>" + item.getLoggedBy().getName() + "</td>");
        sb.append("  <td class='label'>Assigned To</td>");
        sb.append("  <td width='15%'>" + ( item.getAssignedTo() == null ? "" : item.getAssignedTo().getName() ) + "</td>");
        sb.append("</tr>");
        sb.append("<tr class='alt'>");
        sb.append("  <td class='label'>Summary</td>");
        sb.append("  <td colspan='5'>" + HtmlUtils.htmlEscape(item.getSummary()) + "</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("  <td class='label' valign='top'>Detail</td>");
        sb.append("  <td colspan='5'>" + fixWhiteSpace(item.getDetail()) + "</td>");
        sb.append("</tr>");
        
        int row = 0;
        Map<Field.Name, Field> fields = item.getSpace().getMetadata().getFields();
        for(Field.Name fieldName : item.getSpace().getMetadata().getFieldOrder()) {
            Field field = fields.get(fieldName);
            sb.append("<tr" + ( row % 2 == 0 ? " class='alt'" : "" ) + ">");
            sb.append("  <td class='label'>" + field.getLabel() + "</td>");
            sb.append("  <td colspan='5'>" + item.getCustomValue(fieldName) + "</td></tr>");
            sb.append("</tr>");
            row ++;
        }
        
        sb.append("</table>");
        sb.append("&nbsp;<b>History</b>");
        sb.append("<table width='100%' class='jtrac'>");
        sb.append("<tr>");
        sb.append("  <th>Logged By</th><th>Status</th><th>Assigned To</th><th>Comment</th><th>Time Stamp</th>");
        List<Field> editable = item.getSpace().getMetadata().getEditableFields();
        for(Field field : editable) {
            sb.append("<th>" + field.getLabel() + "</th>");
        }
        sb.append("</tr>");
        
        if (item.getHistory() != null) {
            row = 1;
            for(History history : item.getHistory()) {
                sb.append("<tr valign='top'" + ( row % 2 == 0 ? " class='alt'" : "" ) + ">");
                sb.append("  <td>" + history.getLoggedBy().getName() + "</td>");
                sb.append("  <td>" + history.getStatusValue() +"</td>");
                sb.append("  <td>" + ( history.getAssignedTo() == null ? "" : history.getAssignedTo().getName() ) + "</td>");
                sb.append("  <td>");
                Attachment attachment = history.getAttachment();
                if (attachment != null) {
                    if (request != null && response != null) {
                        String href = response.encodeURL(request.getContextPath() + "/app/attachments/" + attachment.getFileName() +"?filePrefix=" + attachment.getFilePrefix());
                        sb.append("<a target='_blank' href='" + href + "'>" + attachment.getFileName() + "</a>&nbsp;");
                    } else {
                        sb.append("(attachment:&nbsp" + attachment.getFileName() + ")&nbsp;");
                    }
                }
                sb.append(fixWhiteSpace(history.getComment()));
                sb.append("  </td>");
                sb.append("  <td>" + history.getTimeStamp() + "</td>");
                for(Field field : editable) {
                    sb.append("<td>" + history.getCustomValue(field.getName()) + "</td>");
                }
                sb.append("</tr>");
                row++;
            }
        }
        sb.append("</table>");
        return sb.toString();
    }
    
}
