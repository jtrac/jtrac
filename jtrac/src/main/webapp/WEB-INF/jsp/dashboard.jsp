<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script type="text/javascript">       

var currentSpaceId;    
    
function doCall(spaceId) {
    if ($('tbody_detail_' + spaceId).innerHTML) {
        Element.hide('tbody_' + spaceId);
        Element.show('tbody_detail_' + spaceId);
    } else {
        Element.show('spinner_' + spaceId);
        currentSpaceId = spaceId;
        new Ajax.Request('${pageContext.request.contextPath}/app/ajax/dashboard.htm', 
            { method: 'get', parameters: 'spaceId=' + spaceId, onComplete: showResponse }
        );
    }
}

function showResponse(ajaxRequest) {
    Element.hide('tbody_' + currentSpaceId);
    Element.hide('spinner_' + currentSpaceId);
    new Insertion.Top('tbody_detail_' + currentSpaceId, ajaxRequest.responseText);
}       

function collapse(spaceId) {
    Element.show('tbody_' + spaceId);    
    Element.hide('tbody_detail_' + spaceId);
}

</script>

<c:choose>
    <c:when test="${principal.spaceCount == 0}">
        <span class="info">You are not mapped to any Spaces.</span>
        <br/>
    </c:when>
    <c:otherwise>
    
<table class="jtrac">
    <tr>
        <th>Space</th>
        <th colspan="2">Action</th>
        <th colspan="2">Status</th>
        <c:if test="${principal.id != 0}">            
            <th>Logged<br/>By Me</th>
            <th>Assigned<br/>To Me</th>
        </c:if>
        <th style="width:4.5em">All</th>
    </tr>
    <tr><td>&nbsp;</td></tr>
        
    <c:set var="spaceCount" value="0"/>

    <c:forEach items="${principal.userSpaceRoles}" var="userSpaceRole">
        <c:if test="${!empty userSpaceRole.space && userSpaceRole.roleKey != 'ROLE_ADMIN'}">
            <c:set var="spaceId" value="${userSpaceRole.space.id}"/>
            <c:set var="counts" value="${countsHolder.counts[spaceId]}"/>
            <tbody id="tbody_${spaceId}">
                <tr class="nav-table">
                    <td class="shrink">${userSpaceRole.space.name}</td>                    
                    <td class="shrink"><a href="<c:url value='/flow/item?spaceId=${spaceId}'/>">(new)</a></td>
                    <td class="shrink"><a href="<c:url value='/flow/item_search?spaceId=${spaceId}'/>">(search)</a></td>
                    <td class="shrink"><a href="#" onclick="doCall(${spaceId})">(+)</a></td>
                    <td><span id="spinner_${spaceId}" style="display:none"><img src="${pageContext.request.contextPath}/resources/spinner.gif"/></span></td>
                    <c:if test="${principal.id != 0}">
                        <td><a href="<c:url value='/flow/item_search?type=loggedBy&spaceId=${spaceId}'/>">${counts.loggedByMe}</a></td>
                        <td><a href="<c:url value='/flow/item_search?type=assignedTo&spaceId=${spaceId}'/>">${counts.assignedToMe}</a></td>
                    </c:if>
                    <td><a href="<c:url value='/flow/item_search?type=total&spaceId=${spaceId}'/>">${counts.total}</a></td>
                </tr>
                <tr><td>&nbsp;</td></tr>
            </tbody>
            <tbody id="tbody_detail_${spaceId}"></tbody>
            <c:set var="spaceCount" value="${spaceCount + 1}"/>
        </c:if>
    </c:forEach>    

    <c:if test="${spaceCount > 1}">
        
        <tr class="nav-table">
            <th colspan="2"/>
            <td class="shrink"><a href="<c:url value='/flow/item_search'/>">(search)</a></td>
            <th colspan="2"/>                        
            <td><a href="<c:url value='/flow/item_search?type=loggedBy'/>">${countsHolder.totalLoggedByMe}</a></td>
            <td><a href="<c:url value='/flow/item_search?type=assignedTo'/>">${countsHolder.totalAssignedToMe}</a></td>
            <td class="selected"><a href="<c:url value='/flow/item_search?type=total'/>">${countsHolder.totalTotal}</a></td>
        </tr>
    </c:if>        
    
</table>

    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
