<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Dashboard</span>

<p/>

<a href="<c:url value='webflow.htm'><c:param name='_flowId' value='userCreate-flow'/></c:url>">Create User</a>

<p/>

<a href="<c:url value='webflow.htm'><c:param name='_flowId' value='spaceCreate-flow'/></c:url>">Create Space</a>

<p/>

<a href="<c:url value='svn_form.htm'/>">Subversion</a>

<p/>

<a href="<c:url value='logout.htm'/>">Logout</a>

<p/>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
