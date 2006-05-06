<%@ include file="/WEB-INF/jsp/header.jsp" %>

<span class="info">Subversion Repository Details</span>

<table class="bdr-collapse">
    <tr>
        <td>
            <table class="jtrac">
                <tr>
                    <th>User</th>
                    <th>Commits</th>
                </tr>
                <c:forEach items="${commitsPerCommitter}" var="entry" varStatus="row">
                    <c:set var="rowClass"><c:if test="${row.count % 2 == 0}">class="alt"</c:if></c:set>
                    <tr ${rowClass}>	
                        <td>${entry.key}</td>		
                        <td align="right">${entry.value}</td>
                    </tr>
                </c:forEach>
            </table>
        </td>
        <td valign="top">            
            <img src="chart/svn_commits_per_committer.jpg"/>
        </td>
    </tr>
</table>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>