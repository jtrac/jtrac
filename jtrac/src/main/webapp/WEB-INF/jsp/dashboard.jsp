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
    
function initProgress() {
    Element.show('progressMsg');
}

var newRow = '<table><tbody><tr><td>FOO BAR</td></tr></tbody></table>';

function resetProgress() {
    // Element.hide('tr_1');
    // tr = Builder.node('tr', $('testDiv').innerHTML);
    // $('tr_1_alt').appendChild(td);
    
    $('testDiv').innerHTML = newRow;
    $('tr_1').parentNode.replaceChild($('testDiv').firstChild.firstChild, $('tr_1'));
    
    Element.hide('progressMsg');
    // Effect.Fade('progressMsg');
    
var stateCounts = eval({ 
    states: [ 'Assigned', 'Fixed', 'Closed' ], 
    atmCounts: [ 3, 4, 5 ],
    lbmCounts: [ 5, 6, 7 ],
    totCounts: [ 9, 10, 11]
});


    
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
