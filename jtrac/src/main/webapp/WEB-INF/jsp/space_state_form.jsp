<%@ include file="/WEB-INF/jsp/header.jsp" %>

<form method="post" action="<c:url value='/flow'/>">

<div class="heading">    
    <fmt:message key='space_state_form.editState'/>
    <c:if test="${!empty stateKey}"><input type="submit" name="_eventId_delete" value="<fmt:message key='delete'/>" <c:if test="${stateKey == 1}">disabled='true'</c:if>/></c:if>
</div>

    <input name="state" value="${state}"/>
    <input type="submit" name="_eventId_submit" value="<fmt:message key='submit'/>"/>
    
    <spring:bind path="space">
        <span class="error">
            <c:forEach items="${status.errorMessages}" var="error">
                ${error}<br/>
            </c:forEach>
        </span>
    </spring:bind>    
    
    <input type="hidden" name="stateKey" value="${stateKey}"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    
    <p/>
    
    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
    
</form>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>