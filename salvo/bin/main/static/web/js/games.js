$(function(){
    tableScoring();
})

function tableScoring(){
    $("#scoring").append("<thead><tr><th>'Name'</th><th>'Total'</th><th>'Won'</th><th>'Lost'</th><th>'Tied'</th></tr></thead>")
    $.get("/api/leaderboard/").done(function (datos){
        $("#scoring").append("<tbody>");
        datos.map(dato =>{
            $("#scoring").append("<tr><td>"+dato.email+"</td><td>"+dato.total+"</td><td>"+dato.win+"</td><td>"+dato.lost+"</td><td>"+dato.tied+"</td></tr>");
        })
        $("#scoring").append("</tbody>")
    }).fail(function(error){
        alert("fallo codigo"+error);
    })
   }

var app=new Vue({
    el: '#app',
    data: function(){
        return{
            login:false,
            user:'',
            pass:''
        }
    },
    methods:{
        logIn:function(event){
            $.post("/api/login",{username : this.user , pass : this.password}).done(()=>{
                login=!login;
                alert("Ha iniciado session con exito!!");
            }).fail((err)=>{
                alert(err.message);
            })
        },
        signIn:function(event){
            $.post("/api/players",{username : this.user ,pass : this.password}).done(()=>{
                login=!login;
                alert("Gracias por registrarse!!");
            }).fail((err)=>{
                alert(err.message);
            })
        },
        logOut:function(event){
            $.post("api/logout").done(()=>{
                login=!login;
                alert("Cerraste sesion con exito!!")
            }).fail((err)=>{
                alert(err);
            })
        }
    },

});


















