<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Space Preview</span>

<br/>

<textarea rows="25" cols="80">${space.metadata.prettyXml}</textarea>

<form method="post" action="<c:url value='flow.htm'/>">
    <input type="submit" name="_eventId_back" value="Back"/>
    <input type="submit" name="_eventId_save" value="Save"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>