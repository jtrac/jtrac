<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Upload Excel File</span>

<p/>

<form method="post" action="<c:url value='/flow'/>" enctype="multipart/form-data">
    <input type="file" name="file"/>
    <input type="submit" name="_eventId_submit" value="Submit"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>    
    <p/>    
    <input type="submit" name="_eventId_cancel" value="Cancel"/>
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
