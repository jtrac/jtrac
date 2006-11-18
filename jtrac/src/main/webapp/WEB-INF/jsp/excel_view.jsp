<%@ include file="/WEB-INF/jsp/header.jsp" %>

<style>
    .cb { background: #ADD8E6; text-align: center; }
    .rn { text-align: right; } 
</style>

<form method="post" action="<c:url value='/flow'/>">
    
<span class="info">Preview Excel File</span>

<input type="submit" name="_eventId_cancel" value="Cancel"/>

<p/>    
    
<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
<input type="submit" name="_eventId_delete" value="Delete"/>
<input type="submit" name="_eventId_convertToDate" value="Convert to Date"/>

<br/><br/>

<table class="jtrac">
    <tr>
        <td/>
        <td/>
        <input type="hidden" name="_selCols"/>
        <input type="hidden" name="_selRows"/>
        <c:forEach items="${excelFile.columns}" var="column" varStatus="col">
            <td class="cb">
                <input type="checkbox" name="selCols" value="${col.count - 1}"/>                
            </td>
        </c:forEach>
    </tr>        
    <tr>
        <td/>
        <td/>
        <c:forEach items="${excelFile.columns}" var="column">
            <th>${column.label}</th>
        </c:forEach>
    </tr>
    <c:forEach items="${excelFile.rows}" var="rowData" varStatus="row">       
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${TODO == 1}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>
        <tr ${rowClass}>
            <td class="cb">
                <input type="checkbox" name="selRows" value="${row.count - 1}"/>
            </td>            
            <th class="rn">
                ${row.count}
            </th>          
            <c:forEach items="${rowData}" var="cell">
                <td>${cell}</td>
            </c:forEach>
        </tr>
    </c:forEach>
</table>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
