<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:url value="/gen/js/edit.js" var="edit" />
<script type="text/javascript" src="${edit}"></script>

<h2><spring:message code="label.edit.title" /></h2><br/>
<div id="error"></div>
<div id="searchProgress"><spring:message code="label.loading" /></div><br/>
    <span><spring:message code="label.edit.input.name" /></span><br/>
    <input type="text" name="name" /><br/><br/>
    <input type="button" id="changeNameB" value="<spring:message code="label.edit.button.name" />" class="button"><hr/>
    <span><spring:message code="label.edit.input.oldpw" /></span><br/>
    <input type="password" name="password0" /><br/><br/>
    <span><spring:message code="label.edit.input.password1" /></span><br/>
    <input type="password" name="password" /><br/><br/>
    <span><spring:message code="label.edit.input.password2" /></span><br/>
    <input type="password" name="password2" /><br/><br/>
    <input type="button" id="changePasswordB" value="<spring:message code="label.edit.button.password" />" class="button"><hr/>
    <span><spring:message code="label.edit.input.profession" /></span><br/>
    <select name="profession">
        <option value="0">- - -</option>
    </select><br/><br/>
    <input type="button" id="changeProfessionB" value="<spring:message code="label.edit.button.profession" />" class="button"><hr/>
    <form id="newCVForm" method="post" enctype="multipart/form-data" action="./user/changeCV" target="newCV">
    <label for="pdf"><img src="<c:url value="img/pdf.png" />" alt="pdf" /> <spring:message code="label.edit.input.cv.pdf" /></label><br/>
    <input type="file" name="pdf" /><br/><br/>
    <label for="odt"><img src="<c:url value="img/document.png" />" alt="odt" /> <spring:message code="label.edit.input.cv.odt" /></label><br/>
    <input type="file" name="odt" /><br/><br/>
    <input type="submit" id="registerSubmit" class="button" value="<spring:message code="label.edit.button.cv" />" />
</form>
<iframe id="newCVFrame" name="newCV"></iframe>
<hr/><br/>
<span><a href="<c:url value="/deleteAccount" />"><spring:message code="label.edit.user.delete" /></a></span>