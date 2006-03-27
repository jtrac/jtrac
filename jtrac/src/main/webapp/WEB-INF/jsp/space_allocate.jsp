<%@ include file="/WEB-INF/jsp/header.jsp" %>

<form method="post" action="<c:url value='flow.htm'/>">

<span class="info">Users allocated to Space: ${space.prefixCode}</span>

<input type="submit" name="_eventId_userCreate" value="Create New User"/>

<p/>

<table class="jtrac">

    <tr><th>Login Name (Name)</th><th>Role</th><th>Remove</th></tr>
    
    <c:forEach items="${userRoles}" var="userRole" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedUserId == userRole.user.id}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>    

        <tr>
            <td>${userRole.user.loginName} (${userRole.user.name})</td>
            <td>${userRole.roleKey}</td>
            <td align="center">
                <a href="<c:url value='flow.htm'>
                    <c:param name='_eventId' value='deallocate'/>
                    <c:param name='_flowExecutionKey' value='${flowExecutionKey}'/>
                    <c:param name='userId' value='${userRole.user.id}'/></c:url>">(X)</a>                
            </td>
        </tr>

    </c:forEach>
    
</table>

<p/>

<span class="info">Choose User and Role to allocate:</span>


<table class="jtrac">
        
    <tr>
        <th>User</th>
        <td>
            <select name="userId">
                <c:forEach items="${unallocatedUsers}" var="user">
                    <option value="${user.id}">${user.loginName} (${user.name})</option>
                </c:forEach>
            </select>        
        </td>
    </tr>
    <tr>
        <th>Role</th>
        <td>
            <select name="roleKey">
                <c:forEach items="${space.metadata.roleSet}" var="role">
                    <option>${role.name}</option>
                </c:forEach>
            </select>
            <input type="submit" name="_eventId_allocate" value="Allocate"/>
        </td>        
    </tr>

</table>

<p/>

<input type="submit" name="_eventId_cancel" value="Cancel"/>

<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>