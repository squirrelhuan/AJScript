//ui.doclick();
var data = {
    title:"动态标题",
    message: 'Hello MINA!',
    buttons:[1,2,3,4,5,6],
}
function click1(tag){
    //toast("点击了"+tag);
}

function textChanged(tag){
    var view2 = findViewByTag("input1");
   // toast("文本改变了"+view2.text);
}

function alertDialog(tag){
    alert(tag);
}

function addViewTest(){
    var view = findViewByTag("layout_01");
    view.addView({name:"button",click:"toast()",text:"t"});

    //var view2 = findViewByTag("btn_01");
   // var v = convertToJava(view2);
    //log(""+JSON.stringify(v));
}


function requestPermission(){
    var p = ["android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"];

    log("权限"+JSON.stringify(p));
    requestPermission_java(JSON.stringify(p));
}

function newPage(){
    var page = {
        controller:"page/main.js",
        title:"新页面",
    };
    startPage(page);
}

//toast('测试'+i);

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


function onCreatView(contentView){
    log("页面创建");
    //setContentView("<layout backgroundColor='#ff6600' tag='v1'><text id='13' text='haha'/><text >ok</text> <button id='btn_01' text='按钮1' click='click1()'/> <layout> <img text='a标签' src=''/> </layout> </layout>");
    setContentViewFromPath("page/index.xml");

    var layout_02 = findViewByTag("layout_02");
    for(var i=0;i<5;i++){
        layout_02.addView({name:"img",width:400,height:400,src:"https://timgsa.baidu.com/timg?image&amp;quality=80&amp;size=b9999_10000&amp;sec=1607231744142&amp;di=4a5b3fe7368ddff55cb8166f0f030320&amp;imgtype=0&amp;src=http%3A%2F%2Fa0.att.hudong.com%2F31%2F96%2F01300000287245125056965117563.jpg"});
    }

}