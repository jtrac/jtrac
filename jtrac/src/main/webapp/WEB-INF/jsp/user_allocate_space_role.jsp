<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Allocate Role for User: ${user.loginName} within Space: ${space.prefixCode}</span>

<form method="post" action="<c:url value='webflow.htm'/>">

    <select name="roleKey">
        <c:forEach items="${space.metadata.roles}" var="roleMapEntry">
            <option>${roleMapEntry.key}</option>
        </c:forEach>
    </select>
    
    <input type="submit" name="_eventId_save" value="Save"/>

    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>