<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Spaces allocated to User: ${user.loginName} (${user.name})</span>

<p/>

<form method="post" action="<c:url value='flow.htm'/>">

<table class="jtrac">

    <tr><th>Space</th><th>Role</th><th>Remove</th></tr>
    
    <c:forEach items="${user.spaceRoles}" var="spaceRole" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedSpaceId == spaceRole.space.id}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>    

        <tr>
            <td>${spaceRole.space.prefixCode}</td>
            <td>${spaceRole.roleKey}</td>
            <td align="center">
                <a href="<c:url value='flow.htm'>
                    <c:param name='userId' value='${user.id}'/>
                    <c:param name='deAllocate' value='${spaceRole.space.id}'/></c:url>">(X)</a>                
            </td>
        </tr>

    </c:forEach>        

</table>

<p/>

<span class="info">Choose a Space to allocate to this user.</span>

<p/>

<select name="spaceId">
    <c:forEach items="${spaces}" var="space">
        <option value="${space.id}">${space.prefixCode}</option>
    </c:forEach>
</select>

<input type="submit" name="_eventId_next" value="Next"/>

<p/>

<input type="submit" name="_eventId_cancel" value="Cancel"/>

<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>