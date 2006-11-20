<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="heading"><fmt:message key='space_role_delete.confirm'/> : '${roleKey}'</div>

<form method="post" action="<c:url value='/flow'/>">

    <p><fmt:message key='space_role_delete.line1'><fmt:param value="${space.name}"/></fmt:message></p>
    <p><fmt:message key='space_role_delete.line2'/></p>    
    <span class="error"><fmt:message key='space_role_delete.line3'/></span>
    <input type="submit" name="_eventId_submit" value="<fmt:message key='submit'/>"/>
    
    <p/>
        
    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    <input type="hidden" name="oldRoleKey" value="${oldRoleKey}"/>
    <input type="hidden" name="roleKey" value="${roleKey}"/>
    
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>