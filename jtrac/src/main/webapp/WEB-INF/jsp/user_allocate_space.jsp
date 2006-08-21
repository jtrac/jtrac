<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script>
function setDeallocate(spaceId) {
    document.userAllocateForm.deallocate.value = spaceId;
}
</script>

<span class="info">Spaces allocated to User: ${user.loginName} (${user.name})</span>

<p/>

<form name="userAllocateForm" method="post" action="<c:url value='flow.htm'/>">

<table class="jtrac">

    <tr><th>Space</th><th>Role</th><th>Remove</th></tr>
    
    <c:forEach items="${user.spaceRoles}" var="spaceRole" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedSpaceId == spaceRole.space.id}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>    

        <tr ${rowClass}>
            <td>${spaceRole.space.prefixCode}</td>
            <td>${spaceRole.roleKey}</td>
            <td align="center">
               <input type="submit" name="_eventId_deallocate" value="X" onClick="setDeallocate('${spaceRole.id}')"/>
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

<p/>

<input type="submit" name="_eventId_cancel" value="Cancel"/>
<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
<input type="hidden" name="deallocate"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>