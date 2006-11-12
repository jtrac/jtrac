<%@ page session="true" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<option/>
<c:forEach items="${userSpaceRoles}" var="usr">
    <option value="${usr.user.id}">${usr.user.name}</option>
</c:forEach>    

