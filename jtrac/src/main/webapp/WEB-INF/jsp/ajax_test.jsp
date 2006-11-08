<%@ page session="true" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<c:set var="principal" value="${ACEGI_SECURITY_CONTEXT.authentication.principal}"/>
<c:set var="spaceId" value="${space.id}"/>
<c:set var="count" value="${counts.counts[spaceId]}"/>
<tr class="nav-table" >
    <td>${space.name}</td>
    <c:if test="${principal.id != 0}">                    
        <td><a href="<c:url value='/flow/item?spaceId=${spaceId}'/>">(new)</a></td>
        <td><a href="#" onclick="doCall()">(+)</a></td>
        <td><a href="<c:url value='/flow/item_search?type=loggedBy&spaceId=${spaceId}'/>">${count.loggedBy}</a></td>
        <td><a href="<c:url value='/flow/item_search?type=assignedTo&spaceId=${spaceId}'/>">${count.assignedTo}</a></td>
    </c:if>
    <td><a href="<c:url value='/flow/item_search?type=total&spaceId=${spaceId}'/>">${count.total}</a></td>
    <td><a href="<c:url value='/flow/item_search?spaceId=${spaceId}'/>">(search)</a></td>
</tr>
