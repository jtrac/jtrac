<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Options Menu</span>

<p><a href="<c:url value='flow.htm?_flowId=userProfile&userId=${principal.id}'/>">Profile</a></p>

<authz:authorize ifAllGranted="ROLE_ADMIN">
    
    <p><a href="<c:url value='user_list.htm'/>">Users</a></p>

    <p><a href="<c:url value='space_list.htm'/>">Spaces</a></p>

    <p><a href="<c:url value='config_list.htm'/>">Config</a></p>
    
</authz:authorize>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
