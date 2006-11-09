 <%@ page session="true" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<c:set var="principal" value="${ACEGI_SECURITY_CONTEXT.authentication.principal}"/>
<c:forEach items="${states}" var="stateEntry" varStatus="row">
    <c:set var="stateId" value="${stateEntry.key}"/>
    <tr class="nav-table" valign="middle">
        <c:if test="${row.count == 1}">
            <th rowSpan="${stateCount + 1}">${space.name}</th>                   
            <td rowSpan="${stateCount + 1}" class="icon">
                <a href="<c:url value='/flow/item?spaceId=${space.id}'/>">
                    <img title="New" class="noborder" src="${pageContext.request.contextPath}/resources/document-new.png"/>
                </a>                
            </td>
            <td rowSpan="${stateCount + 1}" class="icon">
                <a href="<c:url value='/flow/item_search?spaceId=${space.id}'/>">
                    <img title="Search" class="noborder" src="${pageContext.request.contextPath}/resources/system-search.png"/>
                </a>
            </td>
            <td rowSpan="${stateCount + 1}" valign="top" align="center" style="padding:0">
                <a href="#" onclick="collapse(${space.id})">
                    <img title="Hide Details" class="noborder" src="${pageContext.request.contextPath}/resources/expanded.png"/>
                </a>
            </td>
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
