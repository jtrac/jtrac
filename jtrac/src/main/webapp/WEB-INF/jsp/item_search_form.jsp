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
        <th <c:if test="${itemSearch.rowsPerPage != 25}">class="selected"</c:if>>Results / page</th>
        <td>
            <select name="rowsPerPage">
                <c:forTokens items="5,10,15,25,50,100,-1" delims="," var="size">
                    <option value="${size}" <c:if test="${itemSearch.rowsPerPage == size}">selected="true"</c:if>>
                        <c:choose>
                            <c:when test="${size == -1}">No Limit</c:when>
                            <c:otherwise>${size}</c:otherwise>
                        </c:choose>
                    </option>
                </c:forTokens>
            </select>
        </td>
        <td/>	
        <th <c:if test="${itemSearch.sortFieldName != 'itemId'}">class="selected"</c:if>>Sort On Column</th>
        <td>
            <select name="sortFieldName">
                <option value="itemId" <c:if test="${itemSearch.sortFieldName == 'itemId'}">selected="true"</c:if>>ID</option>
                <c:forEach items="${itemSearch.fields}" var="field">
                    <option value="${field.name}" <c:if test="${itemSearch.sortFieldName == field.name}">selected="true"</c:if>>
                        ${field.label}
                    </option>
                </c:forEach>
            </select>
        </td>
        <th <c:if test="${!itemSearch.sortDescending}">class="selected"</c:if>>
            <input type="checkbox" name="sortDescending" value="true" <c:if test="${itemSearch.sortDescending}">checked="true"</c:if>/>&nbsp;Descending
        </th>
        <td/>
        <th <c:if test="${itemSearch.showDescription}">class="selected"</c:if>>
            Show Description&nbsp;<input type="checkbox" name="showDescription" value="true" <c:if test="${itemSearch.showDescription}">checked="true"</c:if>/>
        </th>	
        <th <c:if test="${itemSearch.showHistory}">class="selected"</c:if>>Show History&nbsp;<input type="checkbox" name="showHistory" value="true" <c:if test="${itemSearch.showHistory}">checked="true"</c:if>/></th>
        <td><input type="submit" name="reset" value="Reset"/></td>
    </tr>
</table>

<c:set var="searchMap" value="${itemSearch.searchMap}"/>

<table class="bdr-collapse">
    <tr>
        <c:if test="${empty itemSearch.space}">
            <td>
                <table>
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
                        </td>
                    </tr>
                </table>
            </td>
            <td>
                <table>
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
                        </td>
                    </tr>
                </table>
            </td>
        </c:if>
        <td>
            <table>
                <tr><th <c:if test="${!empty itemSearch.statusSet}">class="selected"</c:if>>Status</th></tr>
                <tr>			
                    <td>
                        <select name="statusSet" size="8" multiple="true">
                            <c:set var="statusMap" value="${searchMap['statusSet']}"/>
                            <c:forEach items="${itemSearch.statusOptions}" var="entry">	
                                <option value="${entry.key}" <c:if test="${statusMap[entry.key]}">selected="true"</c:if>>
                                    ${entry.value}
                                </option>
                            </c:forEach>				
                        </select>
                    </td>
                </tr>
            </table>
        </td>
        <td>
            <table>
                <tr><th <c:if test="${!empty itemSearch.loggedBySet}">class="selected"</c:if>>Logged By</th></tr>
                <tr>	
                    <td>
                        <select name="loggedBySet" size="8" multiple="true">
                            <c:set var="loggedByMap" value="${searchMap['loggedBySet']}"/>
                            <c:forEach items="${userRoles}" var="userRole">                                
                                <option value="${userRole.user.id}" <c:if test="${loggedByMap[userRole.user.id]}">selected="true"</c:if>>
                                    ${userRole.user.name}
                                </option>
                            </c:forEach>				
                        </select>		
                    </td>
                </tr>
            </table>
        </td>
        <td>
            <table>
                <tr><th <c:if test="${!empty itemSearch.assignedToSet}">class="selected"</c:if>>Assigned To</th></tr>
                <tr>	
                    <td>
                        <select name="assignedToSet" size="8" multiple="true">
                            <c:set var="assignedToMap" value="${searchMap['assignedToSet']}"/>
                            <c:forEach items="${userRoles}" var="userRole">                                
                                <option value="${userRole.user.id}" <c:if test="${assignedToMap[userRole.user.id]}">selected="true"</c:if>>
                                    ${userRole.user.name}
                                </option>
                            </c:forEach>				
                        </select>		
                    </td>
                </tr>
            </table>
        </td>
        <td>
            <table>
                <c:forTokens items="loggedDate,historyDate" delims="," var="field">
                    <tr>
                        <th colspan="2">
                            <c:choose>
                                <c:when test="${field=='loggedDate'}">Created Date</c:when>
                                <c:when test="${field=='historyDate'}">Updated Date</c:when>
                            </c:choose>
                        </th>
                    </tr>
                    <c:forTokens items="Start,End" delims="," var="suffix">
                        <c:set var="path">${field}${suffix}</c:set>
                        <c:set var="bindPath">itemFilter.${path}</c:set>
                        <spring:bind path="${bindPath}">						
                            <tr>
                                <th <c:if test="${!empty searchMap[path]}">class="selected"</c:if>>
                                    <c:choose>
                                        <c:when test="${suffix=='Start'}">On / After</c:when>
                                        <c:when test="${suffix=='End'}">On / Before</c:when>
                                    </c:choose>
                                </th>
                                <td>
                                    <input name="${path}" value="${searchMap[path]}" id="${path}" size="8"/>
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

