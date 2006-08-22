<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script>
function setOptionKey(optionKey) {
    document.spaceFieldForm.optionKey.value = optionKey;
}
</script>

<span class="info">Edit Field</span>

<form name="spaceFieldForm" method="post" action="<c:url value='flow.htm'/>">

    <table class="jtrac">
        <tr>
            <td class="label">Internal Name</td>
            <td>${fieldForm.field.name}</td>
        </tr>
        <tr>
            <td class="label">
                Label
                <font color="red">*</font>
            </td>
            <spring:bind path="fieldForm.field.label">
                <td>
                    <input name="${status.expression}" value="${status.value}"/>
                    <span class="error">${status.errorMessage}</span>
                </td>
            </spring:bind>
        </tr>
        <tr>
            <td class="label">Optional</td>
            <td>
                <spring:bind path="fieldForm.field.optional">
                    <input type="checkbox" name="${status.expression}" <c:if test='${status.value}'>checked="true"</c:if>/>
                    <input type="hidden" name="_${status.expression}"/>
                </spring:bind>
            </td>
        </tr>
        <c:if test="${fieldForm.field.name.type < 4}">
            <tr>
                <td class="label">Options</td>
                <td>
                    <table>
                        <c:forEach items="${fieldForm.field.options}" var="entry">
                            <tr>
                                <td>${entry.key}</td>
                                <td class="alt">${entry.value}</td>
                                <td><input type="submit" name="_eventId_up" value="/\" onClick="setOptionKey('${entry.key}')"/></td>
                                <td><input type="submit" name="_eventId_down" value="\/" onClick="setOptionKey('${entry.key}')"/></td>
                                <td><input type="submit" name="_eventId_edit" value="Edit" onClick="setOptionKey('${entry.key}')"/></td>
                                <td><input type="submit" name="_eventId_delete" value="Delete" onClick="setOptionKey('${entry.key}')"/></td>
                            </tr>
                        </c:forEach>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="label">Add Option</td>
                <td>
                    <spring:bind path="fieldForm.option">
                        <input name="${status.expression}" value="${status.value}" id="focus"/>
                        <input type="submit" name="_eventId_update" value="Update"/>
                        <span class="error">${status.errorMessage}</span>
                    </spring:bind>                    
                </td>
            </tr>
        </c:if>
        <tr>
            <td>
                <input type="submit" name="_eventId_cancel" value="Cancel"/>
            </td>
            <td>
                <input type="submit" name="_eventId_done" value="Done"/>
                <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                <input type="hidden" name="optionKey"/>
            </td>
        </tr>
    </table>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
