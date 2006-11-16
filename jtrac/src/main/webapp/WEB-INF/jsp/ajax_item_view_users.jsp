<%@ include file="/WEB-INF/jsp/include.jsp" %>

<select name="history.assignedTo" id="assignedTo">
    <option/>
    <c:forEach items="${userSpaceRoles}" var="usr">
        <option value="${usr.user.id}" <c:if test='${selected == usr.user.id}'>selected="true"</c:if>>${usr.user.name}</option>
    </c:forEach>
</select>    

