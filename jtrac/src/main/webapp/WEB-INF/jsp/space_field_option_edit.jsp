<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="heading">
    <fmt:message key='space_field_option_edit.editOption'/> : '${fieldForm.field.label}'
    <input type="submit" name="_eventId_delete" value="<fmt:message key='delete'/>"/>
</div>

<form method="post" action="<c:url value='/flow'/>">

    <input name="option" value="${option}"/>
    <input type="submit" name="_eventId_submit" value="<fmt:message key='submit'/>"/>
    
    <spring:bind path="fieldForm">
        <span class="error">
            <c:forEach items="${status.errorMessages}" var="error">
                ${error}<br/>
            </c:forEach>
        </span>
    </spring:bind>    
    
    <input type="hidden" name="optionKey" value="${optionKey}"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    
    <p/>
    
    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
    
</form>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>