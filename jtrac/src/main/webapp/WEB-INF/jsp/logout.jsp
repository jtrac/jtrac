<%@ include file="/WEB-INF/jsp/include.jsp" %>

<html>
    <head>
        <title>Logout Successful</title>
        <link href="<c:url value='/resources/jtrac.css'/>" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        
        <table width="100%" class="jtrac alt">
            <tr>
                <td><a href="<c:url value='/'/>">Home</a>
                <td align="right"></td>
            </tr>
        </table>
        
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
                <td class="info" align="center"><a href="<c:url value='/auth/login.htm'/>">Login</a></td>
            </tr>
        </table>        
        
<%@ include file="/WEB-INF/jsp/footer.jsp" %>