<%@ page session="true" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>Logout Successful</title>
        <link href="resources/jtrac.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        
        <table width="100%" class="jtrac"><tr class="alt"><td>JTrac</td></tr></table>
        
        <hr/>        
        <br/>
        <br/>

        <table class="jtrac">
        	<tr>
        		<td class="info">Logout Successful.  Any active "remember me" sessions have been disabled.</td>
        	</tr>
        	<tr>
        		<td><br/></td>
    		</tr>
        	<tr>
        		<td class="info" align="center"><a href="<c:url value='login.htm'/>">Login</a></td>
        	</tr>
   		</table>
        
        <br/>
        <br/>
        <hr/>
        
        <table width="100%" class="jtrac"><tr class="alt"><td><br/></td></tr></table>
        
    </body>
</html>