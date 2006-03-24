<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Dashboard</span>
   
<c:forEach items="${principal.spaceRoles}" var="spaceRole">
    ${spaceRole.space.prefixCode} - ${spaceRole.roleKey}
</c:forEach>    

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
