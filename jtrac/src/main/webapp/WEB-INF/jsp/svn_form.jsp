<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Connect to Subversion repository</span>

<form method="post" action="<c:url value='svn_view.htm'/>">

    <table class="jtrac">
        <tr>	
            <td class="label">URL</td>		
            <td><input name="url"/></td>
        </tr>
        <tr>	
            <td class="label">User Name</td>		
            <td><input name="username"/></td>
        </tr>        
        <tr>	
            <td class="label">Password</td>		
            <td><input type="password" name="password"/></td>
        </tr>          
        <tr>
            <td/>
            <td><input type="submit" value="Submit"/></td>
        </tr>                                                 
    </table>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>