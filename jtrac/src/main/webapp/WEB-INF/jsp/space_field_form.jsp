<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Edit Field</span>

<form method="post" action="<c:url value='flow.htm'/>">

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
        <c:if test="${fieldForm.field.name.type < 5}">
            <tr>
                <td class="label">Options</td>
                <td>
                    <c:forEach items="${fieldForm.field.options}" var="entry">
                        (${entry.key})&nbsp;${entry.value}<br/>
                    </c:forEach>                    
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
            </td>
        </tr>
    </table>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
