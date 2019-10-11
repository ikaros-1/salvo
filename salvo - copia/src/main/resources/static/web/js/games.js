$(function(){
    /*var id_gameplayer = $.urlParam("gp");
    maketable("#you");
    maketable("#oponent");
    if(id_gameplayer !=null){
        loadJsonShips(id_gameplayer);
    }
    else
    alert("Debe pedir un id desde la url");*/

    tableScoring();
})



/*function loadJson() {
    $.get("/api/games")
    .done(function(games) {
        games.map(function(game){
            if(game.GamePlayers.length <1 )
                $("#Lista").append("<li>"+game.id+" - "+game.created+" ");
            else{
                $("#Lista").append("<li>"+game.id+" - "+game.created+" ");
                game.GamePlayers.map(function(gameplayer){
                    $("#Lista").append(gameplayer.player.email+" ");
                })
                $("#Lista").append("</li>");
            }
        })
    })
    .fail(function( jqXHR, textStatus ) {
      alert("fallo")
    });
  }*/

  function loadJsonShips(id){
       $.get("/api/game_view/"+id,function(){
            alert("Cargo")
       })
      .done(function(games) {
            games.ships.map(function(ship){
                ship.location.map(function(loc){
                    console.log(loc)
                    $("#you > ."+loc).append("<submarine1><submarine1/>")
                })
            })
            var you=games.gameplayers.filter(gameplayer =>{return gameplayer.id==id}).shift();
            var oponent =games.gameplayers.filter(gameplayer =>{return gameplayer.id!=id}).shift();
            /*games.GamePlayers.map(function(gameplayer){
                if(gameplayer.player.id==id_Player){
                    $("#players").append(gameplayer.player.email+"(you) vs");
                }
                else{
                    oponent=gameplayer.player.email;
                }
            })
            $("#oponent").append(oponent);*/
            console.log(you);
            console.log(oponent);
            var you_shoot=games.salvo.filter(salvos=>{return salvos.player.id==you.player.id});
            var opp_shoot=games.salvo.filter(salvos=>{return salvos.player.id==oponent.player.id});
            $("#players").append(you.player.email+"(You) vs "+oponent.player.email)
            shooter(opp_shoot,'you');
            shooter(you_shoot,'oponent');
      })
      .fail(function( jqXHR, textStatus ) {
        alert("fallo")
      });
  }

  $.urlParam = function(name){
               var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
               if (results==null) {
                  return null;
               }
               return decodeURI(results[1]) || 0;
           }


   function maketable(table){
        for(var y="A";y!="K";y=String.fromCharCode(y.charCodeAt(0)+1)){
            $(table).append("<tr>")
            $(table).append("<th>"+y+"</th>")
            for(var x=1;x<11;x++){
                $(table).append('<td class="'+y+x+'"></td>')
            }
            $(table).append("</tr>")
        }

   }

   function shooter(shooters,table){
        shooters.map(shooter=>{
            shooter.locations.map(location=>{
                $('#'+table+' > .'+location).append('<shooter>'+shooter.turn+'</shooter>');
            })
        })
   }

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