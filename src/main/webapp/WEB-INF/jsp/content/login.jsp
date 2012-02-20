<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<h2><spring:message code="label.login.title" /></h2><br/>
<div id="error">${SPRING_SECURITY_LAST_EXCEPTION.message}<br /><br /></div>
<form id="loginForm" method="post" name="f" action="/j_spring_security_check">
    <label for="login"><spring:message code="label.login.input.login" /></label><br/>
    <input type="text" id="login" name="j_username" /><br/><br/>
    <label for="password"><spring:message code="label.login.input.password" /></label><br/>
    <input type="password" id="password" name="j_password" /><br/><br/>
    <input type="submit" name="submit" class="button" value="<spring:message code="label.login.button" />" />
</form>
<div id="searchProgress"><spring:message code="label.login.button" /></div>