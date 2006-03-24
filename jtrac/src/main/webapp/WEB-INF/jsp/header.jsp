<%@ page session="true" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="authz" uri="http://acegisecurity.org/authz" %>
<c:set var="principal" value="${ACEGI_SECURITY_CONTEXT.authentication.principal}"/>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>JTrac</title>
    <link rel="stylesheet" type="text/css" href="resources/jtrac.css"/>
    <link rel="stylesheet" type="text/css" href="calendar/calendar-win2k-1.css"/>
    <script type="text/javascript" src="calendar/calendar.js"></script>
    <script type="text/javascript" src="calendar/lang/calendar-en.js"></script>
    <script type="text/javascript" src="calendar/calendar-setup.js"></script>
</head>
<body onLoad="if (document.getElementById('focus') != null) document.getElementById('focus').focus();">

<table width="100%" class="nav-table-outer">
<tr>
<td>
    <table class="nav-table">
        <tr>		
            <td><a href="<c:url value='dashboard.htm'/>">DASHBOARD</a></td>	
        </tr>
    </table>
</td>
<td align="right">
    <table class="nav-table">
        <tr>								
            <td><a href="<c:url value='options.htm'/>">OPTIONS</a></td>
            <td><a href="<c:url value='logout.htm'/>">LOGOUT</a></td>		
            <td>${principal.name}</td>
        </tr>
    </table>
</td>
</tr>
</table>

<hr/>