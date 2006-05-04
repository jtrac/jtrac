<%@ include file="/WEB-INF/jsp/header.jsp" %>

<p/>

<form method="post" action="<c:url value='flow.htm'/>">

<table class="jtrac">
    <tr>
        <td class="info">View Item by Id</td>
        <spring:bind path="itemSearch.refId">
            <td>
                <input name="${status.expression}" value="${status.value}"/>
                <input type="submit" name="_eventId_view" value="View"/>                			
            </td>
            <td>
                <div class="error"><c:out value="${status.errorMessage}"/></div>
                Use the Search button (below) to search for items.
            </td>
        </spring:bind>		
    </tr>
</table>

<p/>

<table class="jtrac">
    <tr>
        <th <c:if test="${itemSearch.pageSize != 25}">class="selected"</c:if>>Results / page</th>
        <td>
            <select name="pageSize">
                <c:forTokens items="5,10,15,25,50,100,-1" delims="," var="size">
                    <option value="${size}" <c:if test="${itemSearch.pageSize == size}">selected="true"</c:if>>
                        <c:choose>
                            <c:when test="${size == -1}">No Limit</c:when>
                            <c:otherwise>${size}</c:otherwise>
                        </c:choose>
                    </option>
                </c:forTokens>
            </select>
        </td>
        <td/>	
        <th <c:if test="${itemSearch.sortFieldName != 'id'}">class="selected"</c:if>>Sort On Column</th>
        <td>
            <select name="sortFieldName">
                <option value="id" <c:if test="${itemSearch.sortFieldName == 'id'}">selected="true"</c:if>>ID</option>
                <c:forEach items="${itemSearch.fields}" var="field">
                    <option value="${field.name}" <c:if test="${itemSearch.sortFieldName == field.name}">selected="true"</c:if>>
                        ${field.label}
                    </option>
                </c:forEach>
            </select>
        </td>
        <th <c:if test="${!itemSearch.sortDescending}">class="selected"</c:if>>
            <input type="checkbox" name="sortDescending" value="true" <c:if test="${itemSearch.sortDescending}">checked="true"</c:if>/>&nbsp;Descending
            <input type="hidden" name="_sortDescending"/>
        </th>
        <td/>
        <th <c:if test="${itemSearch.showDetail}">class="selected"</c:if>>
            Show Detail&nbsp;<input type="checkbox" name="showDetail" value="true" <c:if test="${itemSearch.showDetail}">checked="true"</c:if>/>
            <input type="hidden" name="_showDetail"/>
        </th>	
        <th <c:if test="${itemSearch.showHistory}">class="selected"</c:if>>
            Show History&nbsp;<input type="checkbox" name="showHistory" value="true" <c:if test="${itemSearch.showHistory}">checked="true"</c:if>/>
            <input type="hidden" name="_showHistory"/>
        </th>
    </tr>
</table>

<c:set var="searchMap" value="${itemSearch.searchMap}"/>

