<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<h2><spring:message code="label.registration.title" /></h2><br/>
<div id="error"></div>
<form id="registerForm" method="post" enctype="multipart/form-data" action="<c:url value="/user/register" />" target="registerFrame">
    <label for="name"><spring:message code="label.registration.input.name" /></label><br/>
    <input type="text" id="name" name="name" /><br/><br/>
    <label for="password"><spring:message code="label.registration.input.password1" /></label><br/>
    <input type="password" id="password" name="password" /><br/><br/>
    <label for="password2"><spring:message code="label.registration.input.password2" /></label><br/>
    <input type="password" id="password2" name="password2" /><br/><br/>
    <label for="mail"><spring:message code="label.registration.input.email" /></label><br/>
    <input type="text" id="mail" name="mail" /><br/><br/>
    <label for="profession"><spring:message code="label.registration.input.profesion" /></label><br/>
    <select id="profession" name="profession">
        <option value="0">- - -</option>
    </select><br/><br/>
    <label for="pdf"><img src="img/pdf.png" alt="pdf" /><spring:message code="label.registration.input.cv.pdf" /></label><br/>
    <input type="file" id="pdf" name="pdf" /><br/><br/>
    <label for="odt"><img src="img/document.png" alt="odt" /><spring:message code="label.registration.input.cv.odt" /></label><br/>
    <input type="file" id="odt" name="odt" /><br/><br/>
    <input type="submit" id="registerSubmit" class="button" value="<spring:message code="label.registration.button" />" />
</form>
<div id="searchProgress"><spring:message code="label.loading" /></div>
<iframe id="registerFrame" name="registerFrame"></iframe>