const app = new Vue({
  el: "#app",
  mounted(){
    this.loadgrid();
    console.log("mounted")
  },
  destroyed() {
    console.log("destoyed")
  },
  updated() {
    console.log("updated")
  },
  data: {
    ship:false,
    ships: [],
    options: {
      //matriz 10 x 10
      width: 10,
      height: 10,
      //espacio entre las celdas (widgets)
      verticalMargin: 0,
      //altura de las celdas
      cellHeight: 45,
      //inhabilita la posibilidad de modificar el tamaño
      disableResize: true,
      //floating widgets
      float: true,
      //removeTimeout: tiempo en milisegundos antes de que el widget sea removido
      //mientras se arrastra fuera de la matriz (default: 2000)
      // removeTimeout:100,
      //permite al widget ocupar mas de una columna
      //sirve para no inhabilitar el movimiento en pantallas pequeñas
      disableOneColumnMode: true,
      // en falso permite arrastrar a los widget, true lo deniega
      staticGrid: false,
      //para animaciones
      animate: true
    }
  },
  methods: {
    sendShips: () => {
      var _ships = []
      var typeships = [{ name: "carrier", lenght: 5 }, { name: "battleship", lenght: 4 }, { name: "submarine", lenght: 3 }, { name: "destroyer", lenght: 3 }, { name: "patrol_boat", lenght: 2 }];
      typeships.map((type) => {
        _ships.push({ name: (type.name[0].toUpperCase() + type.name.slice(1)), locations: changeLocation($("#" + type.name)[0].dataset) });
      })

      console.log(_ships)
      $.ajax({
        url: '/api/games/players/5/ships',
        contentType: 'application/json',
        type: 'POST',
        data: JSON.stringify(_ships),
        datatype: 'json'
      })
        .done(function (data) { console.log(data); })
        .fail(function (jqXHR, textStatus, errorThrown) { console.log(errorThrown) });
    },
    loadgrid: function (id,shoot) {
      $(id).gridstack(this.options);

      grid = $('#grid').data('gridstack');

      if(!this.ship  && !this.shoot )
        this.loadShipsDefault()
      else if(!this.shoot)
        this.loadShips()


      this.listenBusyCells('ships')
      $('.grid-stack').on('change', () => this.listenBusyCells('ships'))
    },
    loadShips: function () {
    },
    loadShipsDefault: function () {
      grid.addWidget($('<div id="patrol_boat"><div class="grid-stack-item-content patrol_boatHorizontal"></div><div/>'),
        0, 1, 2, 1);

      grid.addWidget($('<div id="carrier"><div class="grid-stack-item-content carrierHorizontal"></div><div/>'),
        1, 5, 5, 1);

      grid.addWidget($('<div id="battleship"><div class="grid-stack-item-content battleshipHorizontal"></div><div/>'),
        3, 1, 4, 1);

      grid.addWidget($('<div id="submarine"><div class="grid-stack-item-content submarineVertical"></div><div/>'),
        8, 2, 1, 3);

      grid.addWidget($('<div id="destroyer"><div class="grid-stack-item-content destroyerHorizontal"></div><div/>'),
        7, 8, 3, 1);


      //createGrid construye la estructura de la matriz
      this.createGrid(11, $(".grid-ships"), 'ships')

      //Inicializo los listenener para rotar los barcos, el numero del segundo rgumento
      //representa la cantidad de celdas que ocupa tal barco
      this.rotateShips("carrier", 5)
      this.rotateShips("battleship", 4)
      this.rotateShips("submarine", 3)
      this.rotateShips("destroyer", 3)
      this.rotateShips("patrol_boat", 2)
    },
    createGrid: function (size, element, id) {
      // definimos un nuevo elemento: <div></div>
      let wrapper = document.createElement('DIV')

      // le agregamos la clase grid-wrapper: <div class="grid-wrapper"></div>
      wrapper.classList.add('grid-wrapper')

      //vamos armando la tabla fila por fila
      for (let i = 0; i < size; i++) {
        //row: <div></div>
        let row = document.createElement('DIV')
        //row: <div class="grid-row"></div>
        row.classList.add('grid-row')
        //row: <div id="ship-grid-row0" class="grid-wrapper"></div>
        row.id = `${id}-grid-row${i}`
        /*
        wrapper:
                <div class="grid-wrapper">
                    <div id="ship-grid-row-0" class="grid-row">
 
                    </div>
                </div>
        */
        wrapper.appendChild(row)

        for (let j = 0; j < size; j++) {
          //cell: <div></div>
          let cell = document.createElement('DIV')
          //cell: <div class="grid-cell"></div>
          cell.classList.add('grid-cell')
          //aqui entran mis celdas que ocuparan los barcos
          if (i > 0 && j > 0) {
            //cell: <div class="grid-cell" id="ships00"></div>
            cell.id = `${id}${i - 1}${j - 1}`
          }
          //aqui entran las celdas cabecera de cada fila
          if (j === 0 && i > 0) {
            // textNode: <span></span>
            let textNode = document.createElement('SPAN')
            /*String.fromCharCode(): método estático que devuelve 
            una cadena creada mediante el uso de una secuencia de
            valores Unicode especificada. 64 == @ pero al entrar
            cuando i sea mayor a cero, su primer valor devuelto 
            sera "A" (A==65)
            <span>A</span>*/
            textNode.innerText = String.fromCharCode(i + 64)
            //cell: <div class="grid-cell" id="ships00"></div>
            cell.appendChild(textNode)
          }
          // aqui entran las celdas cabecera de cada columna
          if (i === 0 && j > 0) {
            // textNode: <span>A</span>
            let textNode = document.createElement('SPAN')
            // 1
            textNode.innerText = j
            //<span>1</span>
            cell.appendChild(textNode)
          }
          /*
          row:
              <div id="ship-grid-row0" class="grid-row">
                  <div class="grid-cell"></div>
              </div>
          */
          row.appendChild(cell)
        }
      }

      element.append(wrapper)
    },
    rotateShips: function (shipType, cells) {

      $(`#${shipType}`).click(function () {
        //document.getElementById("alert-text").innerHTML = `Rotaste: ${shipType}`
        console.log($(this))
        //Establecemos nuevos atributos para el widget/barco que giramos
        let x = +($(this).attr('data-gs-x'))
        let y = +($(this).attr('data-gs-y'))
        /*
        this hace referencia al elemento que dispara el evento (osea $(`#${shipType}`))
        .children es una propiedad de sólo lectura que retorna una HTMLCollection "viva"
        de los elementos hijos de un elemento.
        https://developer.mozilla.org/es/docs/Web/API/ParentNode/children
        El método .hasClass() devuelve verdadero si la clase existe como tal en el 
        elemento/tag incluso si tal elemento posee mas de una clase.
        https://api.jquery.com/hasClass/
        Consultamos si el barco que queremos girar esta en horizontal
        children consulta por el elemento contenido en "this"(tag que lanza el evento)
        ej:
        <div id="carrier" data-gs-x="0" data-gs-y="3" data-gs-width="5" 
        data-gs-height="1" class="grid-stack-item ui-draggable ui-resizable 
        ui-resizable-autohide ui-resizable-disabled">
            <div class="grid-stack-item-content carrierHorizontal ui-draggable-handle">
            </div>
            <div></div>
            <div class="ui-resizable-handle ui-resizable-se ui-icon 
            ui-icon-gripsmall-diagonal-se" style="z-index: 90; display: none;">
            </div>
        </div>
        */
        if ($(this).children().hasClass(`${shipType}Horizontal`)) {
          // grid.isAreaEmpty revisa si un array esta vacio**
          // grid.isAreaEmpty(fila, columna, ancho, alto)
          if (grid.isAreaEmpty(x, y + 1, 1, cells) || y + cells < 10) {
            if (y + cells - 1 < 10) {
              // grid.resize modifica el tamaño de un array(barco en este caso)**
              // grid.resize(elemento, ancho, alto)
              grid.resize($(this), 1, cells);
              $(this).children().removeClass(`${shipType}Horizontal`);
              $(this).children().addClass(`${shipType}Vertical`);
            } else {
              /* grid.update(elemento, fila, columna, ancho, alto)**
              este metodo actualiza la posicion/tamaño del widget(barco)
              ya que rotare el barco a vertical, no me interesa el ancho sino
              el alto
              */
              grid.update($(this), null, 10 - cells)
              grid.resize($(this), 1, cells);
              $(this).children().removeClass(`${shipType}Horizontal`);
              $(this).children().addClass(`${shipType}Vertical`);
            }


          } else {
            document.getElementById("alert-text").innerHTML = "A ship is blocking the way!"
          }

          //Este bloque se ejecuta si el barco que queremos girar esta en vertical
        } else {

          if (x + cells - 1 < 10) {
            grid.resize($(this), cells, 1);
            $(this).children().addClass(`${shipType}Horizontal`);
            $(this).children().removeClass(`${shipType}Vertical`);
          } else {
            /*en esta ocasion para el update me interesa el ancho y no el alto
            ya que estoy rotando a horizontal, por estoel tercer argumento no lo
            declaro (que es lo mismo que poner null o undefined)*/
            grid.update($(this), 10 - cells)
            grid.resize($(this), cells, 1);
            $(this).children().addClass(`${shipType}Horizontal`);
            $(this).children().removeClass(`${shipType}Vertical`);
          }

        }
      });

    },
    listenBusyCells: function (id) {
      /* id vendria a ser ships. Recordar el id de las celdas del tablero se arma uniendo 
      la palabra ships + fila + columna contando desde 0. Asi la primer celda tendra id
      ships00 */
      for (let i = 0; i < 10; i++) {
        for (let j = 0; j < 10; j++) {
          if (!grid.isAreaEmpty(i, j)) {
            $(`#${id}${j}${i}`).addClass('busy-cell').removeClass('empty-cell')
          } else {
            $(`#${id}${j}${i}`).removeClass('busy-cell').addClass('empty-cell')
          }
        }
      }
    }

  }

})


function changeLocation(data) {
  var locations = [];
  if (parseInt(data.gsWidth) < parseInt(data.gsHeight)) {
    for (var i = 0; i < data.gsHeight; i++) {
      locations.push((String.fromCharCode(parseInt(data.gsY) + 65 + i)) + (parseInt(data.gsX + 1)));
    }
  }
  else {
    for (var i = 0; i < data.gsWidth; i++) {
      locations.push((String.fromCharCode(parseInt(data.gsY) + 65)) + (parseInt(data.gsX) + i + 1));
    }
  }
  return locations;
}