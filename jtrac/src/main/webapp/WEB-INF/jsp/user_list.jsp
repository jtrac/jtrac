<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Users and Allocated Trackers</span>

<a href="<c:url value='webflow.htm'><c:param name='_flowId' value='userCreate-flow'/></c:url>">Create New User</a>

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
                <a href="<c:url value='user_edit.htm'><c:param name='userId' value='${user.id}'/></c:url>">${user.loginName}</a>
            </td>
            <td>
                <c:if test="${user.locked}">locked</c:if>
            </td>					
            <td>						
                <c:forEach items="${spaceRoles}" var="entry">
                    <a href="<c:url value='space_allocate.htm'><c:param name='trackerId' value='${entry.key.id}'/></c:url>">
                        ${entry.key.prefixCode}
                    </a>&nbsp;(${entry.value.name})&nbsp;
                </c:forEach>
            </td>
            <td align="center">
                <a href="<c:url value='user_allocate.htm'><c:param name='userId' value='${user.id}'/></c:url>">(+)</a>
            </td>
        </tr>
    </c:forEach>

</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>