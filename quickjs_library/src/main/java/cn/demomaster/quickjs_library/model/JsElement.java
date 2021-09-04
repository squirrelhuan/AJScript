package cn.demomaster.quickjs_library.model;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;
import cn.demomaster.huan.quickdeveloplibrary.util.xml.NodeElement;

public class JsElement extends MyNodeElement {

    @JSONField(serialize = false)
    private String className;
    @JSONField(serialize = false)
    private Object targetObj;

   /* public void setTargetObj(Object obj) {
        this.targetObj = obj;
    }*/

    public JsElement(Object object) {
        this.targetObj = object;
        this.className = object.getClass().getName();
        if (object instanceof MyNodeElement) {
            MyNodeElement nodeElement = (MyNodeElement) object;
            this.setAttributes(nodeElement.getAttributes());
            this.setChildNodes(nodeElement.getChildNodes());
            this.setNodeName(nodeElement.getNodeName());
            this.setNodeValue(nodeElement.getNodeValue());
            this.setNodeType(nodeElement.getNodeType());
            //this.setParent(nodeElement.getParent());

            Map<String, String> attributes = getAttributesMap();
            String tag = null;
            if (attributes.containsKey("tag")) {
                tag = attributes.get("tag") + "";
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (TextUtils.isEmpty(tag)) {
                    addProperty("tag", View.generateViewId() + "");
                }
            }
        } else if (object instanceof View) {
            View view = (View) object;
            this.addProperty("tag", view.getTag() + "");
        }
    }

    public String toJsString() {
        String method_addView = "";

        if (targetObj != null) {
            if (targetObj instanceof View) {
                addProperty("tag", ((View) targetObj).getTag() + "");
                String tag = "";
                if (getAttributesMap().containsKey("tag")) {
                    tag = getAttributesMap().get("tag") + "";
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (TextUtils.isEmpty(tag)) {
                        addProperty("tag", ((View) targetObj).getId() + "");
                    }
                    if (getAttributesMap().containsKey("tag")) {
                        tag = getAttributesMap().get("tag") + "";
                    }
                    if (TextUtils.isEmpty(tag)) {
                        addProperty("tag", View.generateViewId() + "");
                    }
                }
            }

            if (targetObj instanceof TextView) {
                setNodeName("text");
            } else if (targetObj instanceof Button) {
                setNodeName("button");
            } else if (targetObj instanceof ImageView) {
                setNodeName("img");
            } else if (targetObj instanceof ScrollView) {
                setNodeName("scroll");
            } else if (targetObj instanceof ProgressBar) {
                setNodeName("progress");
            } else if (targetObj instanceof SeekBar) {
                setNodeName("seekbar");
            } else if (targetObj instanceof LinearLayout) {
                setNodeName("layout");
            }
            if (targetObj instanceof TextView) {

            }

            if (targetObj instanceof ViewGroup) {
                method_addView = "addView:function(view){" +
                        "return addView_Java(this.tag,view);" +
                        "},";
            }
        }

        String jsonstr = "{\"name\":\""+getNodeName()+"\"}";//JSON.toJSONString(this);
        jsonstr = jsonstr.substring(0, jsonstr.length() - 1);
        for(NodeElement.NodeProperty nodeProperty: getAttributes ()){
            jsonstr +=",\""+nodeProperty.getName()+"\":";
            jsonstr += "\""+nodeProperty.getValue()+"\"";
        }

        jsonstr += ",setBackgroundColor : function(color) {\n" +
                "     setBackgroundColor_Java(this.tag,color);" +
                "  }," +
                "setText:function(text){" +
                "setText_Java(this.tag,text);" +
                "}," +

                "getChildCount:function(){" +
                "return getChildCount_Java(this.tag);" +
                "}," +

                "show:function(){" +
                "return view_show_Java(this.tag);" +
                "}," +
                "hide:function(){" +
                "return view_hide_Java(this.tag);" +
                "}," +

                method_addView +
                "}";
        //QDLogger.e("转化结果:"+jsonstr);
        return jsonstr;
    }
}
