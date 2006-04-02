<%@ include file="/WEB-INF/jsp/header.jsp" %>

<p/>

<jtrac:itemview item="${item}"/>

<p/>

<form method="post" action="<c:url value='flow.htm'/>">

<table class="jtrac">
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
                    <c:forEach items="${userRoles}" var="userRole">
                        <c:set var="user" value="${userRole.user}"/>
                        <option value="${user.id}" <c:if test='${user.id == status.value}'>selected="true"</c:if>>${user.name}</option>
                    </c:forEach>  
                </select>
                <span class="error">${status.errorMessage}</span>
            </spring:bind> 
        </td>        
    </tr>
     <tr>
        <td class="label">Comment</td>
        <td>
            <textarea name="comment" rows="6" cols="70">${history.comment}</textarea>
        </td>        
    </tr>
     <tr>
        <td/>
        <td>
            <input type="submit" name="_eventId_submit" value="Submit"/>
        </td>        
    </tr>  
</table>

<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>