<%@ include file="/WEB-INF/jsp/include.jsp" %>

<select name="history.assignedTo" id="assignedTo">
    <option/>
    <c:forEach items="${userSpaceRoles}" var="user">
        <option value="${user.id}" <c:if test='${selected == user.id}'>selected="true"</c:if>>${user.name}</option>
    </c:forEach>
</select>    

