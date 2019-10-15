$.urlParam = function (name) {
    var results = new RegExp('[\?&]' + name + '=([^&#]*)')
        .exec(window.location.search);

    return (results !== null) ? results[1] || 0 : false;
}

const app = new Vue({
    el: "#app",
    data: {
        login: false,
        user: '',
        pass: '',
        table: []
    },
    mounted: function () {
        this.tableScoring()
    },
    methods: {
        logIn: function () {
            $.post("/api/login", { username: this.user, password: this.pass }).done(() => {
                this.login = true;
                alert("Ha iniciado session con exito!!");
                this.tableScoring();

            }).fail((err) => {
                alert(err.message);
            })
        },
        signIn: function () {
            $.post("/api/players", { username: this.user, password: this.pass }).done(() => {
                alert("Gracias por registrarse!!");
                //document.location.href="/web/games.html?this.login=true";
                //document.location.reload();
                this.logIn(event);
            }).fail((err) => {
                alert(err.message);
            })
        },
        logOut: function () {
            $.post("/api/logout").done(() => {
                this.login = false;
                alert("Cerraste sesion con exito!!")
                //document.location.href="/web/games.html?this.login=false";
                this.tableScoring();

            }).fail((err) => {
                alert(err.message);
            })
        },
        tableScoring: function () {
            this.table=[{total: 1.5, tied: 1, lost: 0, win: 1, email: "j.bauer@ctu.gov"},
            {total: 0.5, tied: 1, lost: 0, win: 0, email: "c.obrian@ctu.gov"},
            {total: 0, tied: 0, lost: 0, win: 0, email: "hola@adios.com"}]
            //$("#scoring").append("<thead><tr><th>'Name'</th><th>'Total'</th><th>'Won'</th><th>'Lost'</th><th>'Tied'</th></tr></thead>")
            $.get("/api/leaderboard")
                .done( (datos)=> {
                    this.login = true;
                    this.table = datos;
                })
                .fail((error)=> {
                    this.table = "";
                    this.login = false;
                    console.log("fallo codigo" + error);
            })
        }
    },

})













