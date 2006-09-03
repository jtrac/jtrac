<%@ include file="/WEB-INF/jsp/header.jsp" %>

<p/>

<table class="jtrac">
    
    <tr>
        <th>Space</th>
        <th>Role</th>
        <th colspan="2">Action</th>
        <th>Logged<br/>By Me</th>
        <th>Assigned<br/>To Me</th>
        <th>Open</th>
        <th>Closed</th>
        <th>Total</th>
    </tr>

    <c:set var="spaceCount" value="0"/>
    
    <c:forEach items="${principal.userSpaceRoles}" var="userSpaceRole">
        <c:if test="${!empty userSpaceRole.space && userSpaceRole.roleKey != 'ROLE_ADMIN'}">
            <c:set var="spaceId" value="${userSpaceRole.space.id}"/>
            <tr><td>&nbsp;</td></tr>
            <tr class="nav-table">
                <td>${userSpaceRole.space.prefixCode}</td>
                <td>${userSpaceRole.roleKey}</td>
                <td>
                    <a href="<c:url value='flow.htm'>
                        <c:param name='_flowId' value='item'/>
                        <c:param name='spaceId' value='${spaceId}'/>
                        </c:url>">NEW</a>                
                </td>
                <td>
                    <a href="<c:url value='flow.htm'>
                        <c:param name='_flowId' value='itemSearch'/>
                        <c:param name='spaceId' value='${spaceId}'/>
                        </c:url>">SEARCH</a>               
                </td>
                <c:set var="count" value="${counts.counts[spaceId]}"/>
                <td align="right"><a href="<c:url value='flow.htm?_flowId=itemSearch&type=loggedBy&spaceId=${spaceId}'/>">${count.loggedBy}</a></td>
                <td align="right"><a href="<c:url value='flow.htm?_flowId=itemSearch&type=assignedTo&spaceId=${spaceId}'/>">${count.assignedTo}</a></td>
                <td align="right"><a href="<c:url value='flow.htm?_flowId=itemSearch&type=open&spaceId=${spaceId}'/>">${count.open}</a></td>
                <td align="right"><a href="<c:url value='flow.htm?_flowId=itemSearch&type=closed&spaceId=${spaceId}'/>">${count.closed}</a></td>
                <td align="right"><a href="<c:url value='flow.htm?_flowId=itemSearch&type=total&spaceId=${spaceId}'/>">${count.total}</a></td>                
            </tr>
            <c:set var="spaceCount" value="${spaceCount + 1}"/>
        </c:if>
    </c:forEach>
    
    <tr><td>&nbsp;</td></tr>
    
    <c:if test="${spaceCount > 1}">
        <tr>
            <td colspan="2"/>
            <td colspan="2" align="center" class="nav-table"><a href="<c:url value='flow.htm?_flowId=itemSearch'/>">SEARCH ALL</a></td>           
            <td align="right" class="nav-table"><a href="<c:url value='flow.htm?_flowId=itemSearch&type=loggedBy'/>">${counts.loggedBy}</a></td>
            <td align="right" class="nav-table"><a href="<c:url value='flow.htm?_flowId=itemSearch&type=assignedTo'/>">${counts.assignedTo}</a></td>
            <td align="right" class="nav-table"><a href="<c:url value='flow.htm?_flowId=itemSearch&type=open'/>">${counts.open}</a></td>
            <td align="right" class="nav-table"><a href="<c:url value='flow.htm?_flowId=itemSearch&type=closed'/>">${counts.closed}</a></td>
            <td align="right" class="nav-table"><a href="<c:url value='flow.htm?_flowId=itemSearch&type=total'/>">${counts.total}</a></td>    
        </tr>
    </c:if>    
    
</table>    

<c:if test="${spaceCount == 0}">
    <p><span class="info">You are not mapped to any Spaces yet.</span></p>
</c:if>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
