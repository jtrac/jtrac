<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Edit Option for field '${fieldForm.field.label}'</span>

<p/>

<form method="post" action="<c:url value='/flow'/>">

    <input name="option" value="${option}"/>
    <input type="submit" name="_eventId_submit" value="Submit"/>
    <input type="submit" name="_eventId_delete" value="Delete This Option"/>
    
    <spring:bind path="fieldForm">
        <span class="error">
            <c:forEach items="${status.errorMessages}" var="error">
                <c:out value="${error}"/><br/>
            </c:forEach>
        </span>
    </spring:bind>    
    
    <input type="hidden" name="optionKey" value="${optionKey}"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    
    <p/>
    
    <input type="submit" name="_eventId_cancel" value="Cancel"/>
    
</form>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>