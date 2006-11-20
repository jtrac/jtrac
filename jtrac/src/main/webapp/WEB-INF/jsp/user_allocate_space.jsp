<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script>
function setDeallocate(userSpaceRoleId) {
    document.userAllocateForm.deallocate.value = userSpaceRoleId;
}
</script>

<div class="heading"><fmt:message key='user_allocate_space.spacesAllocated'/>: ${user.loginName} (${user.name})</div>

<form name="userAllocateForm" method="post" action="<c:url value='/flow'/>">

<table class="jtrac">

    <tr>
        <th><fmt:message key='user_allocate_space.space'/></th>
        <th><fmt:message key='user_allocate_space.role'/></th>
        <th><fmt:message key='user_allocate_space.remove'/></th>
    </tr>
    
    <c:forEach items="${user.userSpaceRoles}" var="userSpaceRole" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedSpaceId == userSpaceRole.space.id}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>    

        <tr ${rowClass}>
            <td>${userSpaceRole.space.name}</td>
            <td>${userSpaceRole.roleKey}</td>
            <td align="center">
                <c:if test="${!(userSpaceRole.roleKey == 'ROLE_ADMIN' && empty userSpaceRole.space && user.id == 1)}">
                    <input type="submit" name="_eventId_deallocate" value="X" onClick="setDeallocate('${userSpaceRole.id}')"/>                  
                </c:if>             
            </td>
        </tr>
        
    </c:forEach>        

</table>

<p/>

<div class="heading"><fmt:message key='user_allocate_space.chooseSpace'/></div>

<select name="spaceId">
    <c:forEach items="${unallocatedSpaces}" var="space">
        <option value="${space.id}">${space.name}</option>
    </c:forEach>
</select>

<input type="submit" name="_eventId_next" value="<fmt:message key='next'/>"/>

<c:if test="${!user.adminForAllSpaces}">

<p/>

<span class="info"><fmt:message key='user_allocate_space.makeUserAdmin'/></span>
<input type="submit" name="_eventId_makeAdmin" value="<fmt:message key='user_allocate_space.makeAdmin'/>"/>

</c:if>

<p/>

<input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
<input type="hidden" name="deallocate"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>