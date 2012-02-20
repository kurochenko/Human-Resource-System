<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
        <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

        var professionsEditUrl = window.location.origin + "/ajax/deleteProfession";
        var professionAddUrl = window.location.origin + "/ajax/addProfession";

        var deleteProfession = function(w){
            var el = $(w).parent();
            var id = el.find("input")[0].value;
            $.getJSON(professionsEditUrl, {"id": id}, function(data){
                console.log(data);
                el.fadeOut(300, function(){
                    el.remove();
                });
            });
        }

        var getProfessionDiv = function(uuid, name){
            return "<div class=\"profession\"><input type=\"hidden\" value=\""+uuid+"\" />"+name+"</div>";
        }

        var loadProfessionsEdit = function(){
            $.getJSON(professionsUrl, function(data){
                var el = $("#professions");
                for(var i = 0; i < data.length; i++){
                    el.append(getProfessionDiv(data[i].uuid, data[i].name));
                }
            });
        }

        var addProfession = function(){

        }

        $(document).ready(function(){
            $("#newProfessionForm").submit(function(){
                var el = $("input[name=newProfession]", this);
                $.getJSON(professionAddUrl, {"name": el.val()}, function(data){
                    if(data.status == "ok"){
                        el.val("");
                        $("#professions").append($(getProfessionDiv(data.id, data.name)).hide().fadeIn(300));
                    }else{
                        alert(data.error);
                    }
                });
                return false;
            });
        });