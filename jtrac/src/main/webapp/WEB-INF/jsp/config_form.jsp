<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div class="heading">
    <fmt:message key='config.${config.param}'/>
</div>

<form method="post" action="<c:url value='/flow'/>">
    
    <table class="jtrac">
        <tr>
            <td class="label">${config.param}</td>        
            <td>
                <spring:bind path="config.value">
                    <input name="${status.expression}" value="${status.value}" id="focus"/>
                    <input type="submit" name="_eventId_submit" value="<fmt:message key='submit'/>"/>
                    <span class="error">${status.errorMessage}</span>
                </spring:bind>
            </td>
        </tr>    
    </table>       
    
    <input type="hidden" name="param" value="${config.param}"/>        
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    
    <p/>
    
    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
    
</form>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>