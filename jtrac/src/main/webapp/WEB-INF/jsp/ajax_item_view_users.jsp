<%@ page session="true" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<select name="history.assignedTo" id="assignedTo">
    <option/>
    <c:forEach items="${userSpaceRoles}" var="usr">
        <option value="${usr.user.id}" <c:if test='${selected == usr.user.id}'>selected="true"</c:if>>${usr.user.name}</option>
    </c:forEach>
</select>    

