package cn.demomaster.ajscript.engine.j2v8;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.demomaster.qdlogger_library.QDLogger;

public abstract class MyRunnable implements Runnable {
    V8 v8;

    public Object getJsValueFromData(String dataName) {
        /*V8Array parameters = new V8Array(v8).push(dataName);
        v8.executeFunction(,parameters);*/
        //QDLogger.e("getJsValueFromData:"+ JSON.toJSONString(codeBean));
        //jsCodeList.add(codeBean);
       /* V8Object v8Object = (V8Object) v8.get("data");
        String message = (String) v8Object.get(dataName);*/
        V8Array parameters = new V8Array(v8);
        parameters.push(dataName);
        Object r = null;
        try {
            r = v8.executeFunction("getJsValueFromData",parameters);
            QDLogger.println("getJsValueFromData执行结果："+r);
        }catch (Exception e){
            QDLogger.e("获取："+dataName+"失败");
        }
        parameters.release();
        return r;
    }

    public void getAnsyJsValueFromData(String dataName, FunctionCall functionCall){
        CodeBean codeBean = new CodeBean();
        codeBean.setFunctionName("getJsValueFromData");
        codeBean.setFunctionCall(functionCall);
        List<String> list = new ArrayList<>();
        list.add(dataName);
        codeBean.setParams(list);
        jsCodeList.add(codeBean);
    }

    public void getAnsyJsValueFromData2(String script1, String functionName, FunctionCall functionCall){
        //先执行一段代码插入一个方法
        CodeBean codeBean1 = new CodeBean();
        codeBean1.setScript(script1);
        jsCodeList.add(codeBean1);

        //再调用这个方法获取到返回值
        CodeBean codeBean = new CodeBean();
        codeBean.setFunctionName(functionName);
        codeBean.setFunctionCall(functionCall);
        //从内存中移除该方法

        //v8.removeExecutor()

        //List<String> list = new ArrayList<>();
        //list.add(functionName);
        //codeBean.setParams(list);
        jsCodeList.add(codeBean);
    }

    public static enum EventType {
        click(1),
        input(2),
        seek(3);

        private int value = 0;

        public int value() {
            return this.value;
        }

        EventType(int value) {
            this.value = value;
        }

        public static EventType getEnum(int value) {
            EventType resultEnum = null;
            EventType[] enumArray = EventType.values();
            for (int i = 0; i < enumArray.length; i++) {
                if (enumArray[i].value() == value) {
                    resultEnum = enumArray[i];
                    break;
                }
            }
            return resultEnum;
        }
    }

    void callViewEvent(EventType eventType, String tag, String listener) {
        QDLogger.i("java向js返回事件:" + tag + ",listener=" + listener);
        int index = listener.indexOf("(");
        String str1 = listener.substring(0, index + 1);
        String str2 = listener.substring(index + 1, listener.length());
        listener = str1 + "'" + tag + "'" + str2;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tag", tag);
        jsonObject.put("eventType", eventType.value);
        jsonObject.put("listener", listener);

        executeScript("callViewEvent(" + jsonObject.toJSONString() + ");");
    }

    /**
     * java向js返回数据（方法层）
     *
     * @param methodId
     * @param obj
     */
    public void returnValue(String methodId, Object obj) {
        QDLogger.println("java向js返回数据:" + methodId + ",value=" + obj);
        v8.executeVoidScript("addMethodCallBack(\"" + methodId + "\", " + obj + ");");
    }

    public void returnAnsyValue(String methodId, Object obj) {
        QDLogger.println("java向js返回数据:" + methodId + ",value=" + obj);
        executeScript("addMethodCallBack(\"" + methodId + "\", " + obj + ");");
    }

    public void releaseLock(){
        if (v8.getLocker().hasLock()) {
            v8.getLocker().release();
        }
    }

    @Override
    public void run() {
    }

    List<CodeBean> jsCodeList = new ArrayList<>();

    public void executeScript(String js) {
        if (!TextUtils.isEmpty(js)) {
            CodeBean codeBean = new CodeBean();
            codeBean.setScript(js);
            jsCodeList.add(codeBean);
        }
    }

    public static class CodeBean {
        private String script;
        private boolean hasCall;
        public boolean isFunction;
        private String functionName;
        private FunctionCall functionCall;
        private List<? extends Object> params;

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
            isFunction = true;
        }

        public List<? extends Object> getParams() {
            return params;
        }

        public void setParams(List<?extends Object> params) {
            this.params = params;
        }

        public String getScript() {
            return script;
        }

        public void setScript(String script) {
            this.script = script;
        }

        public boolean isHasCall() {
            return hasCall;
        }

        public void setHasCall(boolean hasCall) {
            this.hasCall = hasCall;
        }

        public FunctionCall getFunctionCall() {
            return functionCall;
        }

        public void setFunctionCall(FunctionCall functionCall) {
            this.functionCall = functionCall;
        }
    }

    public static abstract class FunctionCall{
        public abstract void onCall(V8 v8,Object result);
        Map<String,Object> valueMap = new LinkedHashMap<>();
        public FunctionCall(Map<String,Object> map){
            this.valueMap = map;
        }
        public Object getValue(String key){
            return valueMap.get(key);
        }

        public Map<String, Object> getValueMap() {
            return valueMap;
        }
    }

}
