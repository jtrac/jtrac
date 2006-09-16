<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Workflow Role</span>

<p/>

<form method="post" action="<c:url value='/flow'/>">

    <input name="roleKey" value="${roleKey}"/>
    <input type="submit" name="_eventId_submit" value="Submit"/>
    
    <spring:bind path="space">
        <span class="error">
            <c:forEach items="${status.errorMessages}" var="error">
                <c:out value="${error}"/><br/>
            </c:forEach>
        </span>
    </spring:bind>    
    
    <input type="hidden" name="oldRoleKey" value="${oldRoleKey}"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    
    <p/>
    
    <input type="submit" name="_eventId_cancel" value="Cancel"/>
    
</form>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>