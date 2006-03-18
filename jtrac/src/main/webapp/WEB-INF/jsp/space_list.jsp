<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Spaces</span>

<table class="jtrac">

    <tr><th>Space Key</th></tr>

    <c:forEach items="${spaces}" var="space" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedSpaceId == space.id}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>
        <tr ${rowClass}>
            <td>
                <a href="<c:url value='space_edit.htm'><c:param name='spaceId' value='${space.id}'/></c:url>">${space.prefixCode}</a>
            </td>
        </tr>
    </c:forEach>

</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>