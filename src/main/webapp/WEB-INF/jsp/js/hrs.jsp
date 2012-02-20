<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

        var requestUrl = "/ajax/search/";
        var professionsUrl = "/ajax/professions";
        var cvUrl = "/user/cv/";
        var registerUserUrl = "/user/register";

        var Email_regexp=/^[a-z0-9_\+-]+(\.[a-z0-9_\+-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*\.([a-z]{2,4})$/;
        var resultsPerPage = 5;

        var checkEmail = function(email){
            return Email_regexp.test(email);
        }

        var buttonsHover = function(){
            $(".button").hover(function(){
                $(this).css("background", "#414B66");
            }, function(){
                $(this).css("background", "#cb842e");
            });
        }

        var searchConnect = function(){
            $("#searchForm").submit(function(){
                if(!$("#searchSubmit").attr("disabled")){
                    startSearch($("#searchBox").val());
                }
                return false;
            });
        }

        var registerConnect = function(){
            $("#registerForm").submit(function(){
                if(!$("input[type=submit]", this).attr("disabled")){
                    return registerSubmit(this);
                }
                return false;
            });

            $("#registerFrame").load(processRegisterResponse);
        }

        var toggleSubmit = function(w, buttonId){
            var submit = $(buttonId);
            if(w=="disable" || !submit.attr("disabled")){
                submit.css("background", "#666");
                submit.attr("disabled", "disabled");
                $("#searchProgress").css("visibility", "visible");
            }else{
                submit.css("background", "#cb842e");
                submit.removeAttr("disabled");
                $("#searchProgress").css("visibility", "hidden");
            }
        }

        var startSearch = function(text){
            if(text != ""){
                toggleSubmit("disable", "#searchSubmit");
                $("#results").empty();
                setTimeout(function(){
                    $.getJSON(requestUrl + text, loaded)
                }, 700);
            }
            else toggleSubmit("enable", "#searchSubmit");
        }

        var loaded = function(data){
            toggleSubmit("enable", "#searchSubmit");
            var r = $("#results");
            var html = "";
            var pages = 0;
            if(!data) return;
            html += "<h3>${messages.getLabel('js.label.user.found')} " + data.length + " ${messages.getLabel('js.label.user.results')}</h3>"
            for(var i = 0; i < data.length; i++){
                if(i % resultsPerPage == 0){
                    pages++;
                    html += "<div class=\"resultPage\" id=\"page" + pages + "\">";
                }
                html += "<div class=\"person\"><h3>";
                html += data[i].name;
                html += "</h3>";
                html += "<i>" + data[i].profession.name + "</i><br/><br/>";
                html += "<a href=\"" + cvUrl + data[i].uuid + "/pdf\"><img src=\"img/pdf.png\" alt=\"pdf\" /> ${messages.getLabel('js.label.hrs.cv.pdf')}</a><br/><br/>";
                html += "<a href=\"" + cvUrl + data[i].uuid + "/odt\"><img src=\"img/document.png\" alt=\"odt\" /> ${messages.getLabel('js.label.hrs.cv.odt')}</a><br/><br/>";
                html += "</div>";
                if(i % resultsPerPage == 0){
                    html += "</div>";
                }
            }
            html += "<div id=\"pages\">";
            for(var i = 1; i <= pages; i++){
                html += " <a href=\"#\" class=\"pageClick\" id=\"pageClick"+i+"\" onClick=\"loadPage("+i+");return false;\">" + i + "</a> ";
            }
            html += "</div>";
            r.append(html);
            loadPage(1);
        }

        var loadPage = function(page){
            $(".pageClick").css({"pointer-events":"auto", "color":"#cb842e"});
            $(".resultPage").hide();

            $("#pageClick"+page).css({"pointer-events":"none", "color":"#666"});
            $("#page"+page).show();
            $("html, body").animate({scrollTop: 150}, 400);
        }

        var loadProfessions = function(){
            $.getJSON(professionsUrl, function(data){
                var el = $("select[name=profession]");
                for(var i = 0; i < data.length; i++){
                    el.append("<option value=\""+data[i].uuid+"\">"+data[i].name+"</option>");
                }
            });
        }

        var registerSubmit = function(w){
            $("#error").empty();
            var error = false;
            var name = $("input[name=name]", w).val();
            var password = $("input[name=password]", w).val();
            var password2 = $("input[name=password2]", w).val();
            var mail = $("input[name=mail]", w).val();
            var profession = $("select[name=profession]", w).val();
            var pdf = $("input[name=pdf]", w).val();
            var odt = $("input[name=odt]", w).val();

            if(name == ""){
                $("#error").append("${messages.getLabel('error.label.empty.name')}<br/><br/>");
                error = true;
            }
            if(password.length < 6){
                $("#error").append("${messages.getLabel('error.label.short.password')}<br/><br/>");
                error = true;
            }
            if(password != password2){
                $("#error").append("${messages.getLabel('error.label.notsame.password')}<br/><br/>");
                error = true;
            }
            if(!checkEmail(mail)){
                $("#error").append("${messages.getLabel('error.label.notmatch.email')}<br/><br/>");
                error = true;
            }
            if(profession == "0"){
                $("#error").append("${messages.getLabel('error.label.empty.profession')}<br/><br/>");
                error = true;
            }
            if(pdf == ""){
                $("#error").append("${messages.getLabel('error.label.empty.cv.pdf')}<br/><br/>");
                error = true;
            }
            if(odt == ""){
                $("#error").append("${messages.getLabel('error.label.empty.cv.odt')}<br/><br/>");
                error = true;
            }
            if(error){
                $("html, body").animate({scrollTop: 150}, 400);
                return false;
            }

            toggleSubmit("disable", "#registerSubmit");

            //var params = {};
            //params["name"] = name;
            //params["password"] = password;
            //params["mail"] = mail;
            //params["profession"] = profession;

            //$.post(registerUserUrl, params, function(data){
            // toggleSubmit("enable", "#registerSubmit");

            //  if(data.error != "ok"){
            //      $("#error").append(data.error + "<br/>");
            //      $("body").animate({scrollTop: 150}, 400);
            //  }else{


            //$("#registerForm").empty();
            //$("#main").append("<br/>Your registration was succesful! Now you can <a href=\"./login\">login</a>.");
            //      }
            // });
            return true;
        }

        var processRegisterResponse = function(){
            toggleSubmit("enable", "#registerSubmit");
            var data = $.parseJSON($("#registerFrame").contents().text());
            if(data.error != "ok"){
                $("#error").append(data.error + "<br/>");
                $("body").animate({scrollTop: 150}, 400);
            }else{
                $("#registerForm").empty();
                <c:url value="/login" var="loginUrl" />
                $("#main").append("<br/>${messages.getLabel('label.registration.success')} <a href=\"${loginUrl}\">${messages.getLabel('label.registration.success.link.login')}</a>.");
            }
        }


        $(document).ready(function(){

            searchConnect();
            buttonsHover();
            $("#searchBox").focus();
        });