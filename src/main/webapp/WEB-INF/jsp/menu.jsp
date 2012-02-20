<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>

<sec:authorize access="isAnonymous()">
<div class="menuItem"><a href="<c:url value="/registration" />"><spring:message code="label.menu.register"/></a></div>
</sec:authorize>
<sec:authorize access="isAnonymous()">
<div class="menuItem"><a href="<c:url value="/edit" />"><spring:message code="label.menu.login"/></a></div>
</sec:authorize>
<sec:authorize access="hasRole('ADMIN')">
<div class="menuItem"><a href="<c:url value="/editProfessions" />"><spring:message code="label.menu.professions"/></a></div>
</sec:authorize> 
<sec:authorize access="hasRole('LOGGED')">
    <div class="menuItem"><a href="<c:url value="/edit" />"><spring:message code="label.menu.profile"/></a></div>
    <div class="menuItem"><a href="<c:url value="/logout" />"><spring:message code="label.menu.logout"/></a></div>
</sec:authorize>
