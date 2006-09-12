<%@ page session="true" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>

<html>
    <head>
        <title>Logout Successful</title>
        <link href="<c:url value='/resources/jtrac.css'/>" rel="stylesheet" type="text/css"/>
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
                <td class="info" align="center"><a href="<c:url value='/app/auth/login.htm'/>">Login</a></td>
            </tr>
        </table>        
        
<%@ include file="/WEB-INF/jsp/footer.jsp" %>