<table class="bdr-collapse">
    <tr>
        <c:if test="${empty itemSearch.space}">
            <td>
                <table class="jtrac">
                    <tr><th <c:if test="${!empty itemSearch.severitySet}">class="selected"</c:if>>Severity</th></tr>
                    <tr>						
                        <td>
                            <select name="severitySet" size="8" multiple="true">
                                <c:set var="severityMap" value="${searchMap['severitySet']}"/>
                                <c:forEach items="${itemSearch.severityOptions}" var="entry">	
                                    <option value="${entry.key}" <c:if test="${severityMap[entry.key]}">selected="true"</c:if>>
                                        ${entry.value}
                                    </option>
                                </c:forEach>				
                            </select>
                            <input type="hidden" name="_severitySet"/>
                        </td>
                    </tr>
                </table>
            </td>
            <td>
                <table class="jtrac">
                    <tr><th <c:if test="${!empty itemSearch.prioritySet}">class="selected"</c:if>>Priority</th></tr>
                    <tr>			
                        <td>
                            <select name="prioritySet" size="8" multiple="true">
                                <c:set var="priorityMap" value="${searchMap['prioritySet']}"/>
                                <c:forEach items="${itemSearch.priorityOptions}" var="entry">	
                                    <option value="${entry.key}" <c:if test="${priorityMap[entry.key]}">selected="true"</c:if>>
                                        ${entry.value}
                                    </option>
                                </c:forEach>				
                            </select>
                            <input type="hidden" name="_prioritySet"/>
                        </td>
                    </tr>
                </table>
            </td>
        </c:if>
        <td>
            <table class="jtrac">
                <tr><th <c:if test="${!empty itemSearch.statusSet}">class="selected"</c:if>>Status</th></tr>
                <tr>			
                    <td>
                        <select name="statusSet" size="8" multiple="true">
                            <c:set var="statusMap" value="${searchMap['statusSet']}"/>
                            <c:forEach items="${itemSearch.statusOptions}" var="entry">
                                <c:set var="thisKey">${entry.key}</c:set>
                                <option value="${thisKey}" <c:if test="${statusMap[thisKey]}">selected="true"</c:if>>
                                    ${entry.value}
                                </option>
                            </c:forEach>				
                        </select>
                        <input type="hidden" name="_statusSet"/>
                    </td>
                </tr>
            </table>
        </td>
        <td>
            <table class="jtrac">
                <tr><th <c:if test="${!empty itemSearch.loggedBySet}">class="selected"</c:if>>Logged By</th></tr>
                <tr>	
                    <td>
                        <select name="loggedBySet" size="8" multiple="true">
                            <c:set var="loggedByMap" value="${searchMap['loggedBySet']}"/>
                            <c:forEach items="${users}" var="user">
                                <c:set var="thisKey">${user.id}</c:set>
                                <option value="${thisKey}" <c:if test="${loggedByMap[thisKey]}">selected="true"</c:if>>
                                    ${user.name}
                                </option>
                            </c:forEach>				
                        </select>
                        <input type="hidden" name="_loggedBySet"/>
                    </td>
                </tr>
            </table>
        </td>
        <td>
            <table class="jtrac">
                <tr><th <c:if test="${!empty itemSearch.assignedToSet}">class="selected"</c:if>>Assigned To</th></tr>
                <tr>	
                    <td>
                        <select name="assignedToSet" size="8" multiple="true">
                            <c:set var="assignedToMap" value="${searchMap['assignedToSet']}"/>
                            <c:forEach items="${users}" var="user">
                                <c:set var="thisKey">${user.id}</c:set>
                                <option value="${thisKey}" <c:if test="${assignedToMap[thisKey]}">selected="true"</c:if>>
                                    ${user.name}
                                </option>
                            </c:forEach>				
                        </select>
                        <input type="hidden" name="_assignedToSet"/>
                    </td>
                </tr>
            </table>
        </td>
        <td valign="top">
            <table class="jtrac">
                <c:forTokens items="createdDate,modifiedDate" delims="," var="field">
                    <tr>
                        <th colspan="2">
                            <c:choose>
                                <c:when test="${field=='createdDate'}">Created Date</c:when>
                                <c:when test="${field=='modifiedDate'}">History Updated Date</c:when>
                            </c:choose>
                        </th>
                    </tr>
                    <c:forTokens items="Start,End" delims="," var="suffix">
                        <c:set var="path">${field}${suffix}</c:set>
                        <c:set var="bindPath">itemSearch.${path}</c:set>
                        <spring:bind path="${bindPath}">						
                            <tr>
                                <th <c:if test="${!empty status.value}">class="selected"</c:if>>
                                    <c:choose>
                                        <c:when test="${suffix=='Start'}">On / After</c:when>
                                        <c:when test="${suffix=='End'}">On / Before</c:when>
                                    </c:choose>
                                </th>
                                <td>
                                    <input name="${status.expression}" value="${status.value}" id="${path}" size="8"/>
                                    <button type="reset" id="${path}Button">...</button>
                                    <script type="text/javascript">
                                        Calendar.setup({
                                        inputField     :    "${path}",
                                        ifFormat       :    "%Y-%m-%d",
                                        button         :    "${path}Button",
                                        step           :    1
                                        });
                                    </script>								
                                </td>
                                <td class="error"><c:out value="${status.errorMessage}"/></td>
                            </tr>
                        </spring:bind>
                    </c:forTokens>
                </c:forTokens>
            </table>
        </td>
    </tr>
</table>

<c:set var="fieldMap" value="${itemSearch.fieldMap}"/>

