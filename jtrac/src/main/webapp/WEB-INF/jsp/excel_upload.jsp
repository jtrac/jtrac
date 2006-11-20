<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="heading"><fmt:message key='excel_upload.uploadExcelFile'/></div>

<spring:bind path="excelFile">
    <span class="error">
        <c:forEach items="${status.errorMessages}" var="error">
            ${error}<br/>
        </c:forEach>
    </span>
    <br/>
</spring:bind>

<form method="post" action="<c:url value='/flow'/>" enctype="multipart/form-data">
    <input type="file" name="file"/>
    <input type="submit" name="_eventId_submit" value="<fmt:message key='submit'/>"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>    
    <p/>    
    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
