var data = {
    title:"动态标题",
    message: 'Hello MINA!',
}

function getJsValueFromData(valueName){
    if(data[valueName]!=undefined){
        return data[valueName];
    }
    var d = valueName;
    return JSON.stringify(d).tostring;
}

function onCreatView(contentView){
    log("请在你的js文件中重写onCreatView(contentView)方法");
}
function onResume() {
    log("js onResume ");
}

function onPaused(){
    log("js onPaused ");
}

function onDestory() {
    log("js onDestory ");
}