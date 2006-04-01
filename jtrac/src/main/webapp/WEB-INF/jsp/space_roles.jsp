<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Space Roles and State-Transitions (Workflow) for Space: ${space.prefixCode}</span>

<p/>

<c:set var="states" value="${space.metadata.states}"/>
<c:set var="roles" value="${space.metadata.roleList}"/>
<c:set var="fields" value="${space.metadata.fieldList}"/>
<c:set var="rolesCount" value="${space.metadata.rolesCount}"/>
<c:set var="fieldsCount" value="${space.metadata.fieldsCount}"/>
<c:set var="statesCount" value="${space.metadata.statesCount}"/>

<form method="post" action="<c:url value='flow.htm'/>">

<table class="jtrac">
    <tr>
        <td/>
        <td/>
        <th colspan="${statesCount - 1}">Next Allowed State</th>
        <th colspan="${fieldsCount}">Field Level Permissions</th>        
    </tr>
    <tr class="center alt">
        <th>State</th>
        <th>Role</th>
        <c:forEach items="${states}" var="mapEntry">
            <c:if test="${mapEntry.key != 0}">
                <td>${mapEntry.value}</td>
            </c:if>
        </c:forEach>
        <c:forEach items="${fields}" var="field">
            <td>${field.label}</td>
        </c:forEach>      
    </tr>    
    <c:forEach items="${states}" var="mapEntry" varStatus="row">
        <c:set var="rowClass">
            <c:if test="${row.count % 2 == 0}">alt</c:if>
        </c:set>
        <c:forEach items="${roles}" var="role" varStatus="innerRow">
            <c:set var="innerRowClass">
                <c:choose>
                    <c:when test="${selectedStatus == mapEntry.key && selectedRole == role.name}">selected</c:when>
                    <c:when test="${innerRow.count % 2 == 0}">alt</c:when>
                </c:choose>            
            </c:set>
            <c:set var="lastRole">
                <c:if test="${innerRow.count == rolesCount}">bdr-bottom</c:if>
            </c:set>
           <c:set var="roleState" value="${role.states[mapEntry.key]}"/>
            <tr class="center ${innerRowClass} ${lastRole}">
                <c:if test="${innerRow.count == 1}">
                    <td rowspan="${rolesCount}" class="bdr-bottom ${rowClass}">${states[roleState.status]}</td>
                </c:if>
                <td>${role.name}</td>
                <c:forEach items="${states}" var="innerMapEntry">
                    <c:if test="${innerMapEntry.key != 0}">
                        <td>
                            <c:if test="${!empty roleState.transitionMap[innerMapEntry.key]}">X</c:if>&nbsp;
                        </td>
                    </c:if>
                </c:forEach>
                <c:forEach items="${fields}" var="field">
                    <c:set var="mask" value="${roleState.fields[field.name]}"/>
                    <td>
                        <select name="TODO">
                            <option value="0" <c:if test='${mask == 0}'>selected='true'</c:if>>Hide</option>
                            <option value="1" <c:if test='${mask == 1}'>selected='true'</c:if>>View</option>
                            <option value="2" <c:if test='${mask == 2}'>selected='true'</c:if>>Edit</option>
                        </select>
                    </td>
                </c:forEach>                
            </tr>
        </c:forEach>
    </c:forEach>
    <tr class="center">
        <td><input name="state" size="12" value="${state}"/></td>
        <td><input name="role" size="12" value="${role}"/></td>
    <tr>
    <tr class="center">
        <td><input type="submit" name="_eventId_addState" value="Add New State"/></td>
        <td><input type="submit" name="_eventId_addRole" value="Add New Role"/></td>
    <tr>        
</table>

<spring:bind path="space">
    <span class="error">
        <c:forEach items="${status.errorMessages}" var="error">
            <c:out value="${error}"/><br/>
        </c:forEach>
    </span>
</spring:bind>  

<table class="jtrac">
    <tr>
        <td><input type="submit" name="_eventId_back" value="Back"/></td>
        <td><input type="submit" name="_eventId_save" value="Save"/></td> 
    </tr>
</table>

<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>