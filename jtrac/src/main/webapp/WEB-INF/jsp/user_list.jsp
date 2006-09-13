<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Users and Allocated Trackers</span>

<a href="<c:url value='/flow/user'/>">Create New User</a>

<p/>

<table class="jtrac">

    <tr><th>User Name</th><th>Edit Profile</th><th>Locked</th><th>Tracker (Role)</th><th>Assign</br>Tracker</th></tr>

    <c:forEach items="${users}" var="user" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedUserId == user.id}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>
        <tr ${rowClass}>
            <td>${user.name}</td>
            <td>
                <a href="<c:url value='/flow/user?userId=${user.id}'/>">${user.loginName}</a>
            </td>
            <td>
                <c:if test="${user.locked}">locked</c:if>
            </td>                   
            <td>        
                <c:forEach items="${user.userSpaceRoles}" var="userSpaceRole" varStatus="row">
                    <a href="<c:url value='/flow/space_allocate?spaceId=${userSpaceRole.space.id}'/>">
                        ${userSpaceRole.space.prefixCode}
                    </a>
                    (<i>${userSpaceRole.roleKey}</i>)           
                </c:forEach>
            </td>
            <td align="center">
                <a href="<c:url value='/flow/user_allocate?userId=${user.id}'/>">(+)</a>
            </td>
        </tr>
    </c:forEach>

</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>