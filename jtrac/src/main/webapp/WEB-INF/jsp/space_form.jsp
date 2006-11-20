<%@ include file="/WEB-INF/jsp/header.jsp" %>

<form method="post" action="<c:url value='/flow'/>">

<div class="heading">    
    <fmt:message key='space_form.spaceDetails'/>
    <c:if test="${space.id > 0}"><input type="submit" name="_eventId_delete" value="<fmt:message key='delete'/>"/></c:if>
</div>    

    <table class="jtrac">
        <tr>
            <td class="label">
                <fmt:message key='space_form.displayName'/>
                <font color="red">*</font>
            </td>
            <spring:bind path="space.name">
                <td>
                    <input name="${status.expression}" value="${status.value}" id="focus"/>
                    <span class="error">${status.errorMessage}</span>
                </td>
            </spring:bind>
        </tr>         
        <tr>
            <td class="label">
                <fmt:message key='space_form.spaceKey'/>
                <font color="red">*</font>
            </td>
            <spring:bind path="space.prefixCode">
                <td>
                    <input name="${status.expression}" value="${status.value}" size="10"/>
                    <span class="error">${status.errorMessage}</span>
                </td>
            </spring:bind>
        </tr>       
        <tr>
            <td class="label"><fmt:message key='space_form.description'/></td>
            <spring:bind path="space.description">
                <td>
                    <textarea name="${status.expression}" rows="3" cols="40">${status.value}</textarea>
                    <span class="error">${status.errorMessage}</span>
                </td>
            </spring:bind>
        </tr>
        <tr>
            <td class="label"><fmt:message key='space_form.makePublic'/></td>
            <spring:bind path="space.guestAllowed">
                <td>
                    <input type="checkbox" name="${status.expression}" value="true" <c:if test="${status.value}">checked="true"</c:if>/>                
                    <fmt:message key='space_form.allowGuest'/>
                    <input type="hidden" name="_${status.expression}"/>                    
                </td>
            </spring:bind>
        </tr>   
        <tr>
            <td/>
            <td>
                <input type="submit" name="_eventId_submit" value="<fmt:message key='next'/>"/>
                <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
            </td>
        </tr>
    </table>

    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
    
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>