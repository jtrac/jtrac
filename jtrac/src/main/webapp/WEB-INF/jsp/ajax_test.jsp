<%@ page session="true" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<c:set var="principal" value="${ACEGI_SECURITY_CONTEXT.authentication.principal}"/>
<c:forEach items="${states}" var="stateEntry" varStatus="row">
    <c:set var="stateId" value="${stateEntry.key}"/>
    <tr class="nav-table" valign="top">
        <c:if test="${row.count == 1}">
            <td rowSpan="${stateCount}">${space.name}</td>                   
            <td rowSpan="${stateCount}"><a href="<c:url value='/flow/item?spaceId=${space.id}'/>">(new)</a></td>
        </c:if>              
        <td>${states[stateId]}</td>          
        <c:if test="${principal.id != 0}">            
            <td><a href="<c:url value='/flow/item_search?type=loggedBy&spaceId=${space.id}'/>">${counts.loggedByMeMap[stateId]}</a></td>
            <td><a href="<c:url value='/flow/item_search?type=assignedTo&spaceId=${space.id}'/>">${counts.assignedToMeMap[stateId]}</a></td>
        </c:if>
        <td><a href="<c:url value='/flow/item_search?type=total&spaceId=${space.id}'/>">${counts.totalMap[stateId]}</a></td>
        <c:if test="${row.count == 1}">
            <td rowSpan="${stateCount}"><a href="<c:url value='/flow/item_search?spaceId=${space.id}'/>">(search)</a></td>
        </c:if>
    </tr>       
</c:forEach>   
