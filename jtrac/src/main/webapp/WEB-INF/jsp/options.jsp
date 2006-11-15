<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info"><fmt:message key='options.optionsMenu'/></span>

<table class="jtrac">
    <tr><td>&nbsp;</td></tr>
    <tr class="nav-table">  
        <td>
            <a href="<c:url value='/flow/user_profile?userId=${principal.id}'/>"><fmt:message key='options.editYourProfile'/></a>
        </td>
    </tr>
<authz:authorize ifAllGranted="ROLE_ADMIN">
    <tr><td>&nbsp;</td></tr>
    <tr class="nav-table">
        <td>
            <a href="<c:url value='/app/user_list.htm'/>"><fmt:message key='options.manageUsers'/></a>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr class="nav-table">
        <td>
            <a href="<c:url value='/app/space_list.htm'/>"><fmt:message key='options.manageSpaces'/></a>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr class="nav-table">
        <td>
            <a href="<c:url value='/app/config_list.htm'/>"><fmt:message key='options.manageSettings'/></a>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr class="nav-table">    
        <td>
            <a href="<c:url value='/app/reindex.htm'/>"><fmt:message key='options.rebuildIndexes'/></a>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr class="nav-table">
        <td>
            <a href="<c:url value='/flow/excel'/>"><fmt:message key='options.importFromExcel'/></a>
        </td>
    </tr>    
</authz:authorize>
</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
