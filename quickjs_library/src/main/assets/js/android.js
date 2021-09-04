importJs("js/core/view.js");
function importJs(path){
   return callAndroidMethod("importJs",path);
}
ui = Object.create(UiView);
ui.click(function(){
    toast("点击了!");
});
//ui = new UiView();
function startActivity(activity){
    return callAndroidMethod("startActivity",activity);
}

function startPage(pager){
    return callAndroidMethod("startPage_java",{"pageInfo":pager});
}
//动态权限申请
function requestPermission_java(permissions){
    return callAndroidMethod("requestPermission_java",{"permissions":permissions});
}
function toast(msg){
    return callAndroidMethod("toast",{"msg":msg});
}
function findViewById(id){
     return callAndroidMethod("findViewById",{"id":id});
}
//找到view
function findViewByTag(tag){
     return callAndroidMethod("findViewByTag",{"tag":tag});
}
//设置文本背景色
function setBackgroundColor_Java(tag,color){
     return callAndroidMethod("setBackgroundColor_Java",{"tag":tag,"color":color});
}
//设置view文本
function setText_Java(tag,text){
     return callAndroidMethod("setText_Java",{"tag":tag,"text":text});
}
//获取子视图个数
function getChildCount_Java(tag){
     return callAndroidMethod("getChildCount_Java",{"tag":tag});
}
//隐藏view
function view_hide_Java(tag){
     return callAndroidMethod("view_hide_Java",{"tag":tag});
}
//显示view
function view_show_Java(tag){
     return callAndroidMethod("view_show_Java",{"tag":tag});
}

//显示弹窗
function alert(msg){
     return callAndroidMethod("alertDialog_java",{"msg":msg});
}
//解析xml字符串
function inflateXml_Java(xmlStr){
          //  while(true) {
          //      s
          //  }
     return callAndroidMethod("inflateXml_Java",{"data":xmlStr});
}
//解析xml字符串
function parseXml_Java(xmlStr){
     return callAndroidMethod("parseXml_Java",{"xmlStr":xmlStr});
}
//获取根布局
function getContentView_Java(){
     return callAndroidMethod("getContentView_Java",{});
}

function loadStringFromFile_java(path){
     return callAndroidMethod("loadStringFromFile_java",{"path":path});
}

//从本地文件中读取字符串
function loadStringFromAssets_java(path){
     var r = callAndroidMethod("loadStringFromAssets_java",{"path":path});
     return r.data;
}
function setContentViewFromPath(path){
    inflateXml_Java(path);
}

//从资源文件中读取字符串
function setContentViewFromAssets(path){
    inflateXml_Java(path);
}

//设置页面view布局
function setContentView(path){
    inflateXml_Java(path);
    //解析xml布局
    /**var xmlDoc = parseXml_Java(str);
    if(xmlDoc!=undefined){
        //var layout = generateLayout(undefined,xmlDoc.documentElement);
        var layout = generateLayout(undefined,xmlDoc);//生成对应的view对象
        log("设置页面"+JSON.stringify(getContentView_Java()));
        layoutAndroid(getContentView_Java().tag,layout);//把对象添加到布局文件中
    }
    **/
}

//將js对象转化为可序列化的json对象
function convertToJava(view){
    if(view ==undefined){
        return undefined;
    }
    //log("最后显示所有的属性"+JSON.stringify(view));
    var v ={};
    for(var p in view){
        if(typeof(view[p])=="function"){
            log("方法"+p +"="+view[p].toString);
            v[p.toString] = view[p];
            //view[p]();
        }else{
            // p 为属性名称
            v[p] = view[p];
        }
    }
    return v;
}

