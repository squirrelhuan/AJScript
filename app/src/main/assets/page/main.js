//ui.doclick();
var data = {
    title:"动态标题",
    buttons:["a","b","c","d","e","f","g","h","i","j","k","l","m","n","o"],
}


function click1(tag){
    toast("点击了"+tag);
}

function textChanged(tag){
    var view2 = findViewByTag("input1");
    toast("文本改变了"+view2.text);
}

function alertDialog(tag){
    alert(tag);
}

function newPage(){
    var page = {
        controller:"page/main.js",
        title:"新页面",
    };
    startPage(page);
}

function onCreatView(contentView){
    //setContentView("<layout backgroundColor='#ff6600' tag='v1'><text id='13' text='haha'/><text >ok</text> <button id='btn_01' text='按钮1' click='click1()'/> <layout> <img text='a标签' src=''/> </layout> </layout>");
    setContentViewFromAssets("page/main.xml");
}

function progressChanged(value){
    toast(""+value);
}

//toast('测试'+i);
/*    var view = findViewByTag("v1");
    view.setBackgroundColor("#ff6000");
    var view2 = findViewByTag("text1");
    view2.setText("文本"+i);
    view2.setBackgroundColor("#992200");
    var count = view.getChildCount();
    log('得到控件Id：'+view.tag+","+view.tag+",count="+count);
for (var i=0;i<0;i++){
    //sleep(1000);
}
*/
//alert("加载完成");
/**
sleep(2000);
var activity = {
    controller:"page/main.js",
    view:"page/main.view",
    title:"新页面",
};
startActivity(activity);
**/