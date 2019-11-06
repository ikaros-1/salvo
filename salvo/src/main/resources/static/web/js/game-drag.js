const app=new Vue({
    el:"#App",
    data:{
        header:[0,1,2,3,4,5,6,7,8,9,10],
        Column:['A','B','C','D','E','F','G']
    },
    watch:{
        'headers':function(){
            console.log("hola")
        }
    }



})

