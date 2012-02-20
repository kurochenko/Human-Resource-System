<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

    <head>
        <tiles:insertAttribute name="includes" />
        <c:set var="tilesTitle">
            <tiles:insertAttribute name="title" ignore="true" />
        </c:set>
        <title><spring:message code="${tilesTitle}" /> – <spring:message code="label.title" /></title>

    </head>
    <body<tiles:insertAttribute name="onload" ignore="true" />>
        <div id="languages">
            <a href="<c:url value="?lang=en" />"><img src="<c:url value="/img/english.jpg" />" alt="english"/></a>
            <a href="<c:url value="?lang=cs" />"><img src="<c:url value="/img/czech.jpg" />" alt="cesky"/></a>
            <a href="<c:url value="?lang=de" />"><img src="<c:url value="/img/german.jpg" />" alt="deutsch"/></a>
            <a href="<c:url value="?lang=sk" />"><img src="<c:url value="/img/slovak.jpg" />" alt="slovensky"/></a>
        </div>
        <div id="wrap">
            <div id="top">
                <div id="logo"><img src="<c:url value="/img/logo.png" />" alt="logo" /></div>
                <h1 id="title"><a href="<c:url value="/" />"><spring:message code="label.title" /></a></h1>
            </div>
            <div id="menu">
                <div class="right">
                    <tiles:insertAttribute name="menu" />
                </div>
            </div>
            <div id="main">
                <tiles:insertAttribute name="body" />
            </div>
        </div>
    </body>
</html>