<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Config</span>

<p/>

<table class="jtrac">

    <tr><th>Key</th><th>Value</th><th>(edit)</th></tr>

    <c:forEach items="${configKeys}" var="configKey" varStatus="row">
        <c:set var="rowClass">
            <c:choose>
                <c:when test="${selectedConfigKey == configKey}">class="selected"</c:when>
                <c:when test="${row.count % 2 == 0}">class="alt"</c:when>
            </c:choose>            
        </c:set>
        <tr ${rowClass}>
            <td>
                ${configKey}
            </td>
            <td>
                ${configMap[configKey]}
            </td>
            <td align="center">
                <a href="<c:url value='flow.htm'>
                    <c:param name='_flowId' value='config'/>
                    <c:param name='key' value='${configKey}'/>
                    </c:url>">(+)</a>
            </td>            
        </tr>
    </c:forEach>

</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>