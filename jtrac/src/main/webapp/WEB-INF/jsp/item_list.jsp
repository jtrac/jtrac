<%@ include file="/WEB-INF/jsp/header.jsp" %>

<p/>

<table class="jtrac">

<tr>    
    <th>ID</th>
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
        <td>
            <a href="<c:url value='flow.htm'>
                <c:param name='_flowId' value='itemView'/>
                <c:param name='itemId' value='${item.id}'/>                
                </c:url>">${item.refId}</a>            
        </td>
        <c:forEach items="${itemSearch.columns}" var="columnEntry">
            <td>${item[columnEntry.key]}</td>
        </c:forEach>          
    </tr>
</c:forEach>    

</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>