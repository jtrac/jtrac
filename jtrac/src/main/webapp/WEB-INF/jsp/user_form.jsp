<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Create New User</span>

<p/>

<form method="post" action="<c:url value='flow.htm'/>">

    <table class="jtrac">
        <tr>    
            <td class="label">Login ID</td>
            <spring:bind path="userForm.user.loginName">        
                <td>
                    <input name="${status.expression}" value="${status.value}" id="focus" size="15"/>
                    <span class="error">${status.errorMessage}</span>
                </td>
            </spring:bind>
        </tr>
        <tr>
            <td class="label">Full Name</td>
            <spring:bind path="userForm.user.name">
                <td>
                    <input name="${status.expression}" value="${status.value}" size="35"/>
                    <span class="error">${status.errorMessage}</span>
                </td>
            </spring:bind>
        </tr>
        <tr>
            <td class="label">E Mail ID</td>
            <spring:bind path="userForm.user.email">
                <td>
                    <input name="${status.expression}" value="${status.value}" size="35"/>
                    <span class="error">${status.errorMessage}</span>
                </td>
            </spring:bind>
        </tr>
        <tr>
            <td/>
            <td>
                A password will be generated and e-mailed to the new user by default.<br/>
                The fields below are optional.
            </td>
        </tr>
        <tr>
            <td class="label">Password</td>
            <spring:bind path="userForm.user.password">
                <td>
                    <input type="password" name="${status.expression}" value="${status.value}" size="15"/>
                    <span class="error">${status.errorMessage}</span>
                </td>
            </spring:bind>
        </tr>
        <tr>
            <td class="label">Confirm Password</td>
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
                <input type="submit" name="_eventId_submit" value="Submit"/>
                <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
            </td>
        </tr>                                                 
    </table>

    <input type="submit" name="_eventId_cancel" value="Cancel"/>
    
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>