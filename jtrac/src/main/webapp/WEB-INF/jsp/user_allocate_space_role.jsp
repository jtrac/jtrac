<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="heading">
    <fmt:message key='user_allocate_space_role.allocateRole'><fmt:param value="${user.loginName}"/></fmt:message>: ${space.name} (${space.prefixCode})
</div>

<form method="post" action="<c:url value='/flow'/>">

    <select name="roleKey">
        <c:forEach items="${space.metadata.roles}" var="roleMapEntry">
            <option>${roleMapEntry.key}</option>
        </c:forEach>
    </select>   
    
    <input type="submit" name="_eventId_allocate" value="<fmt:message key='user_allocate_space_role.allocate'/>"/>
    <%-- switch on in future version
    <input type="checkbox" name="admin" value="true"/>
    Also add as Admin for this space.
    --%>
    <p/>
    
    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>

    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>