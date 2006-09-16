<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Workflow State</span>

<br/><br/>

<form method="post" action="<c:url value='/flow'/>">

    <input name="state" value="${state}"/>
    <input type="submit" name="_eventId_submit" value="Submit"/>
    
    <spring:bind path="space">
        <span class="error">
            <c:forEach items="${status.errorMessages}" var="error">
                <c:out value="${error}"/><br/>
            </c:forEach>
        </span>
    </spring:bind>    
    
    <input type="hidden" name="stateKey" value="${stateKey}"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    
    <p/>
    
    <input type="submit" name="_eventId_cancel" value="Cancel"/>
    
</form>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>