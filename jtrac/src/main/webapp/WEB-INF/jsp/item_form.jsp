<%@ include file="/WEB-INF/jsp/header.jsp" %>

<p/>

<form method="post" action="<c:url value='flow.htm'/>">

<table class="jtrac">
    <tr>    
        <td class="label">Space</td>    
        <td>${item.space.prefixCode}</td>
    </tr>           
    <tr>
        <td/>
        <td>
            <input type="submit" name="_eventId_submit" value="Submit"/>
            <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
        </td>
    </tr>                                                 
</table>
    
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>