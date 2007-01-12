<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script>
function setFieldName(fieldName) {
    document.spaceFieldsForm.fieldName.value = fieldName;
}
</script>

<div class="heading"><fmt:message key='space_fields.customFields'/>: ${space.name} (${space.prefixCode})</div>

<c:set var="fields" value="${space.metadata.fields}"/>

<form name="spaceFieldsForm" method="post" action="<c:url value='/flow'/>">

<table class="jtrac">
    <tr>
        <th><fmt:message key='space_fields.move'/></th>
        <th><fmt:message key='space_fields.internalName'/></th>
        <th><fmt:message key='space_fields.type'/></th>
        <th><fmt:message key='space_fields.optional'/></th>
        <th><fmt:message key='space_fields.label'/></th>
        <th><fmt:message key='space_fields.optionList'/></th>
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
            <td><input type="submit" name="_eventId_edit" value="<fmt:message key='edit'/>" onClick="setFieldName('${fieldName.text}')"/></td>
        </tr>
    </c:forEach>
</table>

<p/>

<span class="info"><fmt:message key='space_fields.chooseType'/>:</span>

<select name="fieldType">
    <c:forEach items="${space.metadata.availableFieldTypes}" var="entry">
        <option value="${entry.key}">
            <fmt:message key='space_fields.type_${entry.key}'/> -
            <fmt:message key='space_fields.typeRemaining'>
                <fmt:param value='${entry.value}'/>
            </fmt:message>
        </option>
    </c:forEach>
</select>
<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
<input type="submit" name="_eventId_add" value="<fmt:message key='space_fields.addField'/>"/>    
<p/>
<input type="submit" name="_eventId_back" value="<fmt:message key='back'/>"/>
<input type="submit" name="_eventId_next" value="<fmt:message key='next'/>"/>
<input type="hidden" name="fieldName"/>
<p/>
<input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>