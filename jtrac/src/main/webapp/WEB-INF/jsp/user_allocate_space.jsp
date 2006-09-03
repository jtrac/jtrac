<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script>
function setDeallocate(userSpaceRoleId) {
    document.userAllocateForm.deallocate.value = userSpaceRoleId;
}
</script>

<span class="info">Spaces allocated to User: ${user.loginName} (${user.name})</span>

<p/>

<form name="userAllocateForm" method="post" action="<c:url value='flow.htm'/>">

<table class="jtrac">

    <tr><th>Space</th><th>Role</th><th>Remove</th></tr>
    
    <c:forEach items="${user.userSpaceRoles}" var="userSpaceRole" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedSpaceId == userSpaceRole.space.id}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>    

        <tr ${rowClass}>
            <td>${userSpaceRole.space.prefixCode}</td>
            <td>${userSpaceRole.roleKey}</td>
            <td align="center">
                <c:if test="${userSpaceRole.roleKey == 'ROLE_ADMIN' && empty userSpaceRole.space}">
                    <c:set var="isAdmin" value="true"/>
                    <c:if test="${user.id != 1}">
                        <input type="submit" name="_eventId_deallocate" value="X" onClick="setDeallocate('${userSpaceRole.id}')"/>
                    </c:if>
                </c:if>
            </td>
        </tr>
        
    </c:forEach>        

</table>

<p/>

<span class="info">Choose a Space to allocate to this user.</span>

<p/>

<select name="spaceId">
    <c:forEach items="${unallocatedSpaces}" var="space">
        <option value="${space.id}">${space.prefixCode}</option>
    </c:forEach>
</select>

<input type="submit" name="_eventId_next" value="Next"/>

<c:if test="${!isAdmin}">

<p/>

<span class="info">Make this user an Administrator (for all spaces)</span>
<input type="submit" name="_eventId_makeAdmin" value="Make Admin"/>

</c:if>

<p/>

<input type="submit" name="_eventId_cancel" value="Cancel"/>
<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
<input type="hidden" name="deallocate"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>