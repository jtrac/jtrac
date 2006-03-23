<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Custom Fields for Space: ${space.prefixCode}</span>

<p/>

<table class="jtrac">
    <tr>
        <th>Internal Name</th>
        <th>Type</th>
        <th>Optional</th>
        <th>Label</th>
        <th>Option List</th>
    </tr>
    <c:forEach items="${space.metadata.fieldSet}" var="field" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedFieldName == field.name}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>        
        <c:set var='editUrl'>
            <c:url value="webflow.htm">
                <c:param name='fieldName' value='${field.name}'/>
                <c:param name='_eventId' value='edit'/>
                <c:param name='_flowExecutionId' value='${flowExecutionId}'/>
            </c:url>        
        </c:set>            
        <tr ${rowClass}>
            <td><a href="${editUrl}">${field.name}</a></td>
            <td>${field.name.description}</td>
            <td><c:if test="${field.optional}">true</c:if></td>
            <td>${field.label}</td>
            <td>
                <c:forEach items="${field.options}" var="entry">
                    ${entry.value}<br/>
                </c:forEach>
            </td>
        </tr>
    </c:forEach>
</table>

<p/>

<form method="post" action="<c:url value='webflow.htm'/>">
    <select name="fieldType">
        <c:forEach items="${space.metadata.availableFieldTypes}" var="entry">
            <option value="${entry.key}">${entry.value}</option>
        </c:forEach>
    </select>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
    <input type="submit" name="_eventId_add" value="Add Field"/>    
    <p/>
    <input type="submit" name="_eventId_back" value="Back"/>
    <input type="submit" name="_eventId_next" value="Next"/>
</form>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>