<table>
    <tr>
        <c:if test="${!empty selectedTrackerId}">
            <c:set var="metadata" value="${tracker.metadata}"/> 
            <c:forTokens items="severityId,priorityId,cusInt01,cusInt02,cusInt03,cusInt04,cusInt05,cusInt06,cusInt07,cusInt08,cusInt09,cusInt10" delims="," var="name">
                <c:set var="field" value="${metadata[name]}"/>
                <c:if test="${!empty field}">
                    <td>
                        <table>
                            <tr>
                                <th <c:if test="${selectedMap[name]}">class="selected"</c:if>>
                                    <c:out value="${field.label}"/>
                                </th>
                            </tr>
                            <tr>
                                <td>
                                    <select name="<c:out value="${name}"/>Array" size="8" multiple="true">
                                        <c:set var="thisMap" value="${filterMap[name]}"/>
                                        <c:forEach items="${field.options}" var="option">
                                            <c:set var="value"><c:out value="${option.key}"/></c:set>
                                            <option value="<c:out value="${value}"/>" 
                                            <c:if test="${thisMap[value]}">selected="true"</c:if>><c:out value="${option.value}"/></option>
                                        </c:forEach>				
                                    </select>						
                                </td>
                            </tr>
                        </table>
                    </td>
                </c:if>
            </c:forTokens>
        </c:if>
    </tr>
</table>

<table>
    <tr valign="top">

        <c:if test="${!empty selectedTrackerId}">
            <td>
                <table>
                    <c:forTokens items="cusTim01,cusTim02,cusTim03" delims="," var="name">
                        <c:set var="field" value="${metadata[name]}"/>
                        <c:if test="${!empty field}">
                            <tr>
                                <th colspan="2">
                                    <c:out value="${field.label}"/>
                                </th>
                            </tr>
                            <c:forTokens items="Start,End" delims="," var="suffix">
                                <c:set var="path"><c:out value="${name}"/><c:out value="${suffix}"/></c:set>
                                <c:set var="bindPath">itemFilter.<c:out value="${path}"/></c:set>
                                <spring:bind path="${bindPath}">						
                                    <tr>
                                        <th <c:if test="${!empty filterMap[path]}">class="selected"</c:if>>
                                            <c:choose>
                                                <c:when test="${suffix=='Start'}">On / After</c:when>
                                                <c:when test="${suffix=='End'}">On / Before</c:when>
                                            </c:choose>
                                        </th>
                                        <td>
                                            <input name="<c:out value="${path}"/>" 
                                            value="<c:out value="${filterMap[path]}"/>" id="<c:out value="${path}"/>" size="8"/>
                                            <button type="reset" id="<c:out value="${path}"/>Button">...</button>
                                            <script type="text/javascript">
                                                Calendar.setup({
                                                inputField     :    "<c:out value="${path}"/>",
                                                ifFormat       :    "%Y-%m-%d",
                                                button         :    "<c:out value="${path}"/>Button",
                                                step           :    1
                                                });
                                            </script>								
                                        </td>
                                        <td class="error"><c:out value="${status.errorMessage}"/></td>
                                    </tr>
                                </spring:bind>
                            </c:forTokens>
                        </c:if>
                    </c:forTokens>
                </table>
            </td>
        </c:if>

        <c:if test="${empty selectedTrackerId}">
            <td>
                <table>
                    <tr><th <c:if test="${selectedMap['trackerId']}">class="selected"</c:if>>Tracker</th></tr>
                    <tr>				
                        <td>
                            <select name="trackerIdArray" size="8" multiple="true">
                                <c:set var="trackerMap" value="${filterMap['trackerId']}"/>
                                <c:forEach items="${userSession.trackerIds}" var="trackerId">	
                                    <option value="<c:out value="${trackerId}"/>" <c:if test="${trackerMap[trackerId]}">selected="true"</c:if>>
                                        <c:out value="${trackerId}"/>
                                    </option>
                                </c:forEach>				
                            </select>
                        </td>
                    </tr>
                </table>
            </td>
        </c:if>

        <td>
            <table>
                <tr>
                    <th <c:if test="${!empty itemFilter.title}">class="selected"</c:if>>Title</th>
                    <td><input name="title" value="<c:out value="${itemFilter.title}"/>"/></td>
                </tr>
                <c:if test="${!empty selectedTrackerId}">
                    <c:forTokens items="cusStr01,cusStr02,cusStr03,cusStr04,cusStr05" delims="," var="name">
                        <c:set var="field" value="${metadata[name]}"/>
                        <c:if test="${!empty field}">
                            <tr>
                                <th <c:if test="${!empty filterMap[name]}">class="selected"</c:if>>
                                    <c:out value="${field.label}"/>
                                </th>
                                <td>
                                    <input name="<c:out value="${name}"/>" value="<c:out value="${filterMap[name]}"/>"/>
                                </td>
                                <td/>
                            </tr>
                        </c:if>
                    </c:forTokens>
                </c:if>
            </table>
        </td>
        <td>
            <input type="submit" name="query" value="Query" id="focus"/>
        </td>

    </tr>
</table>

<input type="submit" name="_eventId_search" value="Search"/>
<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>