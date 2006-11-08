<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script type="text/javascript">       

var currentSpaceId;    
    
function doCall(spaceId) {
    currentSpaceId = spaceId;
    Element.hide('tr_' + spaceId);
    new Ajax.Request('${pageContext.request.contextPath}/app/ajax/dashboard.htm', 
        { method: 'get', parameters: 'spaceId=' + spaceId, onComplete: showResponse }
    ); 
}

function showResponse(ajaxRequest) {
    new Insertion.Top('tbody_' + currentSpaceId, ajaxRequest.responseText);
}       

</script>

<c:choose>
    <c:when test="${principal.spaceCount == 0}">
        <span class="info">You are not mapped to any Spaces.</span>
        <br/>
    </c:when>
    <c:otherwise>
    
<table class="jtrac">
    
    <tr class="nav-width">        
        <th colspan="3"/>
        <th>Status</th>
        <c:if test="${principal.id != 0}">            
            <th>Logged<br/>By Me</th>
            <th>Assigned<br/>To Me</th>
        </c:if>
        <th>All</th>
    </tr>
        
    <c:set var="spaceCount" value="0"/>

    <c:forEach items="${principal.userSpaceRoles}" var="userSpaceRole">
        <c:if test="${!empty userSpaceRole.space && userSpaceRole.roleKey != 'ROLE_ADMIN'}">
            <c:set var="spaceId" value="${userSpaceRole.space.id}"/>
            <c:set var="counts" value="${countsHolder.counts[spaceId]}"/>
            <tr><td>&nbsp;</td></tr>
            <tbody id="tbody_${spaceId}">
                <tr class="nav-table" id="tr_${spaceId}">
                    <td id="hide_${spaceId}">${userSpaceRole.space.name}</td>                    
                    <td><a href="<c:url value='/flow/item?spaceId=${spaceId}'/>">(new)</a></td>
                    <td><a href="<c:url value='/flow/item_search?spaceId=${spaceId}'/>">(search)</a></td>
                    <td><a href="#" onclick="doCall(${spaceId})">(+)</a></td>
                    <c:if test="${principal.id != 0}">
                        <td><a href="<c:url value='/flow/item_search?type=loggedBy&spaceId=${spaceId}'/>">${counts.loggedByMe}</a></td>
                        <td><a href="<c:url value='/flow/item_search?type=assignedTo&spaceId=${spaceId}'/>">${counts.assignedToMe}</a></td>
                    </c:if>
                    <td><a href="<c:url value='/flow/item_search?type=total&spaceId=${spaceId}'/>">${counts.total}</a></td>
                </tr>
            </tbody>
            <c:set var="spaceCount" value="${spaceCount + 1}"/>
        </c:if>
    </c:forEach>    

   <tr><td>&nbsp;</td></tr>

    <c:if test="${spaceCount > 1}">
        <tr class="nav-table">
            <td colspan="3"><a href="<c:url value='/flow/item_search'/>">(search)</a></td>
            <th/>                        
            <td><a href="<c:url value='/flow/item_search?type=loggedBy'/>">${countsHolder.totalLoggedByMe}</a></td>
            <td><a href="<c:url value='/flow/item_search?type=assignedTo'/>">${countsHolder.totalAssignedToMe}</a></td>
            <td><a href="<c:url value='/flow/item_search?type=total'/>">${countsHolder.totalTotal}</a></td>
        </tr>
    </c:if>        
    
</table>

    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
