//var delay_time = 2000;
var delay_time = 8000;
var fun_name; // = function(){};
var index = 0;

chagePic = function(){
    //alert("def");
    if( index < pics.length ){
        $("#pic_1").attr("src", pics[index]);
        index = index + 1;
    }
    else{
        //clearInterval( fun_name );
        index = 0;
    }
}

startShow = function(){
    
    if( $("#pic_1").attr("src").length == 0 ){
        chagePic();
    } 
        
    fun_name = window.setInterval(chagePic,delay_time);
}


stopShow = function(){
    clearInterval( fun_name );
}


$(function(){
    // 文档就绪
    startShow();
});

