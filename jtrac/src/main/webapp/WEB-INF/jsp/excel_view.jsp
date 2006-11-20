<%@ include file="/WEB-INF/jsp/header.jsp" %>

<style>
    .cb { background: #ADD8E6; text-align: center; }
    .rn { text-align: right; } 
</style>

<form method="post" action="<c:url value='/flow'/>">
    
<div class="heading">
    <fmt:message key='excel_view.previewImportedData'/> <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
</div>  
    
<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

<select name="action">
    <option>-- <fmt:message key='excel_view.selectActionToPerform'/> --</option>
    <option value="1"><fmt:message key='excel_view.deleteSelected'/></option>    
    <option value="2"><fmt:message key='excel_view.convertToDate'/></option>
    <option value="3"><fmt:message key='excel_view.concatenateFields'/></option>
    <option value="4"><fmt:message key='excel_view.extractFirstEighty'/></option>
</select>

<input type="submit" name="_eventId_submit" value="<fmt:message key='submit'/>"/>

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
