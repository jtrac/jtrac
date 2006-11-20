<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="heading">
    <fmt:message key='space_list.spaceList'/>    
    <a href="<c:url value='/flow/space'/>">[ <fmt:message key='space_list.createNewSpace'/> ]</a>    
</div>

<table class="jtrac">

    <tr>
        <th><fmt:message key='space_list.key'/></th>
        <th><fmt:message key='space_list.name'/></th>
        <th><fmt:message key='space_list.edit'/></th>
        <th><fmt:message key='space_list.description'/></th>
        <th><fmt:message key='space_list.users'/></th>
    </tr>

    <c:forEach items="${spaces}" var="space" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedSpaceId == space.id}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>
        <tr ${rowClass}>
            <td>${space.prefixCode}</td>
            <td>${space.name}</td>
            <td>
                <a href="<c:url value='/flow/space?spaceId=${space.id}'/>">(<fmt:message key='edit'/>)</a>
            </td>
            <td>${space.description}</td>
            <td align="center">
                <a href="<c:url value='/flow/space_allocate?spaceId=${space.id}'/>">(+)</a>
            </td>
        </tr>
    </c:forEach>

</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>