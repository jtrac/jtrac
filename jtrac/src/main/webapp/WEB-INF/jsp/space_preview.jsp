<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Space Preview</span>

<p/>

<c:set var="fields" value="${space.metadata.fields}"/>

<form name="spacePreviewForm" method="post" action="<c:url value='/flow'/>">

<table class="jtrac">

    <c:forEach items="${space.metadata.fieldOrder}" var="fieldName">
        <tr>
            <td><input type="submit" name="_eventId_up" value="/\"/></td>
            <td><input type="submit" name="_eventId_down" value="\/"/></td>
            <td class="label">${fields[fieldName].label}</th>        
        </tr>
    </c:forEach>
    
</table>    
    
<p/>

    <input type="submit" name="_eventId_back" value="Back"/>
    <input type="submit" name="_eventId_save" value="Save"/>
    <input type="hidden" name="fieldName"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    
</form>

<p/>

<textarea rows="10" cols="80">${space.metadata.prettyXml}</textarea>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>