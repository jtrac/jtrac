<%@ page session="true" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>

<html>
    <head>
        <title>JTrac Login</title>
        <link href="resources/jtrac.css" rel="stylesheet" type="text/css"/>
        <script>
            <!--
            function setFocus() {
            if (document.login_form.j_username.value == '') document.login_form.j_username.focus();
            else document.login_form.j_password.focus();
            }
            -->
        </script>
    </head>
    <body onLoad="setFocus()">
        
        <table width="100%" class="jtrac"><tr class="alt"><td>JTrac</td></tr></table>
        
        <hr/>
        <br/>  

        <table class="jtrac"><tr><td class="error">${message}<br/></td></tr></table>
		
        <br/>
            
        <form name="login_form" method="post" action="<c:url value='j_acegi_security_check'/>">
            <table class="jtrac">
                <tr>
                    <td class="label">Login name / email ID</td>
                    <td colspan="2">
                        <input name="j_username" value="${loginName}" size="35"/>
                    </td>
                </tr>
                <tr>
                    <td class="label">Password</td>
                    <td>
                        <input type="password" name="j_password" value="${status.value}" size="20"/>
                    </td>
                    <td align="right">
                        <input type="submit" value="Submit"/>
                    </td>
                </tr> 
                <tr>
                    <td colspan="3" align="right">
                        remember me
                        <input type="checkbox" name="_acegi_security_remember_me"/>
                    </td>
                </tr>                                              
            </table>
        </form>      
        
<%@ include file="/WEB-INF/jsp/footer.jsp" %>