<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="heading"><fmt:message key='space_role_form_confirm.confirm'/> : '${oldRoleKey}' to '${roleKey}'</div>

<form method="post" action="<c:url value='/flow'/>">

    <p><fmt:message key='space_role_form_confirm.line1'/></p>    
    <span class="error"><fmt:message key='space_role_form_confirm.line2'/></span>
    <input type="submit" name="_eventId_submit" value="<fmt:message key='submit'/>"/>
    
    <p/>
        
    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    <input type="hidden" name="oldRoleKey" value="${oldRoleKey}"/>
    <input type="hidden" name="roleKey" value="${roleKey}"/>
    
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>