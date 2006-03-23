<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Allocate Space to User: ${user.loginName} (${user.name})</span>

<p/>

<form method="post" action="<c:url value='webflow.htm'/>">

<table class="jtrac">

    <tr><th>Space</th><th>Role</th><th/></tr>
    
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
            <td>
                <a href="<c:url value='webflow.htm'>
                    <c:param name='userId' value='${user.id}'/>
                    <c:param name='deAllocate' value='${spaceRole.space.id}'/></c:url>">
                    (X)
                </a>                
            </td>
        </tr>

    </c:forEach>
        
    <tr>
        <td>
            <select name="spaceId">
                <c:forEach items="${spaces}" var="space">
                    <option value="${space.id}">${space.prefixCode}</option>
                </c:forEach>
            </select>
        </td>
        <td>
            <input type="submit" name="_eventId_next" value="Next"/>
        </td>
    </tr>

</table>

<input type="submit" name="_eventId_cancel" value="Cancel"/>

<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>