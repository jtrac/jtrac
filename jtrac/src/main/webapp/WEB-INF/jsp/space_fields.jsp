<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script>
function setFieldName(fieldName) {
    document.spaceFieldsForm.fieldName.value = fieldName;
}
</script>

<span class="info">Custom Fields for Space: ${space.name} (${space.prefixCode})</span>

<p/>

<c:set var="fields" value="${space.metadata.fields}"/>

<form name="spaceFieldsForm" method="post" action="<c:url value='/flow'/>">

<table class="jtrac">
    <tr>
        <th>Move</th>
        <th>Internal Name</th>
        <th>Type</th>
        <th>Optional</th>
        <th>Label</th>
        <th>Option List</th>
        <th/>
    </tr>
    <c:forEach items="${space.metadata.fieldOrder}" var="fieldName" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedFieldName == fieldName.text}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>           
        <tr ${rowClass}>
            <td>
                <input type="submit" name="_eventId_up" value="/\" onClick="setFieldName('${fieldName.text}')"/>
                <input type="submit" name="_eventId_down" value="\/" onClick="setFieldName('${fieldName.text}')"/>
            </td>
            <c:set var="field" value="${fields[fieldName]}"/>
            <td>${field.name.text}</td>
            <td>${field.name.description}</td>
            <td><c:if test="${field.optional}">true</c:if></td>
            <td>${field.label}</td>
            <td>
                <c:forEach items="${field.options}" var="entry">
                    ${entry.value}<br/>
                </c:forEach>
            </td>
            <td><input type="submit" name="_eventId_edit" value="Edit" onClick="setFieldName('${fieldName.text}')"/></td>
        </tr>
    </c:forEach>
</table>

<p/>

<span class="info">Choose type of custom field to add:</span>

<select name="fieldType">
    <c:forEach items="${space.metadata.availableFieldTypes}" var="entry">
        <option value="${entry.key}">${entry.value}</option>
    </c:forEach>
</select>
<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
<input type="submit" name="_eventId_add" value="Add Field"/>    
<p/>
<input type="submit" name="_eventId_back" value="Back"/>
<input type="submit" name="_eventId_next" value="Next"/>
<input type="hidden" name="fieldName"/>
<p/>
<input type="submit" name="_eventId_cancel" value="Cancel"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>