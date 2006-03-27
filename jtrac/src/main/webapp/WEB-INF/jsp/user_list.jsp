<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Users and Allocated Trackers</span>

<a href="<c:url value='flow.htm'><c:param name='_flowId' value='user'/></c:url>">Create New User</a>

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
                <a href="<c:url value='flow.htm'>
                    <c:param name='_flowId' value='user'/>
                    <c:param name='userId' value='${user.id}'/>
                    </c:url>">${user.loginName}</a>
            </td>
            <td>
                <c:if test="${user.locked}">locked</c:if>
            </td>					
            <td>        
                <c:forEach items="${user.spaceRoles}" var="spaceRole" varStatus="row">
                    <a href="<c:url value='flow.htm'>
                        <c:param name='_flowId' value='spaceAllocate'/>
                        <c:param name='spaceId' value='${spaceRole.space.id}'/>
                        </c:url>">${spaceRole.space.prefixCode}</a>
                    (<i>${spaceRole.roleKey}</i>)           
                </c:forEach>
            </td>
            <td align="center">
                <a href="<c:url value='flow.htm'>
                    <c:param name='_flowId' value='userAllocate'/>
                    <c:param name='userId' value='${user.id}'/>
                    </c:url>">(+)</a>
            </td>
        </tr>
    </c:forEach>

</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>