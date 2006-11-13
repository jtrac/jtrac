<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script type="text/javascript"> 

function doAjaxRequest() {    
    var status = $F('status');
    if (status == null || !status) return;
    var params = 'spaceId=${item.space.id}&toState=' + status; 
    var assignedTo = $F('assignedTo');
    if (assignedTo != null && assignedTo) {
        params += '&assignedTo=' + assignedTo;
    }
    Element.show('spinner');
    new Ajax.Request('${pageContext.request.contextPath}/app/ajax/item_view_users.htm', 
        { method: 'get', parameters: params, onComplete: handleAjaxResponse }
    );    
}

function handleAjaxResponse(ajaxRequest) {
    Element.hide('spinner');       
    Element.update($('assignedToParent'), ajaxRequest.responseText);    
}

</script>

<form method="post" action="<c:url value='/flow'/>" enctype="multipart/form-data">

<table class="jtrac" width="100%">
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
            <input type="submit" name="_eventId_relateSubmit" value="Submit" <c:if test="${item.id == relatingItem.id}">disabled='true'</c:if>/>
        </td>
    </c:if>
    <authz:authorize ifAllGranted="ROLE_ADMIN">
        <td align="right">
            <a href="<c:url value='/flow?_flowExecutionKey=${flowExecutionKey}&_eventId=edit&itemId=${item.id}'/>">(edit)</a>
        </td>
    </authz:authorize>
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
            <spring:bind path="itemViewForm.history.${field.name.text}">
                <%@ include file="/WEB-INF/jsp/item_form_include.jsp" %>               
            </spring:bind>
        </tr>        
    </c:forEach>    
    
    <c:if test="${transitionCount > 0}">
        <tr>
            <td class="label">New Status</td>
            <td>
                <spring:bind path="itemViewForm.history.status">
                    <select name="${status.expression}" id="status" onChange="doAjaxRequest()">
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
                <span id="assignedToParent">
                    <spring:bind path="itemViewForm.history.assignedTo">
                        <select name="${status.expression}" id="assignedTo">
                            <c:if test="${!empty usersAbleToTransitionFrom}">
                                <option/>
                                <c:forEach items="${usersAbleToTransitionFrom}" var="usr">
                                    <option value="${usr.user.id}" <c:if test='${status.value == usr.user.id}'>selected="true"</c:if>>${usr.user.name}</option>
                                </c:forEach>                         
                            </c:if>
                        </select>                       
                        <span class="error">${status.errorMessage}</span>
                    </spring:bind>
                </span>
                <span id="spinner" style="display:none">
                    <img src="${pageContext.request.contextPath}/resources/spinner.gif"/>
                </span>              
            </td>        
        </tr>
    </c:if>
    
     <tr>
        <td class="label">
            Comment
            <font color="red">*</font>
        </td>
        <td>
            <spring:bind path="itemViewForm.history.comment">
                <textarea name="${status.expression}" rows="6" cols="70">${status.value}</textarea>
                <div class="error">${status.errorMessage}</div>
            </spring:bind>
        </td>        
    </tr>
     <tr>
        <td/>
        <td>
            <input type="submit" name="_eventId_submit" value="Submit"/>
            <spring:bind path="itemViewForm.history.sendNotifications">
                <input type="checkbox" name="${status.expression}" value="true" <c:if test="${status.value}">checked="true"</c:if>/>
                <input type="hidden" name="_${status.expression}"/> 
            </spring:bind>              
            send e-mail notifications
            <c:if test="${empty itemViewForm.relatedItemRefId}">            
                | <a href="<c:url value='/flow?_flowExecutionKey=${flowExecutionKey}&_eventId=relateSearch&itemId=${item.id}'/>">(add related item)</a>
            </c:if>
            <c:if test="${!empty itemViewForm.relatedItemRefId}">
                | <span class="info">${relationText} ${itemViewForm.relatedItemRefId}</span>
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
                <spring:bind path="itemViewForm.history.itemUsers">
                    <jtrac:multiselect name="${status.expression}" list="${userSpaceRoles}" selected="${status.value}"/>
                </spring:bind>
            </td>
        </tr>
        <tr><th>Attachment</th></tr>
        <tr><td><input type="file" name="file" size="15"/></td></tr>
    </table>
</td>

</tr>

</table>

<spring:bind path="itemViewForm.relationType">
    <input type="hidden" name="${status.expression}" value="${status.value}"/>
</spring:bind>

<spring:bind path="itemViewForm.relatedItemRefId">
    <input type="hidden" name="${status.expression}" value="${status.value}"/>
</spring:bind>

<input type="hidden" name="itemId" value="${item.id}"/>

</c:if>

<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>