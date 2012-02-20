<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:url value="/gen/js/admin.js" var="admin" />
<script type="text/javascript" src="${admin}"></script>

<h2><spring:message code="label.professions.title" /></h2><br/>
<div id="professions">
</div>
<br/>
<form id="newProfessionForm">
    <label for="profession"><spring:message code="label.professions.intput.profession" /></label><br/>
    <input id="profession" type="text" name="newProfession" />
    <input type="submit" class="button" value="<spring:message code="label.professions.button" />" />
</form>