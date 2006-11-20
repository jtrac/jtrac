<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script>
function setDeallocate(userSpaceRoleId) {    
    document.spaceAllocateForm.deallocate.value = userSpaceRoleId;
}
</script>

<form name="spaceAllocateForm" method="post" action="<c:url value='/flow'/>">

<div class="heading"><fmt:message key='space_allocate.usersAllocatedToSpace'/>: ${space.name} (${space.prefixCode})</div>

<table class="jtrac">

    <tr>
        <th><fmt:message key='space_allocate.loginName'/></th>
        <th><fmt:message key='space_allocate.fullName'/></th>
        <th><fmt:message key='space_allocate.role'/></th>
        <th><fmt:message key='space_allocate.remove'/></th>
    </tr>
    
    <c:forEach items="${userSpaceRoles}" var="userSpaceRole" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedUserId == userSpaceRole.user.id}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>    

        <tr ${rowClass}>
            <td>${userSpaceRole.user.loginName}</td>
            <td>${userSpaceRole.user.name}</td>
            <td>${userSpaceRole.roleKey}</td>
            <td align="center">
                <input type="submit" name="_eventId_deallocate" value="X" 
                    onClick="setDeallocate('${userSpaceRole.id}')"/>
            </td>
        </tr>
    </c:forEach>
    
</table>

<p/>

<div class="heading"><fmt:message key='space_allocate.allocateUser'/>:</div>

<table class="jtrac">
        
    <tr>
        <th><fmt:message key='space_allocate.user'/></th>
        <td>
            <select name="userId">
                <c:forEach items="${unallocatedUsers}" var="user">
                    <option value="${user.id}">${user.loginName} (${user.name})</option>
                </c:forEach>
            </select>
            <input type="submit" name="_eventId_userCreate" value="<fmt:message key='space_allocate.createNewUser'/>"/>
        </td>
    </tr>
    <tr>
        <th><fmt:message key='space_allocate.role'/></th>
        <td>
            <select name="roleKey">
                <c:forEach items="${space.metadata.roleList}" var="role">
                    <option>${role.name}</option>
                </c:forEach>
            </select>
            <input type="submit" name="_eventId_allocate" value="<fmt:message key='space_allocate.allocate'/>"/>
            <%-- switch on in future version
            <input type="checkbox" name="admin" value="true"/>
            Also add as Admin for this space.
            --%>
        </td>        
    </tr>

</table>

<p/>

<input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
<input type="hidden" name="deallocate"/>
<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>