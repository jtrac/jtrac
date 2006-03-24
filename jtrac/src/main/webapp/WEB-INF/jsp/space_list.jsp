<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Spaces</span>

<a href="<c:url value='webflow.htm'><c:param name='_flowId' value='spaceCreate-flow'/></c:url>">Create New Space</a>

<p/>

<table class="jtrac">

    <tr><th>Space Key</th><th>Users</th></tr>

    <c:forEach items="${spaces}" var="space" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedSpaceId == space.id}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>
        <tr ${rowClass}>
            <td>
                <a href="<c:url value='webflow.htm'>
                    <c:param name='_flowId' value='spaceCreate-flow'/>
                    <c:param name='spaceId' value='${space.id}'/>
                    </c:url>">${space.prefixCode}</a>
            </td>
            <td align="center">
                <a href="<c:url value='webflow.htm'>
                    <c:param name='_flowId' value='spaceAllocate-flow'/>
                    <c:param name='spaceId' value='${space.id}'/>
                    </c:url>">(+)</a>
            </td>
        </tr>
    </c:forEach>

</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>