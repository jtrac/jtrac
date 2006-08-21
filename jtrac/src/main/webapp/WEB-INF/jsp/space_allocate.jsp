<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script>
function setDeallocate(userId, roleKey) {
    document.spaceAllocateForm.deallocateUserId.value = userId;
    document.spaceAllocateForm.deallocateRoleKey.value = roleKey;
}
</script>

<form name="spaceAllocateForm" method="post" action="<c:url value='flow.htm'/>">

<span class="info">Users allocated to Space: ${space.prefixCode}</span>

<p/>

<table class="jtrac">

    <tr><th>Login Name</th><th>Full Name</th><th>Role</th><th>Remove</th></tr>
    
    <c:forEach items="${userRoles}" var="userRole" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedUserId == userRole.user.id}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>    

        <tr ${rowClass}>
            <td>${userRole.user.loginName}</td>
            <td>${userRole.user.name}</td>
            <td>${userRole.roleKey}</td>
            <td align="center">
                <input type="submit" name="_eventId_deallocate" value="X" 
                    onClick="setDeallocate('${userRole.user.id}', '${userRole.roleKey}')"/>
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
            <input type="submit" name="_eventId_userCreate" value="Create New User"/>
        </td>
    </tr>
    <tr>
        <th>Role</th>
        <td>
            <select name="roleKey">
                <c:forEach items="${space.metadata.roleList}" var="role">
                    <option>${role.name}</option>
                </c:forEach>
            </select>
            <input type="submit" name="_eventId_allocate" value="Allocate"/>
            <input type="checkbox" name="admin" value="true"/>
            Also add as Admin for this space.            
        </td>        
    </tr>

</table>

<p/>

<input type="submit" name="_eventId_cancel" value="Cancel"/>
<input type="hidden" name="deallocateUserId"/>
<input type="hidden" name="deallocateRoleKey"/>
<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>