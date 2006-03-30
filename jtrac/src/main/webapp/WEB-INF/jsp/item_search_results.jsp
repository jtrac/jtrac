<%@ include file="/WEB-INF/jsp/header.jsp" %>

<p/>

<table class="jtrac">

<tr>    
<c:forEach items="${itemSearch.columns}" var="columnEntry">
    <th>${columnEntry.value}</th>
</c:forEach>    
</tr>

<c:forEach items="${items}" var="item" varStatus="row">
    <c:set var="rowClass">
        <c:choose>
            <c:when test="${selectedItemId == item.id}">class="selected"</c:when>
            <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
        </c:choose>            
    </c:set>    
    <tr ${rowClass}>
        <c:forEach items="${itemSearch.columns}" var="columnEntry">
            <td>${item[columnEntry.key]}</td>
        </c:forEach>          
    </tr>
</c:forEach>    

</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>