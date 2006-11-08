<%@ include file="/WEB-INF/jsp/header.jsp" %>

<c:choose>
    <c:when test="${principal.spaceCount == 0}">
        <span class="info">You are not mapped to any Spaces.</span>
        <br/>
    </c:when>
    <c:otherwise>
    
<table class="jtrac">
    
    <tr class="nav-width">        
        <th>Space</th>
        <c:if test="${principal.id != 0}">
            <th>New</th>
            <th/>
            <th>Logged<br/>By Me</th>
            <th>Assigned<br/>To Me</th>
        </c:if>
        <th>Total</th>
        <th>Search</th>
    </tr>
        
    <c:set var="spaceCount" value="0"/>

    <c:forEach items="${principal.userSpaceRoles}" var="userSpaceRole">
        <c:if test="${!empty userSpaceRole.space && userSpaceRole.roleKey != 'ROLE_ADMIN'}">
            <c:set var="spaceId" value="${userSpaceRole.space.id}"/>
            <c:set var="counts" value="${countsHolder.counts[spaceId]}"/>
            <tr style="height:1em"></tr>
            <tbody id="tbody_${spaceId}">
                <tr class="nav-table" >
                    <td>${userSpaceRole.space.name}</td>
                    <c:if test="${principal.id != 0}">                    
                        <td><a href="<c:url value='/flow/item?spaceId=${spaceId}'/>">(new)</a></td>
                        <td><a href="#" onclick="doCall(${spaceId})">(+)</a></td>
                        <td><a href="<c:url value='/flow/item_search?type=loggedBy&spaceId=${spaceId}'/>">${counts.loggedByMe}</a></td>
                        <td><a href="<c:url value='/flow/item_search?type=assignedTo&spaceId=${spaceId}'/>">${counts.assignedToMe}</a></td>
                    </c:if>
                    <td><a href="<c:url value='/flow/item_search?type=total&spaceId=${spaceId}'/>">${counts.total}</a></td>
                    <td><a href="<c:url value='/flow/item_search?spaceId=${spaceId}'/>">(search)</a></td>
                </tr>
            </tbody>
            <c:set var="spaceCount" value="${spaceCount + 1}"/>
        </c:if>
    </c:forEach>    

   <tr style="height:1em"></tr>

    <c:if test="${spaceCount > 1}">
        <tr class="nav-table">
            <th colspan="3"/>         
            <td><a href="<c:url value='/flow/item_search?type=loggedBy'/>">${countsHolder.totalLoggedByMe}</a></td>
            <td><a href="<c:url value='/flow/item_search?type=assignedTo'/>">${countsHolder.totalAssignedToMe}</a></td>
            <td><a href="<c:url value='/flow/item_search?type=total'/>">${countsHolder.totalTotal}</a></td>
            <td><a href="<c:url value='/flow/item_search'/>">(search)</a></td>
        </tr>
    </c:if>        
    
</table>

    </c:otherwise>
</c:choose>

<script type="text/javascript">       

var currentSpaceId;    
    
function doCall(spaceId) {
    currentSpaceId = spaceId;
    new Ajax.Request('${pageContext.request.contextPath}/app/ajax/test.htm', 
        { method: 'get', parameters: 'spaceId=' + spaceId, onComplete: showResponse }
    ); 
}

function showResponse(ajaxRequest) {
    new Insertion.Top('tbody_' + currentSpaceId, ajaxRequest.responseText);
}       

</script>

<span id="progressMsg" style="display:none"><img src="${pageContext.request.contextPath}/resources/indicator.gif" /> Loading...</span>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
