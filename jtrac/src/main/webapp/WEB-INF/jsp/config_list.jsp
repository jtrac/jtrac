<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="heading"><fmt:message key='config_list.configurationSettings'/></div>

<table class="jtrac">

    <tr>
        <th><fmt:message key='config_list.parameter'/></th>
        <th><fmt:message key='config_list.value'/></th>
        <th><fmt:message key='config_list.edit'/></th>
        <th><fmt:message key='config_list.description'/></th>
    </tr>

    <c:forEach items="${configParams}" var="configParam" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${param.selected == configParam}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>
        <tr ${rowClass}>
            <td>
                ${configParam}
            </td>
            <td>
                ${configMap[configParam]}
            </td>
            <td align="center">
                <a href="<c:url value='/flow/config?param=${configParam}'/>">(+)</a>
            </td>
            <td>
                <fmt:message key='config.${configParam}'/>
            </td>
        </tr>
    </c:forEach>

</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>