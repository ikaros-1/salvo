const app = new Vue({
  el: "#app",
  data: {
    ship:false
  },
  methods:{
    sendShips:()=>{
      var ships=[]
      var typeships=[{name:"carrier",lenght:5},{name:"battleship",lenght:4},{name:"submarine",lenght:3},{name:"destroyer",lenght:3},{name:"patrol_boat",lenght:2}];
      typeships.map((type)=>{
        ships.push({typeShip:{name:(type.name[0].toUpperCase()+type.name.slice(1)),lenght:type.lenght},locations:[changeLocation($("#"+type.name)[0].dataset)]});
      })
      
      console.log(ships)
      $.ajax({
        url: '/api/games/players/5/ships',
        contentType: 'application/json',
        type: 'POST',
        data: JSON.stringify(ships),
        datatype: 'json'
      })
      .done(function (data) { console.log(data); })
      .fail(function (jqXHR, textStatus, errorThrown) { console.log(errorThrown) });
    }
  }

})


function changeLocation(data){
  var locations=[];
  if(parseInt(data.gsWidth)<parseInt(data.gsHeight)){
    for(var i=0;i<data.gsHeight;i++){
      locations.push((String.fromCharCode(parseInt(data.gsY)+65+i))+(parseInt(data.gsX+1)));
    }
  }
  else{
    for(var i=0;i<data.gsWidth;i++){
      locations.push((String.fromCharCode(parseInt(data.gsY)+65))+(parseInt(data.gsX)+i+1));
    }
  }
  return locations;
}