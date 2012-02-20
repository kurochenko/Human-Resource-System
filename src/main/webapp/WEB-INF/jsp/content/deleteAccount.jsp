<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<h2><spring:message code="label.delete.title" /></h2><br/>
<span><spring:message code="label.delete.question" /></span><br/><br/>
<a href="#" id="deleteAccount"><spring:message code="label.delete.answer" /></a>
<script>
    $("#deleteAccount").click(function(){
        $.post("<c:url value="/user/deleteAccount" />", {}, function(data){
            if(data.error=="ok"){
                window.location.href="<c:url value="/logout" />";
            }else{
                alert(data.error);
            }
        }, "json")
    });
</script>