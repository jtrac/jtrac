<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Config</span>

<p/>

<table class="jtrac">

    <tr><th>Param</th><th>Value</th><th>(edit)</th></tr>

    <c:forEach items="${configParams}" var="configParam" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedConfigParam == configParam}">class="selected"</c:when>
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
                <a href="<c:url value='flow.htm?_flowId=config&param=${configParam}'/>">(+)</a>
            </td>            
        </tr>
    </c:forEach>

</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>