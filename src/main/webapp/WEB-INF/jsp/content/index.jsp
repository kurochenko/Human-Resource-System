<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<h2><spring:message code="label.search.title" /></h2>
<div id="search">
    <form id="searchForm">
        <input type="text" id="searchBox" name="search" /><br/><br/>
        <div id="searchProgress"><spring:message code="label.loading" /></div>
        <input type="submit" id="searchSubmit" class="button" value="<spring:message code="label.search.button" />" />
    </form>
</div>
<div id="results"></div>
