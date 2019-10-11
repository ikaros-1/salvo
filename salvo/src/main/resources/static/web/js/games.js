
$.urlParam = function (name) {
    var results = new RegExp('[\?&]' + name + '=([^&#]*)')
                      .exec(window.location.search);

    return (results !== null) ? results[1] || 0 : false;
}
/*
function tableScoring(){
    $("#scoring").append("<thead><tr><th>'Name'</th><th>'Total'</th><th>'Won'</th><th>'Lost'</th><th>'Tied'</th></tr></thead>")
    $.get("/api/leaderboard/").done(function (datos){
        $("#scoring").append("<tbody>");
        datos.map(dato =>{
            $("#scoring").append("<tr><td>"+dato.email+"</td><td>"+dato.total+"</td><td>"+dato.win+"</td><td>"+dato.lost+"</td><td>"+dato.tied+"</td></tr>");
        })
        $("#scoring").append("</tbody>")
    }).fail(function(error){
        console.log("fallo codigo"+error);
    })
   }*/

var app=new Vue({
    el: '#app',
    created:function(){
        this.tableScoring();
    },
    data: {
          login:false,
          user:'',
          pass:''
    },
    methods:{
        logIn:function(event){
            $.post("/api/login",{username : this.user , password : this.pass}).done(()=>{
                this.login=true;
                alert("Ha iniciado session con exito!!");
                document.location.reload();

            }).fail((err)=>{
                alert(err.message);
            })
        },
        signIn:function(event){
            $.post("/api/players",{username : this.user ,pass : this.password}).done(()=>{
                alert("Gracias por registrarse!!");
                //document.location.href="/web/games.html?login=true";
                //document.location.reload();
            }).fail((err)=>{
                alert(err.message);
            })
        },
        logOut:function(event){
            $.post("/api/logout").done(()=>{
                this.login=false;
                alert("Cerraste sesion con exito!!")
                //document.location.href="/web/games.html?login=false";
                document.location.reload();
            }).fail((err)=>{
                alert(err.message);
            })
        },
        tableScoring:function(){
            $("#scoring").append("<thead><tr><th>'Name'</th><th>'Total'</th><th>'Won'</th><th>'Lost'</th><th>'Tied'</th></tr></thead>")
            $.get("/api/leaderboard/").done(function (datos){
                this.login=true;
                $("#scoring").append("<tbody>");
                datos.map(dato =>{
                    $("#scoring").append("<tr><td>"+dato.email+"</td><td>"+dato.total+"</td><td>"+dato.win+"</td><td>"+dato.lost+"</td><td>"+dato.tied+"</td></tr>");
                })
                $("#scoring").append("</tbody>")
            }).fail(function(error){
                this.login=false;
                console.log("fallo codigo"+error);
            })
         }
    },

});


















