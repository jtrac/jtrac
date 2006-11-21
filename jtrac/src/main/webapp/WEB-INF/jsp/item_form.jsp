<%@ include file="/WEB-INF/jsp/header.jsp" %>

<form method="post" action="<c:url value='/flow'/>" enctype="multipart/form-data">

<c:if test="${item.id != 0}">    
    <input type="submit" name="_eventId_delete" value="<fmt:message key='delete'/>"/><br/><br/>
</c:if>

<table class="bdr-collapse">

<tr>
<td>
    <table class="jtrac">
        <tr>    
            <td class="label">
                <fmt:message key="item_form.summary"/>
                <font color="red">*</font>
            </td>    
            <td>
                <spring:bind path="item.summary">
                    <input name="${status.expression}" value="${status.value}" size="107" id="focus"/>
                    <span class="error"><br/>${status.errorMessage}</span>                   
                </spring:bind>
            </td>
        </tr>

        <tr>    
            <td class="label">
                <fmt:message key="item_form.detail"/>
                <font color="red">*</font>
            </td>    
            <td>
                <spring:bind path="item.detail">
                    <textarea name="${status.expression}" rows="10" cols="80">${status.value}</textarea>                    
                    <span class="error"><br/>${status.errorMessage}</span>
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
                    <c:if test="${item.id == 0}">
                        <tr>    
                            <td class="label">
                                <fmt:message key="item_form.assignTo"/>
                                <font color="red">*</font>
                            </td>
                            <td>
                                <spring:bind path="item.assignedTo">                
                                    <select name="${status.expression}">
                                        <option/>
                                        <c:forEach items="${usersAbleToTransitionFrom}" var="usr">                                            
                                            <option value="${usr.user.id}" <c:if test='${usr.user.id == status.value}'>selected='true'</c:if>>${usr.user.name}</option>                                            
                                        </c:forEach>   
                                    </select>
                                    <span class="error">${status.errorMessage}</span>
                                </spring:bind>
                            </td>
                        </tr>  
                    </c:if>
                    <c:if test="${item.id != 0}">
                        <tr>    
                            <td class="label">
                                <fmt:message key="item_form.editReason"/>
                                <font color="red">*</font>
                            </td>
                            <td>
                                <textarea name="comment" rows="5" cols="40">${comment}</textarea>
                                <c:if test="${!empty commentError}">
                                    <span class="error"><fmt:message key='error.empty'/></span>
                                </c:if>
                            </td>
                        </tr>                        
                    </c:if>
                    <tr>
                        <td/>
                        <td>
                            <input type="submit" name="_eventId_submit" value="<fmt:message key='submit'/>"/>
                            <input type="checkbox" name="sendNotifications" value="true" <c:if test="${item.sendNotifications}">checked="true"</c:if>/>
                            <fmt:message key="item_form.sendNotifications"/>
                            <input type="hidden" name="_sendNotifications"/>
                            <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                            <input type="hidden" name="itemId" value="${item.id}"/>
                        </td>
                    </tr>
                </table>

            </td>
            <c:if test="${item.id == 0}">
                <td align="right">
                    <table class="jtrac">
                        <tr><th><fmt:message key="item_form.notifyByEmail"/></th></tr>
                        <tr>
                            <td>
                                <spring:bind path="item.itemUsers">
                                    <jtrac:multiselect name="${status.expression}" list="${userSpaceRoles}" selected="${status.value}"/>
                                </spring:bind>
                            </td>
                        </tr>
                        <tr><th><fmt:message key="item_form.attachment"/></th></tr>
                        <tr><td><input type="file" name="file" size="15"/></td></tr>
                    </table>
                </td>
            </c:if>
        </tr>
    </table>
    </td>
</tr>

</table>
    
<c:if test="${item.id != 0}">
    <input type="submit" name="_eventId_cancel" value="<fmt:message key='cancel'/>"/>
</c:if>

</form>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>