/**
*生成布局对象
*/
function generateLayout(layout,element){
	var nodeTypeName ="";
	//log("element.nodeType="+element.nodeType);
	if(element.nodeType ==1){//元素,每个 XML 标签是一个元素节点
		var layout ={
				id:-1,
				tag:"",
				name:"",
				text:"",
				click:"",
				oninput:"",
				orientation:0,
				childs:[],
				src:"",
				width:"",
				height:"",
				weight:"",
				gravity:"",
				layout_gravity:"",
				backgroundColor:"",
				textColor:"",
				hint:"",
				margin:0,
				marginLeft:0,
				marginTop:0,
				marginRight:0,
				marginBottom:0,
				padding:0,
				paddingLeft:0,
				paddingTop:0,
				paddingRight:0,
				paddingBottom:0,
		};

		layout.name=element.nodeName;
		for(var i =0;i<element.attributes.length;i++){
			element.attributes[i];
			attrName = element.attributes[i]['nodeName'];
			attrValue = element.attributes[i]['nodeValue'];
			log(attrName+"="+attrValue);
			if(attrName == "id"){
				layout.id=attrValue;
			}else if(attrName == "text"){
				layout.text=attrValue;
			}else if(attrName == "click"){
				layout.click=attrValue;
			}else if(attrName == "oninput"){
                layout.oninput=attrValue;
            }else if(attrName == "tag"){
             	layout.tag=attrValue;
            }else if(attrName == "orientation"){
                layout.orientation=attrValue;
            }else if(attrName == "width"){
                layout.width=attrValue;
            }else if(attrName == "weight"){
                layout.weight=attrValue;
            }else if(attrName == "height"){
                layout.height=attrValue;
            }else if(attrName == "src"){
                layout.src=attrValue;
            }else if(attrName == "gravity"){
                layout.gravity=attrValue;
            }else if(attrName == "layout_gravity"){
                layout.layout_gravity=attrValue;
            }else if(attrName == "backgroundColor"){
                layout.backgroundColor=attrValue;
            }else if(attrName == "textColor"){
                layout.textColor=attrValue;
            }else if(attrName == "hint"){
                 layout.hint=attrValue;
             }


            else if(attrName == "margin"){
                layout.margin=attrValue;
            }else if(attrName == "marginLeft"){
                layout.marginLeft=attrValue;
            }else if(attrName == "marginTop"){
                layout.marginTop=attrValue;
            }else if(attrName == "marginRight"){
                layout.marginRight=attrValue;
            }else if(attrName == "marginBottom"){
                 layout.marginBottom=attrValue;
             }
            else if(attrName == "padding"){
                layout.padding=attrValue;
            }else if(attrName == "paddingLeft"){
                layout.paddingLeft=attrValue;
            }else if(attrName == "paddingTop"){
                layout.paddingTop=attrValue;
            }else if(attrName == "paddingRight"){
                layout.paddingRight=attrValue;
            }else if(attrName == "paddingBottom"){
                 layout.paddingBottom=attrValue;
             }
		}

		var nodes = element.childNodes;
		for(var i=0;i<nodes.length;i++){
			if(nodes[i].nodeType ==1){
				var child = generateLayout(layout,nodes[i]);
				if(child != undefined){
					console.log("第"+i+"个，元素节点："+nodes[i].nodeType+",name="+nodes[i].nodeName+",value="+nodes[i].nodeValue);
					if(layout.childs == undefined){
						layout.childs = [];
					}
					layout.childs.push(child);
				}
			}
		}
		return layout;
	}else if(element.nodeType ==2){//属性,每一个 XML 属性是一个属性节点
		nodeTypeName ="属性节点";
		//alert("属性节点,name="+element.nodeName+",value="+element.nodeValue +",attributes="+JSON.stringify(element.attributes));
		console.log("属性节点,name="+element.nodeName+",value="+element.nodeValue +",attributes="+JSON.stringify(element.attributes));
	}else if(element.nodeType ==3){//文本,包含在 XML 元素中的文本是文本节点
		nodeTypeName ="文本节点";
		//alert("文本节点,name="+element.nodeName+",value="+element.nodeValue +",attributes="+JSON.stringify(element.attributes));
		console.log("文本节点,name="+element.nodeName+",value="+element.nodeValue +",attributes="+JSON.stringify(element.attributes));
	}else if(element.nodeType ==8){//注释
		nodeTypeName ="注释节点";
		//alert("注释节点,name="+element.nodeName+",value="+element.nodeValue+",attributes="+JSON.stringify(element.attributes));
		console.log("注释节点,name="+element.nodeName+",value="+element.nodeValue+",attributes="+JSON.stringify(element.attributes));
	}else if(element.nodeType ==9){//文档,整个文档是一个文档节点
		nodeTypeName ="文档节点";
		//alert("文档节点,name="+layout.name+","+JSON.stringify(element.attributes)+",节点类型："+nodeTypeName);
		console.log("文档节点,name="+layout.name+","+JSON.stringify(element.attributes)+",节点类型："+nodeTypeName);
	}

	return undefined;
}

