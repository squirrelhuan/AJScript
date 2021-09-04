package cn.demomaster.ajscript;

import androidx.annotation.Nullable;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.demomaster.ajscript.engine.rhino.RhinoJSEngine;
import cn.demomaster.huan.quickdeveloplibrary.base.activity.QDActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.PermissionManager;
import cn.demomaster.huan.quickdeveloplibrary.util.QDFileUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.xml.NodeElement;
import cn.demomaster.huan.quickdeveloplibrary.view.banner.qdlayout.AjsLayoutInflater;
import cn.demomaster.huan.quickdeveloplibrary.view.banner.qdlayout.Element;

import static cn.demomaster.huan.quickdeveloplibrary.view.banner.qdlayout.AjsLayoutInflater.generateLayout;

public class MainActivity extends QDActivity {

    Button btn_select_file;
    Button btn_test;
    EditText et_script;
    TextView tv_console;
    TextView tv_file;
    public static ViewGroup view = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getActionBarLayout().setActionBarType(ACTIONBAR_TYPE.ACTION_STACK);
        view = new LinearLayout(this);
        view.setTag(001);
        ((FrameLayout) findViewById(R.id.frame)).addView(view);
        NodeElement nodeElement = AjsLayoutInflater.parseXmlAssets(this, "test/layout_banner2.xml", null);
        if (nodeElement != null) {
            Element myElement = getElement(nodeElement);
            view.addView(generateLayout(this, null, myElement));
        }

        /*QdLayoutInflater.parseXmlAssetsForLayout(this, "config/test.xml", new QdSaxHandler.OnParseCompleteListener() {
            @Override
            public void onComplete(Context context, Element element, View rootView) {
                //QDLogger.d("parseXmlAssets=" + JSON.toJSONString(result));
                QdThreadHelper.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        View view = generateLayout(null, element);
                        ((FrameLayout) findViewById(R.id.frame)).addView(view);
                    }
                });
            }
        });*/

        tv_file = findViewById(R.id.tv_file);
        btn_test = findViewById(R.id.btn_test);
        btn_select_file = findViewById(R.id.btn_select_file);
        et_script = findViewById(R.id.et_script);
        scriptStr = QDFileUtil.getFromAssets(mContext, "js/test.js");
        et_script.setText(scriptStr);
        tv_console = findViewById(R.id.tv_console);
        tv_console.setTag("textview1");
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String str = QDFileUtil.getFromAssets(mContext, "js/android.js");
               // AjsEngine.getInstance().executeJ2V8(mContext, et_script.getText().toString());
            }
        });
        btn_select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseFile();
            }
        });

        PermissionManager.getInstance().chekPermission(mContext, PERMISSIONS_STORAGE, null);

        String str1 = runScript(JAVA_CALL_JS_FUNCTION, "Test", new String[]{});
        String str2 = runScript(JS_CALL_JAVA_FUNCTION, "Test", new String[]{});
        //String str3 = runScript(JS_CALL_JAVA_FUNCTION2, "Test", new View[]{});
        //QdToast.show(str3);
        RhinoJSEngine jsEngine = new RhinoJSEngine();
        jsEngine.runScript(testjs);
    }

    public static Element getElement(NodeElement nodeElement) {
        Element myNodeElement = new Element();
        myNodeElement.setTag(nodeElement.getNodeName());
        List<NodeElement.NodeProperty> atts = nodeElement.getAttributes();
        if (atts != null) {
            for (int i = 0; i < atts.size(); i++) {
                String attsQName = atts.get(i).getName();
                String value = atts.get(i).getValue();
                //QDLogger.i("元素: attsQName=" + attsQName + ",value=" + value);
                myNodeElement.addProperty(attsQName, value);
            }
        }
        for (NodeElement element1 : nodeElement.getChildNodes()) {
            myNodeElement.addNode(getElement(element1));
        }
        return myNodeElement;
    }

    private String testjs ="var val = getValue('testKey');" +
            "setValue('setKey',val);"+
            "var test = getObjectValue('objectKey');" +
                    "setValue('testvalue',test.name);";

    /**
     * Java执行js的方法
     */
    private static final String JAVA_CALL_JS_FUNCTION = "function Test(){ return '农民伯伯 java call js Rhino'; }";
    /**
     * js调用Java中的方法
     */
    private static final String JS_CALL_JAVA_FUNCTION = "var ScriptAPI = java.lang.Class.forName(\"" + MainActivity.class.getName()
            + "\", true, javaLoader);"
            + "var methodRead = ScriptAPI.getMethod(\"jsCallJava\", [java.lang.String]);"
            + "function jsCallJava(url) {return methodRead.invoke(null, url);}"
            + "function Test(){ return jsCallJava(); }";
    private static final String JS_CALL_JAVA_FUNCTION2 =
             "function Test(){ var id = view;" +
                     //"view.findViewById" +
                     "return id; }"+
                     "";

    /**
     * 执行JS
     * @param js js代码
     * @param functionName js方法名称
     * @param functionParams js方法参数
     * @return
     */
    public String runScript(String js, String functionName, Object[] functionParams) {
        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        try {
            Scriptable scope = rhino.initStandardObjects();
            ScriptableObject.putProperty(scope, "javaContext", Context.javaToJS(MainActivity.this, scope));
            ScriptableObject.putProperty(scope, "javaLoader", Context.javaToJS(MainActivity.class.getClassLoader(), scope));
            ScriptableObject.putProperty(scope, "view", Context.javaToJS(view, scope));
            rhino.evaluateString(scope, js, "MainActivity", 1, null);
            Function function = (Function) scope.get(functionName, scope);
            Object result = function.call(rhino, scope, scope, functionParams);
            if (result instanceof String) {
                return (String) result;
            } else if (result instanceof NativeJavaObject) {
                return (String) ((NativeJavaObject) result).getDefaultValue(String.class);
            } else if (result instanceof NativeObject) {
                return (String) ((NativeObject) result).getDefaultValue(String.class);
            }
            return result.toString();//(String) function.call(rhino, scope, scope, functionParams);
        } finally {
            Context.exit();
        }
    }

    public static String jsCallJava(String url) {
        return "农民伯伯 js call Java Rhino";
    }

    // 权限
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    String scriptStr = " ";

    public void choseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 57312);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 57312) {//指定文件路径
            Uri uri = data.getData();
            if (uri != null) {
                String path = QDFileUtil.getFilePathByUri(mContext, uri);
                if (path != null) {
                    File file = new File(path);
                    if (file.exists()) {
                        String upLoadFilePath = file.toString();
                        String upLoadFileName = file.getName();
                        tv_file.setText(upLoadFilePath);
                        try {
                            et_script.setText(QDFileUtil.readFileSdcardFile(upLoadFilePath));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("file not found");
                    }
                }
            }
        }
    }
}
