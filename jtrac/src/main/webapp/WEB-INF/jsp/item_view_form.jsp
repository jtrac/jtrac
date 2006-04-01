<%@ include file="/WEB-INF/jsp/header.jsp" %>

<p/>

<jtrac:itemview item="${item}"/>

<p/>

<form method="post" action="<c:url value='flow.htm'/>">

<table class="jtrac">
    <tr>
        <td class="label">New Status</td>
        <td>
            <select name="status">
                <option/>
                <c:forEach items="${transitions}" var="transitionEntry">
                    <option value="${transitionEntry.key}">${transitionEntry.value}</option>
                </c:forEach>
            </select>
        </td>        
    </tr>
    <tr>
        <td class="label">Assign To</td>
        <td>
            <select name="assignedTo">
                <option/>
                <c:forEach items="${userRoles}" var="userRole">
                    <c:set var="user" value="${userRole.user}"/>
                    <option value="${user.id}">${user.name}</option>
                </c:forEach>   
            </select>
        </td>        
    </tr>
     <tr>
        <td class="label">Comment</td>
        <td>
            <textarea name="comment" rows="6" cols="70"></textarea>
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