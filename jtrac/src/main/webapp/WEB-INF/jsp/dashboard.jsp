<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script type="text/javascript">       

var currentSpaceId;    
    
function doAjaxRequest(spaceId) {
    if ($('tbody_detail_' + spaceId).innerHTML) {
        Element.hide('tbody_' + spaceId);
        Element.show('tbody_detail_' + spaceId);
    } else {
        Element.show('spinner_' + spaceId);
        currentSpaceId = spaceId;
        new Ajax.Request('${pageContext.request.contextPath}/app/ajax/dashboard.htm', 
            { method: 'get', parameters: 'spaceId=' + spaceId, onComplete: handleAjaxResponse }
        );
    }
}

function handleAjaxResponse(ajaxRequest) {
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
        <span class="info"><fmt:message key="dashboard.noSpaces"/></span>
        <br/>
    </c:when>
    <c:otherwise>
    
<table class="jtrac">
    <tr>
        <th><fmt:message key="dashboard.space"/></th>
        <th colspan="2"><fmt:message key="dashboard.action"/></th>
        <th colspan="2"><fmt:message key="dashboard.status"/></th>
        <c:if test="${principal.id != 0}">            
            <th><fmt:message key="dashboard.loggedByMe"/></th>
            <th><fmt:message key="dashboard.assignedToMe"/></th>
        </c:if>
        <th style="width:4.5em"><fmt:message key="dashboard.all"/></th>
    </tr>
    <tr><td>&nbsp;</td></tr>
        
    <c:set var="spaceCount" value="0"/>

    <c:forEach items="${principal.spaceRoles}" var="userSpaceRole">        
        <c:set var="spaceId" value="${userSpaceRole.space.id}"/>
        <c:set var="counts" value="${countsHolder.counts[spaceId]}"/>
        <tbody id="tbody_${spaceId}">
            <tr class="nav-table">
                <th>${userSpaceRole.space.name}</th>                    
                <td class="icon">
                    <c:if test="${principal.spacesWhereAbleToCreateNewItem[spaceId]}">
                        <a href="<c:url value='/flow/item?spaceId=${spaceId}'/>">
                            <img title="<fmt:message key='dashboard.new'/>" class="noborder" src="${pageContext.request.contextPath}/resources/document-new.png"/>
                        </a>
                    </c:if>
                </td>
                <td class="icon">
                    <a href="<c:url value='/flow/item_search?spaceId=${spaceId}'/>">
                        <img title="<fmt:message key='dashboard.search'/>" class="noborder" src="${pageContext.request.contextPath}/resources/system-search.png"/>
                    </a>
                </td>
                <td style="padding:0">
                    <a href="#" onclick="doAjaxRequest(${spaceId})">
                        <img title="<fmt:message key='dashboard.showDetails'/>" class="noborder" src="${pageContext.request.contextPath}/resources/collapsed.png"/>
                    </a>
                </td>
                <td>
                    <span id="spinner_${spaceId}" style="display:none">
                        <img src="${pageContext.request.contextPath}/resources/spinner.gif"/>
                    </span>
                </td>
                <c:if test="${principal.id != 0}">
                    <td><a href="<c:url value='/flow/item_search?type=loggedBy&spaceId=${spaceId}'/>">${counts.loggedByMe}</a></td>
                    <td><a href="<c:url value='/flow/item_search?type=assignedTo&spaceId=${spaceId}'/>">${counts.assignedToMe}</a></td>
                </c:if>
                <td class="selected"><a href="<c:url value='/flow/item_search?type=total&spaceId=${spaceId}'/>">${counts.total}</a></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
        </tbody>
        <tbody id="tbody_detail_${spaceId}"></tbody>
        <c:set var="spaceCount" value="${spaceCount + 1}"/>        
    </c:forEach>    

    <c:if test="${spaceCount > 1}">
        
        <tr class="nav-table">
            <th colspan="2"/>
            <td class="icon">
                <a href="<c:url value='/flow/item_search'/>">
                    <img title="Search" class="noborder" src="${pageContext.request.contextPath}/resources/system-search.png"/>
                </a>
            </td>
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
