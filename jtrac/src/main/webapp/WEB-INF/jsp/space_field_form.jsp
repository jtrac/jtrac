<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script>
function setOptionKey(optionKey) {
    document.spaceFieldForm.optionKey.value = optionKey;
}
</script>

<span class="info"><fmt:message key='space_field_form.editField'/></span>

<br/><br/>

<form name="spaceFieldForm" method="post" action="<c:url value='/flow'/>">

    <table class="jtrac">
        <tr>
            <td class="label"><fmt:message key='space_field_form.internalName'/></td>
            <td>
                ${fieldForm.field.name.text}
                <input type="submit" name="_eventId_delete" value="<fmt:message key='delete'/>"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <fmt:message key='space_field_form.label'/>
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
            <td class="label"><fmt:message key='space_field_form.optional'/></td>
            <td>
                <spring:bind path="fieldForm.field.optional">
                    <input type="checkbox" name="${status.expression}" <c:if test='${status.value}'>checked="true"</c:if>/>
                    <input type="hidden" name="_${status.expression}"/>
                </spring:bind>
            </td>
        </tr>
        <c:if test="${fieldForm.field.name.type < 4}">
            <tr>
                <td class="label"><fmt:message key='space_field_form.options'/></td>
                <td>
                    <table>
                        <c:forEach items="${fieldForm.field.options}" var="entry">
                            <tr>
                                <td>${entry.key}</td>
                                <td class="alt">${entry.value}</td>
                                <td><input type="submit" name="_eventId_up" value="/\" onClick="setOptionKey('${entry.key}')"/></td>
                                <td><input type="submit" name="_eventId_down" value="\/" onClick="setOptionKey('${entry.key}')"/></td>
                                <td><input type="submit" name="_eventId_edit" value="<fmt:message key='edit'/>" onClick="setOptionKey('${entry.key}')"/></td>
                            </tr>
                        </c:forEach>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="label"><fmt:message key='space_field_form.addOption'/></td>
                <td>
                    <spring:bind path="fieldForm.option">
                        <input name="${status.expression}" value="${status.value}" id="focus"/>
                        <input type="submit" name="_eventId_update" value="<fmt:message key='space_field_form.update'/>"/>
                        <span class="error">${status.errorMessage}</span>
                    </spring:bind>                    
                </td>
            </tr>
        </c:if>
        <tr>
            <td/>
            <td>
                <input type="submit" name="_eventId_done" value="<fmt:message key='space_field_form.done'/>"/>
                <input type="hidden" name="fieldName" value="${fieldForm.field.name.text}"/>
                <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                <input type="hidden" name="optionKey"/>
            </td>
        </tr>
    </table>

    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
    
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
