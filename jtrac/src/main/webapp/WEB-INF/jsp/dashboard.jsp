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
            <tr id="tr_1"></tr>
            <tr class="nav-table">
                <td>${userSpaceRole.space.name}</td>
                <c:if test="${principal.id != 0}">                    
                    <td><a href="<c:url value='/flow/item?spaceId=${spaceId}'/>">(new)</a></td>
                    <td align="right"><a href="<c:url value='/flow/item_search?type=loggedBy&spaceId=${spaceId}'/>">${count.loggedBy}</a></td>
                    <td align="right"><a href="<c:url value='/flow/item_search?type=assignedTo&spaceId=${spaceId}'/>">${count.assignedTo}</a></td>
                </c:if>
                <td align="right"><a href="<c:url value='/flow/item_search?type=total&spaceId=${spaceId}'/>">${count.total}</a></td>
                <td><a href="<c:url value='/flow/item_search?spaceId=${spaceId}'/>">(search)</a></td>
            </tr>
            <c:set var="spaceCount" value="${spaceCount + 1}"/>
        </c:if>
    </c:forEach>    

    <tr><td style="height:1em"/></tr>

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
    
function initProgress() {
    Element.show('progressMsg');
}

function resetProgress() {
    if ($('removeMe')) Element.remove('removeMe');
    td = Builder.node('td', {id:'removeMe'}, $('testDiv').innerHTML);
    // td.innerHTML = $('testDiv').innerHTML);
    // new Insertion.Top('tr_1', td);
    $('tr_1').appendChild(td);
    Element.hide('progressMsg');
    // Effect.Fade('progressMsg');
}        

</script>

<div id="testDiv"></div>

<button id="clicky">Click Me!</button>

<span id="progressMsg" style="display:none"><img src="${pageContext.request.contextPath}/resources/indicator.gif" /> Loading...</span>

<ajax:htmlContent
  baseUrl="${pageContext.request.contextPath}/app/ajax/test.htm" 
  source="clicky"
  target="testDiv"
  parameters="" 
  preFunction="initProgress"
  postFunction="resetProgress" />

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
