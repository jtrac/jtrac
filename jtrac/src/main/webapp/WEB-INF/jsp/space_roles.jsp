<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script>
function editState(stateKey) {
    document.spaceRolesForm.stateKey.value = stateKey;
}
function editRole(roleKey) {
    document.spaceRolesForm.roleKey.value = roleKey;
}
function editTransition(stateKey, roleKey, transitionKey) {
    document.spaceRolesForm.stateKey.value = stateKey;
    document.spaceRolesForm.roleKey.value = roleKey;
    document.spaceRolesForm.transitionKey.value = transitionKey;
}
function editMask(stateKey, roleKey, fieldKey) {
    document.spaceRolesForm.stateKey.value = stateKey;
    document.spaceRolesForm.roleKey.value = roleKey;
    document.spaceRolesForm.fieldKey.value = fieldKey;
}
</script>

<span class="info">Space Roles and State-Transitions (Workflow) for Space: ${space.name} (${space.prefixCode})</span>

<br/><br/>

<c:set var="states" value="${space.metadata.states}"/>
<c:set var="roles" value="${space.metadata.roleList}"/>
<c:set var="fields" value="${space.metadata.fieldList}"/>
<c:set var="roleCount" value="${space.metadata.roleCount}"/>
<c:set var="fieldCount" value="${space.metadata.fieldCount}"/>
<c:set var="stateCount" value="${space.metadata.stateCount}"/>

<form name="spaceRolesForm" method="post" action="<c:url value='/flow'/>">

<table class="jtrac">
    <tr class="center">
        <td colspan="2"><input type="submit" name="_eventId_editState" value="Add State"/></td>
        <td colspan="2"><input type="submit" name="_eventId_editRole" value="Add Role"/></td>
        <th colspan="${stateCount - 1}">Next Allowed State</th>
        <th colspan="${fieldCount}">Field Level Permissions<br/>E=Edit, V=view</th> <%-- H=hide support in future --%>        
    </tr>
    <tr class="center alt">
        <th colspan="2">State</th>
        <th colspan="2">Role</th>
        <c:forEach items="${states}" var="mapEntry">
            <c:if test="${mapEntry.key != 0}">
                <td>${mapEntry.value}</td>
            </c:if>
        </c:forEach>
        <c:forEach items="${fields}" var="field">
            <td class="info">${field.label}</td>
        </c:forEach>      
    </tr>
    <%-- end table headings --%>
    <c:forEach items="${states}" var="stateRowEntry" varStatus="row">
        <c:set var="rowClass">
            <c:if test="${row.count % 2 == 0}">alt</c:if>
        </c:set>
        <c:forEach items="${roles}" var="role" varStatus="innerRow">
            <c:set var="innerRowClass">
                <c:if test="${innerRow.count % 2 == 0}">alt</c:if>                
            </c:set>
            <c:set var="lastRole">
                <c:if test="${innerRow.count == roleCount}">bdr-bottom</c:if>
            </c:set>
           <c:set var="roleState" value="${role.states[stateRowEntry.key]}"/>
            <tr class="center ${innerRowClass} ${lastRole}">                
                <c:if test="${innerRow.count == 1}">
                    <td rowspan="${roleCount}" class="bdr-bottom ${rowClass}">
                        ${states[stateRowEntry.key]}
                    </td>
                    <td rowspan="${roleCount}" class="bdr-bottom ${rowClass}">
                        <c:choose>
                            <c:when test="${stateRowEntry.key != 0 && stateRowEntry.key != 99}">
                                <input type="submit" name="_eventId_editState" value="..." onClick="editState('${stateRowEntry.key}')"/>
                            </c:when>
                            <c:otherwise>
                                &nbsp;
                            </c:otherwise>
                        </c:choose>
                    </td>
                </c:if>                
                <td>                    
                    ${role.name}
                </td>
                <td>    
                    <c:choose>
                        <c:when test="${stateRowEntry.key == 0}">
                            <input type="submit" name="_eventId_editRole" value="..." onClick="editRole('${role.name}')"/>
                        </c:when>
                        <c:otherwise>
                            &nbsp;
                        </c:otherwise>
                    </c:choose>                    
                </td>
                <c:forEach items="${states}" var="stateColEntry">
                    <c:if test="${stateColEntry.key != 0}">
                        <c:set var="showTransition">
                            <c:choose>
                                <c:when test="${!empty roleState.transitionMap[stateColEntry.key]}">Y</c:when>
                                <c:otherwise>&nbsp;&nbsp;&nbsp;</c:otherwise>
                            </c:choose>
                        </c:set>
                        <td>
                            <c:choose>
                                <c:when test="${stateRowEntry.key == 0 || stateRowEntry.key == 99}">${showTransition}</c:when>
                                <c:otherwise>
                                    <input type="submit" name="_eventId_editTransition" value="${showTransition}"
                                        onClick="editTransition('${stateRowEntry.key}', '${role.name}', '${stateColEntry.key}')" title="toggle"/>
                                </c:otherwise>
                            </c:choose>                            
                        </td>
                    </c:if>
                </c:forEach>
                <c:forEach items="${fields}" var="field">
                    <c:set var="mask" value="${roleState.fields[field.name]}"/>
                    <c:set var="showMask">
                        <c:choose>
                            <c:when test="${mask == 0}">H</c:when>
                            <c:when test="${mask == 1}">V</c:when>
                            <c:when test="${mask == 2}">E</c:when>
                        </c:choose>
                    </c:set>
                    <td>                           
                      <c:choose>
                            <c:when test="${stateRowEntry.key == 0 || stateRowEntry.key == 99}">${showMask}</c:when>
                            <c:otherwise>
                                <input type="submit" name="_eventId_editMask" value="${showMask}"
                                    onClick="editMask('${stateRowEntry.key}', '${role.name}', '${field.name.text}')" title="toggle"/>
                            </c:otherwise>
                        </c:choose>                             
                    </td>
                </c:forEach>                
            </tr>
        </c:forEach>
    </c:forEach>       
</table> 

<br/>

<input type="submit" name="_eventId_back" value="Back"/>
<input type="submit" name="_eventId_save" value="Save"/>

<input type="hidden" name="stateKey"/>
<input type="hidden" name="roleKey"/>
<input type="hidden" name="transitionKey"/>
<input type="hidden" name="fieldKey"/>
<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

<p/>

<input type="submit" name="_eventId_cancel" value="Cancel"/>

</form>

${workflow.asHtml}

<%@ include file="/WEB-INF/jsp/footer.jsp" %>