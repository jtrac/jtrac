<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="heading"><fmt:message key='space_field_option_delete.confirm'/> : '${option}' for field '${fieldForm.field.label}'</div>

<form method="post" action="<c:url value='/flow'/>">

    <p><fmt:message key='space_field_option_delete.line1'/></p>
    <p><fmt:message key='space_field_option_delete.line2'><fmt:param value="${affectedCount}"/></fmt:message></p>
    <span class="error"><fmt:message key='space_field_option_delete.line3'/></span>
    <input type="submit" name="_eventId_submit" value="<fmt:message key='submit'/>"/>
    
    <p/>
        
    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    <input type="hidden" name="optionKey" value="${optionKey}"/>
    
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>