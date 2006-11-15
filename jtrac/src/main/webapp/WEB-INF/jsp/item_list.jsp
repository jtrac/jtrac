<%@ include file="/WEB-INF/jsp/header.jsp" %>

<c:if test="${!empty calledByRelate}">
    <table class="jtrac">
        <tr>
            <td><a href="<c:url value='/flow?_flowExecutionKey=${flowExecutionKey}&_eventId=back&itemId=${relatingItem.id}'/>">(<fmt:message key='back'/>)</a></td>
            <td class="selected">
                <fmt:message key='item_list.searchingForRelated'/> ${relatingItem.refId} [${relatingItem.summary}]
            </td>
        </tr>
    </table>
    <br/>
</c:if>

<jtrac:itemlist items="${items}" itemSearch="${itemSearch}"/>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>