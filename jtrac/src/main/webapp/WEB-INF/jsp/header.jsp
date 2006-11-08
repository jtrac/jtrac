<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="authz" uri="http://acegisecurity.org/authz" %>
<%@ taglib prefix="jtrac" uri="/WEB-INF/tld/jtrac.tld" %>
<c:if test="${empty principal}"><c:set var="principal" value="${ACEGI_SECURITY_CONTEXT.authentication.principal}"/></c:if>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>JTrac</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/jtrac.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/calendar/calendar-win2k-1.css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/prototype.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/scriptaculous.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/overlibmws.js"></script>    
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/calendar/calendar.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/calendar/lang/calendar-en.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/calendar/calendar-setup.js"></script>    
</head>
<body onLoad="if (document.getElementById('focus') != null) document.getElementById('focus').focus();">

<table width="100%" class="nav-table-outer">
<tr>
<td>
    <table class="nav-table">
        <tr>        
            <td><a href="<c:url value='/app'/>">DASHBOARD</a></td>
            <c:if test="${!empty space && _flowId != 'space'}">
                <td>${space.name}</td>
                <c:if test="${principal.id != 0}">
                    <td><a href="<c:url value='/flow/item?spaceId=${space.id}'/>">NEW</a></td>
                </c:if>
                <td><a href="<c:url value='/flow/item_search?spaceId=${space.id}'/>">SEARCH</a></td>                
            </c:if>
            <c:if test="${empty space && principal.spaceCount > 1}">
                <td><a href="<c:url value='/flow/item_search'/>">SEARCH</a></td>               
            </c:if>            
        </tr>
    </table>
</td>
<td align="right">
    <table class="nav-table">
        <tr>                                
            <c:choose>
                <c:when test="${principal.id == 0}">
                    <td><a href="<c:url value='/auth/login.htm'/>">LOGIN</a></td>
                </c:when>
                <c:otherwise>
                    <td><a href="<c:url value='/app/options.htm'/>">OPTIONS</a></td>
                    <td><a href="<c:url value='/auth/logout.htm'/>">LOGOUT</a></td>
                </c:otherwise>
            </c:choose>
            <td>${principal.name}</td>
        </tr>
    </table>
</td>
</tr>
</table>
<hr/>
<br/>