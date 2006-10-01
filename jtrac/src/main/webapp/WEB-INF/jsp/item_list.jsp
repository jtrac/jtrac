<%@ include file="/WEB-INF/jsp/header.jsp" %>

<c:if test="${!empty calledByRelate}">
    <table class="jtrac">
        <tr>
            <td><a href="<c:url value='/flow?_flowExecutionKey=${flowExecutionKey}&_eventId=back&itemId=${relatingItem.id}'/>">(back)</a></td>
            <td class="selected">
                Searching for items related to ${relatingItem.refId} [${relatingItem.summary}]
            </td>
        </tr>
    </table>
    <br/>
</c:if>

<jtrac:itemlist items="${items}" itemSearch="${itemSearch}"/>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>