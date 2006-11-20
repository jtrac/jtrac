<%@ include file="/WEB-INF/jsp/header.jsp" %>

<form method="post" action="<c:url value='/flow'/>">

<div class="heading">
    <fmt:message key='space_role_form.editRoleName'/>
    <input type="submit" name="_eventId_delete" value="<fmt:message key='delete'/>" <c:if test="${space.metadata.roleCount <= 1}">disabled='true'</c:if>/>
</div>

    <input name="roleKey" value="${roleKey}"/>
    <input type="submit" name="_eventId_submit" value="<fmt:message key='submit'/>"/>
    
    <spring:bind path="space">
        <span class="error">
            <c:forEach items="${status.errorMessages}" var="error">
                ${error}<br/>
            </c:forEach>
        </span>
    </spring:bind>    
    
    <input type="hidden" name="oldRoleKey" value="${oldRoleKey}"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    
    <p/>
    
    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
    
</form>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>