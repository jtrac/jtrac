<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Upload Excel File</span>

<br/><br/>

<spring:bind path="excelFile">
    <span class="error">
        <c:forEach items="${status.errorMessages}" var="error">
            <c:out value="${error}"/><br/>
        </c:forEach>
    </span>
    <br/>
</spring:bind>

<form method="post" action="<c:url value='/flow'/>" enctype="multipart/form-data">
    <input type="file" name="file"/>
    <input type="submit" name="_eventId_submit" value="Submit"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>    
    <p/>    
    <input type="submit" name="_eventId_cancel" value="Cancel"/>
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
