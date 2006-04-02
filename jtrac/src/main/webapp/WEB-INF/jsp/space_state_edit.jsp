<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Rename Workflow State Name</span>

<p/>

<c:set var="fields" value="${space.metadata.fields}"/>

<form method="post" action="<c:url value='flow.htm'/>">

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