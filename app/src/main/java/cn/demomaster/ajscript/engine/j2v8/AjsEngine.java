package cn.demomaster.ajscript.engine.j2v8;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eclipsesource.v8.JavaVoidCallback;
import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.demomaster.ajscript.AppletsFragment;
import cn.demomaster.ajscript.MyApp;
import cn.demomaster.ajscript.R;
import cn.demomaster.ajscript.core.ViewHelper;
import cn.demomaster.ajscript.model.JsElement;
import cn.demomaster.ajscript.qdlayout.MyNodeElement;
import cn.demomaster.huan.quickdeveloplibrary.helper.PermissionManager;
import cn.demomaster.huan.quickdeveloplibrary.helper.QdThreadHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.QdToast;
import cn.demomaster.huan.quickdeveloplibrary.model.EventMessage;
import cn.demomaster.huan.quickdeveloplibrary.util.ClipboardUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.QDFileUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.xml.DomSaxHandler;
import cn.demomaster.huan.quickdeveloplibrary.util.xml.NodeElement;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDDialog;
import cn.demomaster.qdlogger_library.QDLogger;

public class AjsEngine implements ViewHelper.EventBandding {
    Context context;

    public AjsEngine(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);
    }

    MyRunnable myRunnable = null;

    public void onResume() {
        myRunnable.executeScript("onResume();");
    }

    public void onPaused() {
        myRunnable.executeScript("onPaused();");
    }

    public void onDestory() {
        myRunnable.executeScript("onDestory();");
    }

    public void startControll(AppletsFragment fragment, View contentView, String scriptStr) {
        myRunnable = new MyRunnable() {
            @Override
            public void run() {
                String str = QDFileUtil.getFromAssets(context, "js/core/view.js") +
                        // " " + QDFileUtil.getFromAssets(context, "js/convertUtil.js") +
                        " " + QDFileUtil.getFromAssets(context, "js/android.js") +
                        " " + QDFileUtil.getFromAssets(context, "js/controller.js");
                v8 = V8.createV8Runtime();
                v8.getLocker().acquire();
                //反射方式注册
        /*V8Object v8Console = new V8Object(v8);
        v8.add("console", v8Console);
        v8Console.registerJavaMethod(console, "log", "jlog", new Class<?>[] { String.class });
        v8Console.release();*/

                //接口注册全局方法
                JavaVoidCallback callback0 = new JavaVoidCallback() {
                    @Override
                    public void invoke(final V8Object receiver, final V8Array parameters) {
                        String methodId = (String) parameters.get(0);
                        String methodName = (String) parameters.get(1);
                        Log.i("AJS", "回调Android方法：" + methodName);
                        V8Object v8Object = ((V8Object) parameters.get(2));

                        if (methodName.equals("requestPermission_java")) {//权限申请
                            if (v8Object.contains("permissions")) {
                                List<String> permissions = JSON.parseArray(v8Object.get("permissions") + "", String.class);
                                String[] strings = new String[permissions.size()];
                                permissions.toArray(strings);
                                //QDLogger.e(Arrays.toString(strings));
                                PermissionManager.getInstance().chekPermission(context, strings, null);
                            }
                        }
                        if (methodName.equals("alertDialog_java")) {
                            String[] msgs = new String[1];
                            if (v8Object.contains("msg")) {
                                msgs[0] = v8Object.get("msg") + "";
                            }
                            QdThreadHelper.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (v8.getLocker().hasLock()) {
                                        v8.getLocker().release();
                                    }
                                    //if (v8Object.contains("msg")) {
                                    new QDDialog.Builder(context).setTitle(msgs[0]).create().show();
                                    //}
                                }
                            });
                        } else if (methodName.equals("setBackgroundColor_Java")) {
                            if (v8Object.contains("color")) {
                                Object object = v8Object.get("color");
                                int color = Color.parseColor(object + "");
                                View view = ViewHelper.findViewByTag(contentView, v8Object.get("tag") + "");
                                if (view != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (v8.getLocker().hasLock()) {
                                                v8.getLocker().release();
                                            }
                                            view.setBackgroundColor(color);
                                        }
                                    });
                                    if (!v8.getLocker().hasLock()) {
                                        v8.getLocker().acquire(); // 释放主线程的锁
                                    }
                                }
                            }
                        } else if (methodName.equals("getContentView_Java")) {
                            JsElement v = new JsElement(contentView);
                            Log.e("AJS", "getContentView_Java：" + v.toJsString());
                            returnValue(methodId, v.toJsString());
                        } else if (methodName.equals("setText_Java")) {
                            // Log.i("AJS", "setTextJava：" + v8Object.get("text") + ",tag=" + v8Object.get("tag"));
                            if (v8Object.contains("text")) {
                                View view = ViewHelper.findViewByTag(contentView, v8Object.getString("tag"));
                                String text = v8Object.getString("text");
                                if (view != null && view instanceof TextView) {
                                    TextView textView = (TextView) view;
                                    QdThreadHelper.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (v8.getLocker().hasLock()) {
                                                v8.getLocker().release();
                                            }
                                            textView.setText(text);
                                        }
                                    });
                                }
                            }
                        } else if (methodName.equals("getChildCount_Java")) {
                            // Log.i("AJS", "getChildCount_Java：tag=" + v8Object.get("tag"));
                            if (v8Object.contains("tag")) {
                                View view = ViewHelper.findViewByTag(contentView, v8Object.get("tag") + "");
                                if (view != null) {
                                    int count = 0;
                                    if (view instanceof ViewGroup) {
                                        count = ((ViewGroup) view).getChildCount();
                                    }
                                    Log.i("AJS", "getChildCount_Java：" + view.getClass().getName() + ",count=" + count);
                                    returnValue(methodId, count);
                                }
                            }
                        } else if (methodName.equals("parseXml_Java")) {
                            Log.i("AJS", "parseXml_Java");
                            if (v8Object.contains("xmlStr")) {
                                String xmlStr = v8Object.getString("xmlStr");
                                MyNodeElement element = ViewHelper.parseXml(context, xmlStr);
                                JsElement viewModel = new JsElement(element);
                                returnValue(methodId, viewModel.toJsString());
                            }
                        } else if (methodName.equals("inflateXml_Java")) {//
                            Log.i("AJS", "inflateXml_Java");
                            if (v8Object.contains("data")) {
                                String data = v8Object.getString("data");
                                QDLogger.i("data=" + data);
                                String xmlStr = QDFileUtil.getFromAssets(context, data);
                                //xmlStr = ViewHelper.dynamicAttributes(AjsEngine.this, xmlStr);
                                Object[] waitObj = new Object[]{true, ""};
                                MyNodeElement element = ViewHelper.parseXml(context, xmlStr);
                                MyNodeElement element1 = ViewHelper.buildAttributes(AjsEngine.this, element);
                                QdThreadHelper.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        releaseLock();
                                        ViewHelper.inflateXml(context, (ViewGroup) contentView, element1, AjsEngine.this);
                                        waitObj[0] = false;
                                    }
                                });

                                while ((boolean) waitObj[0]) {
                                    try {
                                        Thread.sleep(1);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                returnValue(methodId, JSON.toJSONString(element1));
                            }
                        } else if (methodName.equals("addView_Java")) {//添加单个view
                            String tag = v8Object.get("tag") + "";
                            //Log.i("AJS", "addView_Java：tag=" +tag);
                            Log.i("AJS", "addView_Java：" + v8Object.get("view") + ",tag=" + tag);
                            if (v8Object.contains("view")) {
                                View view = ViewHelper.findViewByTag(contentView, tag);
                                String viewJson = v8Object.get("view") + "";
                                Log.i("AJS", "向" + tag + "(" + view + ")添加控件");
                                if (view != null && view instanceof ViewGroup) {
                                    final Object[] viewJsStr = {-1, ""};
                                    QdThreadHelper.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            releaseLock();
                                            //Log.i("AJS", "添加控件2：" + viewJson);
                                            JSONObject jsonObject = JSONObject.parseObject(viewJson);
                                            //TODO js 传递的view对象，需要做转换
                                            ViewHelper.inflateXml(context, (ViewGroup) view, jsonObject, AjsEngine.this);
                                            viewJsStr[0] = -0;
                                        }
                                    });
                                    while ((int) viewJsStr[0] == -1) {
                                        try {
                                            Thread.sleep(1);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (viewJsStr[1] != null && viewJsStr[1] instanceof JsElement) {
                                        JsElement v = (JsElement) viewJsStr[1];
                                        returnValue(methodId, v.toJsString());
                                    }
                                }
                            }
                        } else if (methodName.equals("loadStringFromAssets_java")) {
                            if (v8Object.contains("path")) {
                                String path = v8Object.getString("path");
                                String resultString = QDFileUtil.getFromAssets(context, path);
                                //JSONObject jsonObject = JSONObject.parseObject(o);
                                Map<String, String> map = new HashMap<>();
                                map.put("data", resultString);
                                returnValue(methodId, JSON.toJSONString(map));
                            }
                        }else if (methodName.equals("startPage_java")) {
                            if (v8Object.contains("pageInfo")) {
                                V8Object pageInfo = (V8Object) v8Object.get("pageInfo");
                                String controller = pageInfo.get("controller") + "";
                                String title = pageInfo.get("title") + "";
                                Bundle bundle = new Bundle();
                                bundle.putString("controller", controller);
                                bundle.putString("title", title);
                                QDLogger.i("bundle=", bundle);
                                fragment.startFragment(bundle);
                            }
                        }

                        switch (methodName) {
                            case "findViewById":
                                ViewGroup viewGroup = ((Activity) context).findViewById(R.id.ll_content);
                                View view1 = ViewHelper.findViewByTag(viewGroup, "001");
                                returnValue(methodId, view1);
                                break;
                            case "findViewByTag":
                                View view2 = ViewHelper.findViewByTag(contentView, ((V8Object) parameters.get(2)).get("tag") + "");
                                if (view2 != null) {
                                    JsElement v = new JsElement(view2);
                                    returnValue(methodId, v.toJsString());
                                }
                                break;
                            case "toast":
                                QdToast.show(MyApp.getInstance().getApplicationContext(), ((V8Object) parameters.get(2)).get("msg") + "", Toast.LENGTH_SHORT);
                                break;
                            case "importJs":
                                break;
                            case "startActivity":
                                Log.i("AJS", "parameters：" + v8Object.toString());
                                Log.i("AJS", "parameters：" + v8Object.get("controller"));
                                Log.i("AJS", "parameters：" + v8Object.get("title"));
                                String controller = v8Object.get("controller").toString();
                                String title = v8Object.get("title").toString();
                                /*Intent intent = new Intent(context, MiniActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("title", title);
                                bundle.putString("controller", controller);
                                intent.putExtras(bundle);
                                context.startActivity(intent);*/
                                break;
                        }

                        for (int i = 0; i < parameters.length(); i++) {
                            if (parameters.get(i) instanceof Releasable) {
                                ((Releasable) parameters.get(i)).release();
                            }
                        }
                    }
                };
                v8.registerJavaMethod(callback0, "androidMethod"); // 注册到 js 全局函数，函数名为 `print`

                //接口注册全局方法
                JavaVoidCallback callback2 = new JavaVoidCallback() {
                    @Override
                    public void invoke(final V8Object receiver, final V8Array parameters) {
                        Log.i("AJSLog", parameters.get(0).toString());
                    }
                };
                v8.registerJavaMethod(callback2, "log"); // 注册到 js 全局函数，函数名为 `print`

                //接口注册全局方法
                JavaVoidCallback callback3 = new JavaVoidCallback() {
                    @Override
                    public void invoke(final V8Object receiver, final V8Array parameters) {
                        try {
                            Thread.sleep(Long.valueOf("" + parameters.get(0)));
                            if (!v8.getLocker().hasLock()) {
                                v8.getLocker().acquire(); // 释放主线程的锁
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                v8.registerJavaMethod(callback3, "sleep"); // 注册到 js 全局函数，函数名为 `print`
                v8.executeVoidScript(str + scriptStr);

                V8Array v8Array = new V8Array(v8);
                v8Array.push(new JsElement(contentView).toJsString());
                v8.executeFunction("onCreatView", v8Array);
                v8Array.release();
                V8Object v8Object = (V8Object) v8.get("data");
                //String message = (String) v8Object.get("message");
                //Log.e("AJS", "j2v8环境变量message："+message);
                Log.e("AJS", "j2v8环境变量object："+JSON.toJSONString(v8Object));
                ClipboardUtil.setClip(context, JSON.toJSONString(v8));
                //Log.e("AJS", "j2v8环境变量："+JSON.toJSONString(v8));
                while (true) {
                    if (jsCodeList.size() > 0) {
                        try {
                            CodeBean codeBean = jsCodeList.get(0);
                            if (codeBean != null) {
                                if (codeBean.isFunction) {//执行js方法
                                    V8Array parameters = new V8Array(v8);
                                    if(codeBean.getParams()!=null)
                                    for (Object obj : codeBean.getParams()) {
                                        parameters.push(obj);
                                    }
                                    QDLogger.println("执行："+codeBean.getFunctionName() +","+ JSON.toJSONString(codeBean.getParams()));
                                    Object r = v8.executeFunction(codeBean.getFunctionName(), parameters);
                                    if(codeBean.getFunctionName().startsWith("getJsValueFromData2")){
                                        v8.removeExecutor((V8Object) v8.get(codeBean.getFunctionName()));
                                        ((V8Object) v8.get(codeBean.getFunctionName())).release();
                                        QDLogger.println("codeBean.getFunctionName()=" + v8.get(codeBean.getFunctionName()));
                                    }
                                    QDLogger.println("FUNCTION执行结果：" + r);
                                    parameters.isReleased();
                                    if (codeBean.getFunctionCall() != null) {
                                        codeBean.getFunctionCall().onCall(v8, r);
                                        //codeBean.getFunctionCall().setV8(null);
                                    }
                                } else {//执行代码片段
                                    if (!TextUtils.isEmpty(codeBean.getScript())) {
                                        v8.executeVoidScript(codeBean.getScript());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        jsCodeList.remove(0);
                    }
                }
            }
        };
        new MyThread(myRunnable).start();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage message) {
        switch (message.code) {
            case 1:
                Map<String, Object> map = (Map<String, Object>) message.getObj();
                String tag = (String) map.get("tag");
                String listenerEvent = (String) map.get("event");
                //callEvent(tag, listenerEvent);
                break;
        }
    }

    @Override
    public void callViewEvent(MyRunnable.EventType eventType, String tag, String listener, Map<String, Object> map) {
        if (myRunnable != null) {
            switch (eventType) {
                case click:
                    break;
                case seek:
                    break;
                case input:
                    break;
            }
            myRunnable.callViewEvent(eventType, tag, listener);
        }
    }

    @Override
    public Object getJsValueFromData(String dataName) {
        return myRunnable.getJsValueFromData(dataName);
    }

    @Override
    public void getAnsyJsValueFromData2(String script1,String functionName,MyRunnable.FunctionCall functionCall) {
         myRunnable.getAnsyJsValueFromData2(script1,functionName,functionCall);
    }

    @Override
    public void getAnsyJsValueFromData(String dataName, MyRunnable.FunctionCall functionCall) {
         myRunnable.getAnsyJsValueFromData(dataName,functionCall);
    }
}
