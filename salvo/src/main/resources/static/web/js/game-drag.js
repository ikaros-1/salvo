const app = new Vue({
    el: "#App",
    data: {
        header: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        Column: ['Z', 'A', 'B', 'C', 'D', 'E', 'F', 'G'],
        option: {
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
    mounted:function(){
        $( '.ship' ).draggable({
            helper: 'clone'
          });
          
          $( '.square' ).droppable({
            accept: '.ship',
            hoverClass: 'hovering',
            drop: function( ev, ui ) {
              ui.draggable.detach();
              $( this ).append( ui.draggable );
            }
          });
    }



})

