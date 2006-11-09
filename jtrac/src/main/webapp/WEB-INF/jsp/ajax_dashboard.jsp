 <%@ page session="true" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<c:set var="principal" value="${ACEGI_SECURITY_CONTEXT.authentication.principal}"/>
<c:forEach items="${states}" var="stateEntry" varStatus="row">
    <c:set var="stateId" value="${stateEntry.key}"/>
    <tr class="nav-table" valign="middle">
        <c:if test="${row.count == 1}">
            <th rowSpan="${stateCount + 1}">${space.name}</th>                   
            <td rowSpan="${stateCount + 1}"><a href="<c:url value='/flow/item?spaceId=${space.id}'/>">(new)</a></td>
            <td rowSpan="${stateCount + 1}"><a href="<c:url value='/flow/item_search?spaceId=${space.id}'/>">(search)</a></td>
            <td rowSpan="${stateCount + 1}" class="nostyle" valign="top"><a href="#" onclick="collapse(${space.id})">(--)</a></td>
        </c:if>
        <td>${states[stateId]}</td>          
        <c:if test="${principal.id != 0}">            
            <td><a href="<c:url value='/flow/item_search?type=loggedBy&spaceId=${space.id}&status=${stateId}'/>">${counts.loggedByMeMap[stateId]}</a></td>
            <td><a href="<c:url value='/flow/item_search?type=assignedTo&spaceId=${space.id}&status=${stateId}'/>">${counts.assignedToMeMap[stateId]}</a></td>
        </c:if>
        <td><a href="<c:url value='/flow/item_search?type=total&spaceId=${space.id}&status=${stateId}'/>">${counts.totalMap[stateId]}</a></td>
    </tr>       
</c:forEach>
<tr class="nav-table">
    <th/>
    <c:if test="${principal.id != 0}">
        <td><a href="<c:url value='/flow/item_search?type=loggedBy&spaceId=${space.id}'/>">${counts.loggedByMe}</a></td>
        <td><a href="<c:url value='/flow/item_search?type=assignedTo&spaceId=${space.id}'/>">${counts.assignedToMe}</a></td>        
    </c:if>
    <td class="selected"><a href="<c:url value='/flow/item_search?type=total&spaceId=${space.id}'/>">${counts.total}</a></td>
</tr>
<tr><td>&nbsp;</td></tr>
