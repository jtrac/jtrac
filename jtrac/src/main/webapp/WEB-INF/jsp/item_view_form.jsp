<%@ include file="/WEB-INF/jsp/header.jsp" %>

<form method="post" action="<c:url value='/flow'/>" enctype="multipart/form-data">

<table class="jtrac">
<tr>
    <td>
        <c:if test="${!empty calledBySearch || !empty calledByRelate}">            
            <a href="<c:url value='/flow?_flowExecutionKey=${flowExecutionKey}&_eventId=back&itemId=${item.id}#goto'/>">(back)</a>        
        </c:if>
    </td>
    <c:if test="${!empty calledByRelate}">
        <td class="selected">
           Relate this item to ${relatingItem.refId} [${relatingItem.summary}]
        </td>
        <td>            
            <select name="relationType">                
                <option value="1">${relatingItem.refId} is duplicate of this</option>
                <option value="2">${relatingItem.refId} depends on this</option>
                <option value="0">Both items are related</option>
            </select>
            <input type="hidden" name="itemId" value="${relatingItem.id}"/>
            <input type="hidden" name="relatedItemRefId" value="${item.refId}"/>
            <input type="submit" name="_eventId_relateSubmit" value="Submit"/>
        </td>
    </c:if>    
</tr>
</table>

<br/>

<jtrac:itemview item="${item}"/>

<c:if test="${principal.id > 0 && empty calledByRelate}">

<br/>

<table class="bdr-collapse" width="100%">

<tr>
<td>    
    
<table class="jtrac">
        
    <c:forEach items="${editableFields}" var="field">
        <tr>
            <td class="label">
                ${field.label}            
            </td>
            <spring:bind path="history.${field.name}">
                <%@ include file="/WEB-INF/jsp/item_form_include.jsp" %>               
            </spring:bind>
        </tr>        
    </c:forEach>    
    
    <tr>
        <td class="label">New Status</td>
        <td>
            <spring:bind path="history.status">
                <select name="${status.expression}">
                    <option/>
                    <c:forEach items="${transitions}" var="transitionEntry">
                        <option value="${transitionEntry.key}" <c:if test='${transitionEntry.key == status.value}'>selected="true"</c:if>>${transitionEntry.value}</option>
                    </c:forEach>
                </select>
                <span class="error">${status.errorMessage}</span>
            </spring:bind>
        </td>        
    </tr>
    <tr>
        <td class="label">Assign To</td>       
        <td>
            <spring:bind path="history.assignedTo">
                <select name="${status.expression}">
                    <option/>
                    <c:forEach items="${users}" var="user">
                        <option value="${user.id}" <c:if test='${user.id == status.value}'>selected="true"</c:if>>${user.name}</option>
                    </c:forEach>  
                </select>
                <span class="error">${status.errorMessage}</span>
            </spring:bind> 
        </td>        
    </tr>
     <tr>
        <td class="label">
            Comment
            <font color="red">*</font></td>
        <td>
            <spring:bind path="history.comment">
                <textarea name="${status.expression}" rows="6" cols="70">${status.value}</textarea>
                <div class="error">${status.errorMessage}</div>
            </spring:bind>
        </td>        
    </tr>
     <tr>
        <td/>
        <td>
            <input type="submit" name="_eventId_submit" value="Submit"/>
            <input type="checkbox" name="sendNotifications" value="true" <c:if test="${history.sendNotifications}">checked="true"</c:if>/>
            <input type="hidden" name="_sendNotifications"/>               
            send e-mail notifications
            <c:if test="${empty relatedItemRefId}">            
                | <a href="<c:url value='/flow?_flowExecutionKey=${flowExecutionKey}&_eventId=relateSearch&itemId=${item.id}'/>">(add related item)</a>
            </c:if>
            <c:if test="${!empty relatedItemRefId}">
                | <span class="info">${relationText} ${relatedItemRefId}</span>
                <a href="<c:url value='/flow?_flowExecutionKey=${flowExecutionKey}&_eventId=relateSearch&itemId=${item.id}'/>">(change)</a>
            </c:if>
        </td>        
    </tr>  
</table>

</td>

<td valign="top">
    <table class="jtrac">
        <tr><th>Notify By E-mail</th></tr>
        <tr>
            <td>
                <spring:bind path="history.itemUsers">
                    <jtrac:multiselect name="${status.expression}" list="${users}" selected="${status.value}"/>
                </spring:bind>
            </td>
        </tr>
        <tr><th>Attachment</th></tr>
        <tr><td><input type="file" name="file" size="15"/></td></tr>
    </table>
</td>

</tr>

</table>

<input type="hidden" name="relationType" value="${relationType}"/>
<input type="hidden" name="relatedItemRefId" value="${relatedItemRefId}"/>
<input type="hidden" name="itemId" value="${item.id}"/>

</c:if>

<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>