var eventCallMap = new Map();//方法集合
/**添加事件监听**/
function addEventListener(type,tag,call){
    if(call!=undefined){
        eventCallMap.set(tag+"_event_"+type,call);
    }
}
//事件监听
function callViewEvent(eventObject){
//log(JSON.stringify(eventObject));
      var eventType = eventObject.eventType;
      var listener = eventObject.listener;
      var params = eventObject.params;
       // jsonObject.put("tag",tag);
      //  jsonObject.put("eventType",eventType.value);
      //  jsonObject.put("listener",listener);

    //var listener = eventCallMap.get(tag+"_event_"+type);
       //log("事件回调0："+tag + ",listener="+ listener);
    if(listener!=undefined){
       var val = new Function(listener);
       var event={
           "params":params,
           do:function(){
               //log("事件回调====：do "+this.params);
               if(val!=undefined){
                   val(this.tag,this.params);
                   //log("事件回调====："+this.tag);
               }
           },
       };
       //log("事件回调2："+tag + typeof val);
       //log(val);
        if(event!=undefined){
           event.do();
        }
    }
        //delete methodCallMap[methodId];
}

/**
*控件布局
*/
function layoutAndroid(viewParentTag,layout){
	var data = addView_Java(viewParentTag,layout);
	if(data!=undefined){
	    //view 添加成功后添加监听器
        addEventListener(1,layout.tag,layout.click);
        log("回调成功"+JSON.stringify(data.tag));
        if(layout!=undefined&&layout.childs!=undefined){
            for(var i=0;i<layout.childs.length;i++){
                layoutAndroid(data.tag,layout.childs[i]);
            }
        }
	}
}

/**
*向一个view中添加指定控件
*/
function addView_Java(parentTag,v){
	//log("添加了一个view,"+JSON.stringify(v));
    return callAndroidMethod("addView_Java",{"tag":parentTag,"view":JSON.stringify(v)});
}

function addAllView_Java(parentId,v){
    return callAndroidMethod("addAllView_Java",{"tag":parentId,"view":JSON.stringify(v)});
}

/**
*调用Android原生方法异步
*/
function callAsyncAndroidMethod(methodName,args,call){
      //获取唯一methodId
      var methodId = methodName+Date.parse(new Date());
      //添加异步监听器
      addAsyncMethodCallBack(methodId,call);
      //执行Android方法
	 androidMethod(methodId,methodName,args);
}

/**
*调用Android原生方法
*/
function callAndroidMethod(methodName,args){
      //获取唯一methodId
      var methodId = methodName+Date.parse(new Date());
      //执行Android方法
	 androidMethod(methodId,methodName,args);
	 return getMethodCallBack(methodId);
}

var methodCallMap = new Map();//方法集合
var asynMethodCallMap = new Map();//异步方法集合
/**
* 添加异步回调监听
*获取method返回值
*/
function addAsyncMethodCallBack(methodId,call){
    //log("异步方法，methodId="+methodId+",call="+call);
    return asynMethodCallMap.set(methodId,call)
}

function getMethodCallBack(methodId){
    var result = methodCallMap.get(methodId);
    delete methodCallMap[methodId];
    return result;
}

/**
*添加method执行结果
*/
function addMethodCallBack(methodId,value){
    var result = methodCallMap.set(methodId,value);
    invokeAsyncMethodCallBack(methodId,value);
    return result;
}

/**
*添加method执行结果 异步方法回调
*/
function invokeAsyncMethodCallBack(methodId,value){
    var call = asynMethodCallMap.get(methodId);
    if(call!=undefined){
        //log("收到异步返回："+methodId+",data:"+value);
        call.success(value);
    }
    delete asynMethodCallMap[methodId];
}