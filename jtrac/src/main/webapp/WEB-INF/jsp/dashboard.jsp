<%@ include file="/WEB-INF/jsp/header.jsp" %>

<table class="jtrac">
    
    <tr>
        <th>Space</th>
        <c:if test="${principal.id != 0}">
            <th>Role</th>
            <th>Create<br/>New Item</th>
            <th>Logged<br/>By Me</th>
            <th>Assigned<br/>To Me</th>
        </c:if>
        <th>Open</th>
        <th>Closed</th>
        <th>Total</th>
        <th>Search</th>
    </tr>

    <c:set var="spaceCount" value="0"/>
    
    <c:forEach items="${principal.userSpaceRoles}" var="userSpaceRole">
        <c:if test="${!empty userSpaceRole.space && userSpaceRole.roleKey != 'ROLE_ADMIN'}">
            <c:set var="spaceId" value="${userSpaceRole.space.id}"/>
            <c:set var="count" value="${counts.counts[spaceId]}"/>
            <tr><td>&nbsp;</td></tr>
            <tr class="nav-table">
                <td>${userSpaceRole.space.name}</td>
                <c:if test="${principal.id != 0}">
                    <td>${userSpaceRole.roleKey}</td>
                    <td><a href="<c:url value='/flow/item?spaceId=${spaceId}'/>">(new)</a></td>
                    <td align="right"><a href="<c:url value='/flow/item_search?type=loggedBy&spaceId=${spaceId}'/>">${count.loggedBy}</a></td>
                    <td align="right"><a href="<c:url value='/flow/item_search?type=assignedTo&spaceId=${spaceId}'/>">${count.assignedTo}</a></td>
                </c:if>                
                <td align="right"><a href="<c:url value='/flow/item_search?type=open&spaceId=${spaceId}'/>">${count.open}</a></td>
                <td align="right"><a href="<c:url value='/flow/item_search?type=closed&spaceId=${spaceId}'/>">${count.closed}</a></td>
                <td align="right"><a href="<c:url value='/flow/item_search?type=total&spaceId=${spaceId}'/>">${count.total}</a></td>
                <td><a href="<c:url value='/flow/item_search?spaceId=${spaceId}'/>">(search)</a></td>
            </tr>
            <c:set var="spaceCount" value="${spaceCount + 1}"/>
        </c:if>
    </c:forEach>
    
    <tr><td>&nbsp;</td></tr>
    
    <c:if test="${spaceCount > 1}">
        <tr class="nav-table">
            <th colspan="3"/>         
            <td align="right"><a href="<c:url value='/flow/item_search?type=loggedBy'/>">${counts.loggedBy}</a></td>
            <td align="right"><a href="<c:url value='/flow/item_search?type=assignedTo'/>">${counts.assignedTo}</a></td>
            <td align="right"><a href="<c:url value='/flow/item_search?type=open'/>">${counts.open}</a></td>
            <td align="right"><a href="<c:url value='/flow/item_search?type=closed'/>">${counts.closed}</a></td>
            <td align="right"><a href="<c:url value='/flow/item_search?type=total'/>">${counts.total}</a></td>
            <td><a href="<c:url value='/flow/item_search'/>">(search)</a></td>
        </tr>
    </c:if>    
    
</table>    

<c:if test="${spaceCount == 0}">
    <span class="info">You are not mapped to any Spaces yet.</span>
</c:if>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
