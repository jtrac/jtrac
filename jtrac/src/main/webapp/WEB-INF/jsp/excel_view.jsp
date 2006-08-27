<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Preview Excel File</span>

<p/>

<form method="post" action="<c:url value='flow.htm'/>">
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>    
    <input type="submit" name="_eventId_cancel" value="Cancel"/>
</form>

<table class="jtrac">
    <tr>
        <c:forEach items="${excelFile.labels}" var="label">
            <th>${label}</th>
        </c:forEach>
    </tr>
    <c:forEach items="${excelFile.cells}" var="rowData" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${TODO == 1}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>
        <tr ${rowClass}>
            <c:forEach items="${rowData}" var="cell">
                <td>${cell}</td>
            </c:forEach>
        </tr>
    </c:forEach>
</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
