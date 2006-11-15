<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info"><fmt:message key='user_form.userDetails'/></span>

<p/>

<form method="post" action="<c:url value='/flow'/>">

    <spring:bind path="userForm.user.id">        
        <input type="hidden" name="userId" value="${status.value}"/>
    </spring:bind>

    <table class="jtrac">
        <tr>
            <td class="label">
                <fmt:message key='user_form.loginId'/>
                <font color="red">*</font>
            </td>
            <spring:bind path="userForm.user.loginName">        
                <td>
                    <input name="${status.expression}" value="${status.value}" id="focus" size="15"/>
                    <span class="error">${status.errorMessage}</span>
                </td>
            </spring:bind>
        </tr>
        <tr>
            <td class="label">
                <fmt:message key='user_form.fullName'/>
                <font color="red">*</font>
            </td>
            <spring:bind path="userForm.user.name">
                <td>
                    <input name="${status.expression}" value="${status.value}" size="35"/>
                    <span class="error">${status.errorMessage}</span>
                </td>
            </spring:bind>
        </tr>
        <tr>
            <td class="label">
                <fmt:message key='user_form.emailId'/>
                <font color="red">*</font>
            </td>
            <spring:bind path="userForm.user.email">
                <td>
                    <input name="${status.expression}" value="${status.value}" size="35"/>
                    <span class="error">${status.errorMessage}</span>
                </td>
            </spring:bind>
        </tr>
        <tr>
            <td class="label">
                <fmt:message key='user_form.language'/>
            </td>
            <spring:bind path="userForm.user.locale">
                <td>
                    <select name="${status.expression}">
                        <c:forEach items="${locales}" var="localeEntry">
                            <option value="${localeEntry.key}" <c:if test="${status.value == localeEntry.key}">selected='true'</c:if>>${localeEntry.value}</option>
                        </c:forEach>
                    </select>
                </td>
            </spring:bind>
        </tr>        
        <c:if test="${userForm.user.id == 0}">
            <tr>
                <td/>
                <td>
                    <fmt:message key='user_form.passwordMessage'/>                    
                </td>
            </tr>
        </c:if>
        <tr>
            <td class="label"><fmt:message key='user_form.password'/></td>
            <spring:bind path="userForm.password">
                <td>
                    <input type="password" name="${status.expression}" value="${status.value}" size="15"/>
                    <span class="error">${status.errorMessage}</span>
                </td>
            </spring:bind>
        </tr>
        <tr>
            <td class="label"><fmt:message key='user_form.confirmPassword'/></td>
            <td>
                <spring:bind path="userForm.passwordConfirm">
                    <input type="password" name="${status.expression}" value="${status.value}" size="15"/>
                    <span class="error">${status.errorMessage}</span>
                </spring:bind>
            </td>
        </tr>
        <tr>
            <td/>
            <td>
                <input type="submit" name="_eventId_submit" value="<fmt:message key='submit'/>"/>
                <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
            </td>
        </tr>                                                 
    </table>

    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
    
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>