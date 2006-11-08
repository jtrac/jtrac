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
            <c:set var="count" value="${counts.counts[spaceId]}"/>
            <tbody id="tr_1">
                <tr class="nav-table" >
                    <td>${userSpaceRole.space.name}</td>
                    <c:if test="${principal.id != 0}">                    
                        <td><a href="<c:url value='/flow/item?spaceId=${spaceId}'/>">(new)</a></td>
                        <td align="right"><a href="<c:url value='/flow/item_search?type=loggedBy&spaceId=${spaceId}'/>">${count.loggedBy}</a></td>
                        <td align="right"><a href="<c:url value='/flow/item_search?type=assignedTo&spaceId=${spaceId}'/>">${count.assignedTo}</a></td>
                    </c:if>
                    <td align="right"><a href="<c:url value='/flow/item_search?type=total&spaceId=${spaceId}'/>">${count.total}</a></td>
                    <td><a href="<c:url value='/flow/item_search?spaceId=${spaceId}'/>">(search)</a></td>
                </tr>
            </tbody>
            <c:set var="spaceCount" value="${spaceCount + 1}"/>
        </c:if>
    </c:forEach>    

   <tbody id="tr_1_alt"/>

    <c:if test="${spaceCount > 1}">
        <tr class="nav-table">
            <th colspan="2"/>         
            <td align="right"><a href="<c:url value='/flow/item_search?type=loggedBy'/>">${counts.loggedBy}</a></td>
            <td align="right"><a href="<c:url value='/flow/item_search?type=assignedTo'/>">${counts.assignedTo}</a></td>
            <td align="right"><a href="<c:url value='/flow/item_search?type=total'/>">${counts.total}</a></td>
            <td><a href="<c:url value='/flow/item_search'/>">(search)</a></td>
        </tr>
    </c:if>        
    
</table>

    </c:otherwise>
</c:choose>

<script type="text/javascript">       

function doCall() {
    new Ajax.Request('${pageContext.request.contextPath}/app/ajax/test.htm', { method: 'get', onComplete: showResponse }); 
}

function showResponse(ajaxRequest) {
    new Insertion.Top('tr_1', ajaxRequest.responseText);
}

function initProgress() {
    Element.show('progressMsg');
}

function resetProgress() {
    alert($('testDiv').innerHTML);
    // new Insertion.Top('tr_1', html);
    Element.hide('progressMsg');
    // Effect.Fade('progressMsg');
       
}        

</script>

<button id="clicky" onClick="doCall()">Click Me!</button>

<span id="progressMsg" style="display:none"><img src="${pageContext.request.contextPath}/resources/indicator.gif" /> Loading...</span>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
