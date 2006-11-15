<% session.invalidate(); %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<html>
    <head>
        <title><fmt:message key='logout.title'/></title>
        <link href="<c:url value='/resources/jtrac.css'/>" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        
        <table width="100%" class="jtrac alt">
            <tr>
                <td><a href="<c:url value='/'/>"><fmt:message key='logout.home'/></a>
                <td align="right"></td>
            </tr>
        </table>
        
        <hr/>        
        <br/>
        <br/>

        <table class="jtrac">
            <tr>
                <td class="info"><fmt:message key='logout.message'/></td>
            </tr>
            <tr>
                <td><br/></td>
            </tr>
            <tr>
                <td class="info" align="center"><a href="<c:url value='/auth/login.htm'/>"><fmt:message key='logout.login'/></a></td>
            </tr>
        </table>        
        
<%@ include file="/WEB-INF/jsp/footer.jsp" %>