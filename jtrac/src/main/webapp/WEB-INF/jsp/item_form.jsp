<%@ include file="/WEB-INF/jsp/header.jsp" %>

<p/>

<form method="post" action="<c:url value='flow.htm'/>" enctype="multipart/form-data">

<table class="bdr-collapse">

<tr>
<td>
    <table class="jtrac">
        <tr>    
            <td class="label">
                Summary
                <font color="red">*</font>
            </td>    
            <td>
                <spring:bind path="item.summary">
                    <input name="${status.expression}" value="${status.value}" size="107" id="focus"/>
                    <br/><span class="error">${status.errorMessage}</span>
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
                    <br/><span class="error">${status.errorMessage}</span>
                </spring:bind>
            </td>
        </tr>

    </table>
<td>
</tr>
<tr>
<td>
    <table class="bdr-collapse" width="100%">
        <tr>
            <td valign="top">
                <table class="jtrac">

                    <c:forEach items="${space.metadata.fieldList}" var="field">
                        <tr>
                            <td class="label">
                                ${field.label}
                                <c:if test="${field.optional}">&nbsp;</c:if>
                                <c:if test="${!field.optional}"><font color="red">*</font></c:if>              
                            </td>
                            <spring:bind path="item.${field.name}">
                                <%@ include file="/WEB-INF/jsp/item_form_include.jsp" %>               
                            </spring:bind>
                        </tr>        
                    </c:forEach>

                    <tr>    
                        <td class="label">
                            Assign To
                            <font color="red">*</font>
                        </td>    
                        <td>
                            <spring:bind path="item.assignedTo">                
                                <select name="${status.expression}">
                                    <option/>
                                    <c:forEach items="${userRoles}" var="userRole">
                                        <c:set var="user" value="${userRole.user}"/>
                                        <option value="${user.id}" <c:if test='${user.id == status.value}'>selected='true'</c:if>>${user.name}</option>
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

            </td>

            <td align="right">
                <table class="jtrac">
                    <tr><th>Notify By E-mail</th></tr>
                    <tr>
                        <td>
                            <spring:bind path="item.itemUsers">
                                <jtrac:multiselect name="${status.expression}" list="${userRoles}" selected="${item.itemUsers}"/>
                            </spring:bind>
                        </td>
                    </tr>
                    <tr><th>Attachment</th></tr>
                    <tr><td><input type="file" name="file"/></td></tr>
                </table>
            </td>
        </tr>
    </table>
    </td>
</tr>

</table>
    
</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>