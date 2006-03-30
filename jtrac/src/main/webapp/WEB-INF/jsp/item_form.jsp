<%@ include file="/WEB-INF/jsp/header.jsp" %>

<p/>

<form method="post" action="<c:url value='flow.htm'/>">

<table class="jtrac">
    
    <tr>    
        <td class="label">
            Summary
            <font color="red">*</font>
        </td>    
        <td>
            <spring:bind path="item.summary">
                <input name="${status.expression}" value="${status.value}" size="107" id="focus"/>
                <span class="error">${status.errorMessage}</span>
            </spring:bind>
        </td>
    </tr>

    <tr>    
        <td class="label">
            Detail
            <font color="red">*</font>
        </td>    
        <td>
            <spring:bind path="item.detail">
                <textarea name="${status.expression}" rows="10" cols="80">${status.value}</textarea>                
                <span class="error">${status.errorMessage}</span>
            </spring:bind>
        </td>
    </tr>           
    
    <c:set var="fields" value="${space.metadata.fields}"/>
    
    <c:forEach items="${space.metadata.fieldOrder}" var="fieldName">
        <c:set var="field" value="${fields[fieldName]}"/>
        <tr>
            <td class="label">
                ${field.label}
                <c:if test="${!field.optional}"><font color="red">*</font></c:if>              
            </td>
            <c:set var="bindPath">item.${fieldName}</c:set>
            <spring:bind path="${bindPath}">
                <td>	
                    <c:choose>
                        <c:when test="${field.name.type < 4}">
                            <select name="${status.expression}">
                                <option/>
                                <c:forEach items="${field.options}" var="entry">
                                    <option value="${entry.key}" <c:if test='${entry.key == item[fieldName]}'>selected='true'</c:if>>${entry.value}</option>
                                </c:forEach>							
                            </select>
                        </c:when>
                        <c:when test="${field.name.type == 6}">
                            <input name="${status.expression}" value="${status.value}" id="${fieldName}_field" size="8"/>
                            <button type="reset" id="${fieldName}_button">...</button>
                            <script type="text/javascript">
                                Calendar.setup({
                                    inputField     :    "${fieldName}_field",
                                    ifFormat       :    "%Y-%m-%d",
                                    button         :    "${fieldName}_button",
                                    step           :    1
                                });
                            </script>	    				
                        </c:when>		    				    		
                        <c:otherwise>
                            <input name="${status.expression}" value="${status.value}"/>
                        </c:otherwise>
                    </c:choose>
                    <span class="error">${status.errorMessage}</span>
                </td>                
            </spring:bind>
        </tr>        
    </c:forEach>
    
    <tr>    
        <td class="label">
            Assigned To
            <font color="red">*</font>
        </td>    
        <td>
            <spring:bind path="item.assignedTo">
                <select name="${status.expression}">
                    <option/>
                    <c:forEach items="${userRoles}" var="userRole">
                        <c:set var="user" value="${userRole.user}"/>
                        <option value="${user.id}" <c:if test='${user == status.expression}'>selected='true'</c:if>>${user.name}</option>
                    </c:forEach>   
                </select>
                <span class="error">${status.errorMessage}</span>
            </spring:bind>
        </td>
    </tr>  
    
    <tr>
        <td/>
        <td>
            <input type="submit" name="_eventId_submit" value="Submit"/>
            <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
        </td>
    </tr>                                                 
</table>
    
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>