<%-- this is included in both item_form.jsp and item_view_form.jsp --%>
<td>	
    <c:choose>
        <c:when test="${field.name.type < 4}">
            <select name="${status.expression}">
                <option/>
                <c:forEach items="${field.options}" var="entry">
                    <option value="${entry.key}" <c:if test='${entry.key == status.value}'>selected='true'</c:if>>${entry.value}</option>
                </c:forEach>							
            </select>
        </c:when>
        <c:when test="${field.name.type == 6}">
            <input name="${status.expression}" value="${status.value}" id="${field.name}_field" size="8"/>
            <button type="reset" id="${field.name}_button">...</button>
            <script type="text/javascript">
                Calendar.setup({
                    inputField     :    "${field.name}_field",
                    ifFormat       :    "%Y-%m-%d",
                    button         :    "${field.name}_button",
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