<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

var userUrl = "/user/getUser";
var nameUrl = "/user/changeName";
var passwordUrl = "/user/changePassword";
var professionUrl = "/user/changeProfession";
var cvUrl = "/user/changeCV";

var loadUserEdit = function(){
    $.getJSON(userUrl, function(data){
        $("input[name=name]").val(data.name);
        fillProf(data.profession);
    });
}

var fillProf = function(prof){
    var el = $("select[name=profession]");
    if(el.children().length > 1){
        $("option[value="+prof+"]", el).attr("selected", "selected")
    } else{
        setTimeout(function(){
            fillProf(prof);
        }, 50);
    }
}

var buttonsConnect = function(){
    
    $("#newCVFrame").load(processNewCVResponse);
    $("#newCVForm").submit(function(){
        $("#error").empty();
        var pdf = $("input[name=pdf]").val();
        var odt = $("input[name=odt]").val();
        var error = false;
        if(pdf == ""){
            $("#error").append("${messages.getLabel('error.label.empty.cv.pdf')}<br/><br/>");
            error = true;
        }
        if(odt == ""){
            $("#error").append("${messages.getLabel('error.label.empty.cv.odt')}<br/><br/>");
            error = true;
        }
        if(error){
            $("html, body").animate({
                scrollTop: 150
            }, 400);
            return false;
        }
        toggleSubmit("disable", ".button");
        return true;
    });
    
    $("#changeNameB").click(function(){
        $("#error").empty();
        var name = $("input[name=name]").val();
        if(name == ""){
            $("#error").append("${messages.getLabel('error.label.empty.name')}<br/><br/>");
            $("html, body").animate({
                scrollTop: 150
            }, 400);
            return false;
        }else{
            toggleSubmit("disable", ".button");
            $.post(nameUrl, {
                "name": name
            }, function(data){
                toggleSubmit("enable", ".button");
                if(data.error != "ok"){
                    $("#error").append(data.error + "<br/>");
                }else{
                    $("#error").append("<span style=\"color:green;\">${messages.getLabel('label.edit.ok.name')}</span><br/>");
                }
                $("body").animate({
                    scrollTop: 150
                }, 400);
            },"json")
        }
    });
    
    $("#changePasswordB").click(function(){
        $("#error").empty();
        var error = false;
        var old = $("input[name=password0]");
        var password = $("input[name=password]");
        var password2 = $("input[name=password2]");
        if(password.val().length < 6){
            $("#error").append("${messages.getLabel('error.label.short.password')}<br/><br/>");
            error = true;
        }
        if(password.val() != password2.val()){
            $("#error").append("${messages.getLabel('error.label.notsame.password')}<br/><br/>");
            error = true;
        }
        if(error){
            $("html, body").animate({
                scrollTop: 150
            }, 400);
            return false;
        }else{
            toggleSubmit("disable", ".button");
            $.post(passwordUrl, {
                "old": old.val(), "password": password.val()
            }, function(data){
                toggleSubmit("enable", ".button");
                if(data.error != "ok"){
                    $("#error").append(data.error + "<br/>");
                }else{
                    $("#error").append("<span style=\"color:green;\">${messages.getLabel('label.edit.ok.password')}</span><br/>");
                    old.val("");
                    password.val("");
                    password2.val("");
                }
                $("body").animate({
                    scrollTop: 150
                }, 400);
            },"json")
        }
    });
    
    $("#changeProfessionB").click(function(){
        $("#error").empty();
        var prof = $("select[name=profession]").val();
        if(prof == "0"){
            $("#error").append("${messages.getLabel('error.label.empty.profession')}<br/><br/>");
            $("html, body").animate({
                scrollTop: 150
            }, 400);
            return false;
        }else{
            toggleSubmit("disable", ".button");
            $.post(professionUrl, {
                "profession": prof
            }, function(data){
                toggleSubmit("enable", ".button");
                if(data.error != "ok"){
                    $("#error").append(data.error + "<br/>");
                }else{
                    $("#error").append("<span style=\"color:green;\">${messages.getLabel('label.edit.ok.profession')}</span><br/>");
                }
                $("body").animate({
                    scrollTop: 150
                }, 400);
            },"json")
        }
    });

}

var processNewCVResponse = function(){
    toggleSubmit("enable", ".button");
    var data = $.parseJSON($("#newCVFrame").contents().text());
    if(data.error != "ok"){
        $("#error").append(data.error + "<br/>");
    }else{
        $("#error").append("<span style=\"color:green;\">${messages.getLabel('label.edit.ok.cv')}</span><br/>");
        $("#newCVForm")[0].reset();
    }
    $("body").animate({
        scrollTop: 150
    }, 400);
}


$(document).ready(function(){
    loadProfessions();
    loadUserEdit();
    buttonsConnect();
});