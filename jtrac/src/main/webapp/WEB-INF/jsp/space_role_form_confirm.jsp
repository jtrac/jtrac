<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Confirm Role Rename : '${oldRoleKey}' to '${roleKey}'</span>

<p/>

<form method="post" action="<c:url value='/flow'/>">

    <p>Are you sure that you want to rename this role?</p>    
    <span class="error">You cannot undo database updates for this operation.</span>
    <input type="submit" name="_eventId_submit" value="Submit"/>
    
    <p/>
        
    <input type="submit" name="_eventId_cancel" value="Cancel"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    <input type="hidden" name="oldRoleKey" value="${oldRoleKey}"/>
    <input type="hidden" name="roleKey" value="${roleKey}"/>
    
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>