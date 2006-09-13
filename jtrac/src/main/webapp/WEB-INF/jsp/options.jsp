<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Options Menu</span>

<table class="jtrac">
    <tr><td>&nbsp;</td></tr>
    <tr class="nav-table">  
        <td>
            <a href="<c:url value='/flow/user_profile?userId=${principal.id}'/>">Edit Your Profile</a>
        </td>
    </tr>
<authz:authorize ifAllGranted="ROLE_ADMIN">
    <tr><td>&nbsp;</td></tr>
    <tr class="nav-table">
        <td>
            <a href="<c:url value='/app/user_list.htm'/>">Manage Users</a>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr class="nav-table">
        <td>
            <a href="<c:url value='/app/space_list.htm'/>">Manage Spaces</a>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr class="nav-table">
        <td>
            <a href="<c:url value='/app/config_list.htm'/>">Manage Settings</a>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr class="nav-table">    
        <td>
            <a href="<c:url value='/app/reindex.htm'/>">Rebuild Indexes</a>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr class="nav-table">
        <td>
            <a href="<c:url value='/flow/excel'/>">Import from Excel</a>
        </td>
    </tr>    
</authz:authorize>
</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
