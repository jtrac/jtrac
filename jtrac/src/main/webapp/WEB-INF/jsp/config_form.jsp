<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Edit Config</span>

<p/>

<form method="post" action="<c:url value='flow.htm'/>">
    
    <table class="jtrac">
        <tr>
            <td class="label">${config.key}</td>        
            <td>
                <spring:bind path="config.value">
                    <input name="${status.expression}" value="${status.value}"/>
                    <input type="submit" name="_eventId_submit" value="Submit"/>
                    <span class="error">${status.errorMessage}</span>
                </spring:bind>
            </td>
        </tr>    
    </table>       
    
    <input type="hidden" name="key" value="${config.key}"/>
    
    <input type="hidden" name="stateKey" value="${stateKey}"/>
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
    
    <p/>
    
    <input type="submit" name="_eventId_cancel" value="Cancel"/>
    
</form>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>