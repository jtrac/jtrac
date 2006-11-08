<%@ page session="true" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<c:set var="principal" value="${ACEGI_SECURITY_CONTEXT.authentication.principal}"/>
<c:forEach items="${space.metadata.states}" var="stateEntry">
    <c:set var="stateId" value="${stateEntry.key}"/>
    <tr class="nav-table" >
        <td>${space.name}</td>
        <c:if test="${principal.id != 0}">                    
            <td></td>
            <td>${space.metadata.states[stateId]}</td>
            <td><a href="<c:url value='/flow/item_search?type=loggedBy&spaceId=${space.id}'/>">${counts.loggedByMeMap[stateId]}</a></td>
            <td><a href="<c:url value='/flow/item_search?type=assignedTo&spaceId=${space.id}'/>">${counts.assignedToMeMap[stateId]}</a></td>
        </c:if>
        <td><a href="<c:url value='/flow/item_search?type=total&spaceId=${space.id}'/>">${counts.totalMap[stateId]}</a></td>
        <td></td>
    </tr>
</c:forEach>   
