<%@ include file="/WEB-INF/jsp/header.jsp" %>

<p/>

<span class="info">Dashboard</span>

<p/>

<table class="jtrac">
    <tr><th>Space</th><th>Role</th><th colspan="2">Action</th></tr>

    <c:forEach items="${principal.spaceRoles}" var="spaceRole">
        <tr><td>&nbsp;</td></tr>
        <tr class="nav-table">
            <td>${spaceRole.space.prefixCode}</td>
            <td>${spaceRole.roleKey}</td>
            <td>
                <a href="<c:url value='flow.htm'>
                    <c:param name='_flowId' value='item'/>
                    <c:param name='spaceId' value='${spaceRole.space.id}'/>
                    </c:url>">NEW</a>                
            </td>
            <td>
                <a href="<c:url value='flow.htm'>
                    <c:param name='_flowId' value='itemSearch'/>
                    <c:param name='spaceId' value='${spaceRole.space.id}'/>
                    </c:url>">SEARCH</a>               
            </td>
        </tr>        
    </c:forEach>
    
</table>    

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