<table class="bdr-collapse">
    <tr>
        <c:if test="${!empty itemSearch.space}">             
            <c:forTokens items="severity,priority,cusInt01,cusInt02,cusInt03,cusInt04,cusInt05,cusInt06,cusInt07,cusInt08,cusInt09,cusInt10" delims="," var="name">
                <c:set var="field" value="${fieldMap[name]}"/>
                <c:set var="nameSet">${name}Set</c:set>
                <c:set var="optionsMap" value="${searchMap[nameSet]}"/>
                <c:if test="${!empty field}">
                    <td>
                        <table class="jtrac">
                            <tr>
                                
                                <th <c:if test="${!empty optionsMap}">class="selected"</c:if>>
                                    <c:out value="${field.label}"/>
                                </th>
                            </tr>
                            <tr>
                                <td>
                                    <select name="${nameSet}" size="8" multiple="true">                                        
                                        <c:forEach items="${field.options}" var="entry">
                                            <option value="${entry.key}" 
                                            <c:if test="${optionsMap[entry.key]}">selected="true"</c:if>>${entry.value}</option>
                                        </c:forEach>				
                                    </select>
                                    <input type="hidden" name="_${nameSet}"/>
                                </td>
                            </tr>
                        </table>
                    </td>
                </c:if>
            </c:forTokens>
        </c:if>
    </tr>
</table>

<table class="bdr-collapse">
    <tr valign="top">

        <c:if test="${!empty itemSearch.space}">
            <td>
                <table class="jtrac">
                    <c:forTokens items="cusTim01,cusTim02,cusTim03" delims="," var="name">
                        <c:set var="field" value="${fieldMap[name]}"/>
                        <c:if test="${!empty field}">
                            <tr>
                                <th colspan="2">${field.label}</th>
                            </tr>
                            <c:forTokens items="Start,End" delims="," var="suffix">
                                <c:set var="path">${name}${suffix}</c:set>
                                <c:set var="bindPath">itemSearch.${path}</c:set>
                                <spring:bind path="${bindPath}">						
                                    <tr>
                                        <th <c:if test="${!empty status.value}">class="selected"</c:if>>
                                            <c:choose>
                                                <c:when test="${suffix=='Start'}">On / After</c:when>
                                                <c:when test="${suffix=='End'}">On / Before</c:when>
                                            </c:choose>
                                        </th>
                                        <td>
                                            <input name="${path}" value="${status.value}" id="${path}" size="8"/>
                                            <button type="reset" id="${path}Button">...</button>
                                            <script type="text/javascript">
                                                Calendar.setup({
                                                inputField     :    "${path}",
                                                ifFormat       :    "%Y-%m-%d",
                                                button         :    "${path}Button",
                                                step           :    1
                                                });
                                            </script>								
                                        </td>
                                        <td class="error">${status.errorMessage}</td>
                                    </tr>
                                </spring:bind>
                            </c:forTokens>
                        </c:if>
                    </c:forTokens>
                </table>
            </td>
        </c:if>

        <c:if test="${empty itemSearch.space}">
            <td>
                <table class="jtrac">
                    <tr><th <c:if test="${!empty itemSearch.spaceSet}">class="selected"</c:if>>Tracker</th></tr>
                    <tr>				
                        <td>
                            <select name="spaceSet" size="8" multiple="true">
                                <c:set var="spaceMap" value="${searchMap['spaceSet']}"/>
                                <c:forEach items="${itemSearch.spaceOptions}" var="entry">
                                    <c:set var="thisKey">${entry.key}</c:set>
                                    <option value="${thisKey}" <c:if test="${spaceMap[thisKey]}">selected="true"</c:if>>
                                        ${entry.value}
                                    </option>
                                </c:forEach>					
                            </select>
                            <input type="hidden" name="_spaceSet"/>
                        </td>
                    </tr>
                </table>
            </td>
        </c:if>

        <td>
            <table class="jtrac">
                <tr>
                    <th <c:if test="${!empty itemSearch.summary}">class="selected"</c:if>>Summary</th>
                    <td><input name="title" value="${itemSearch.summary}"/></td>
                </tr>
                <c:if test="${!empty itemSearch.space}">
                    <c:forTokens items="cusStr01,cusStr02,cusStr03,cusStr04,cusStr05" delims="," var="name">
                        <c:set var="field" value="${fieldMap[name]}"/>
                        <c:if test="${!empty field}">
                            <tr>
                                <th <c:if test="${!empty itemSearch[field.name]}">class="selected"</c:if>>
                                    ${field.label}
                                </th>
                                <td>
                                    <input name="${field.name}" value="${itemSearch[field.name]}"/>
                                </td>
                                <td/>
                            </tr>
                        </c:if>
                    </c:forTokens>
                </c:if>
            </table>
        </td>
        <td>
            <input type="submit" name="_eventId_search" value="Search"/>            
        </td>

    </tr>
</table>

<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

</form>

${_conversationId}

<%@ include file="/WEB-INF/jsp/footer.jsp" %>