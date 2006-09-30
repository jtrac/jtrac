<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Allocate Role for User: ${user.loginName} within Space: ${space.name} (${space.prefixCode})</span>

<p/>

<form method="post" action="<c:url value='/flow'/>">

    <select name="roleKey">
        <c:forEach items="${space.metadata.roles}" var="roleMapEntry">
            <option>${roleMapEntry.key}</option>
        </c:forEach>
    </select>   
    
    <input type="submit" name="_eventId_allocate" value="Allocate"/>
    <%-- switch on in future version
    <input type="checkbox" name="admin" value="true"/>
    Also add as Admin for this space.
    --%>
    <p/>
    
    <input type="submit" name="_eventId_cancel" value="Cancel"/>

    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>