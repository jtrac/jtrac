<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Confirm Option Delete : '${option}' for field '${fieldForm.field.label}'</span>

<p/>

<form method="post" action="<c:url value='flow.htm'/>">

    Are you sure that you want to delete this option?
    <input type="submit" name="_eventId_submit" value="Submit"/>
    
    <p/>
        
    <input type="submit" name="_eventId_cancel" value="Cancel"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    <input type="hidden" name="optionKey" value="${optionKey}"/>
    
</form>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>