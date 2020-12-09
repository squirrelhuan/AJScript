package cn.demomaster.ajscript.core;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.demomaster.ajscript.engine.j2v8.MyRunnable;
import cn.demomaster.ajscript.qdlayout.MyNodeElement;
import cn.demomaster.ajscript.view.RecycleViewAdapter;
import cn.demomaster.huan.quickdeveloplibrary.helper.QdThreadHelper;
import cn.demomaster.huan.quickdeveloplibrary.util.xml.DomSaxHandler;
import cn.demomaster.huan.quickdeveloplibrary.util.xml.NodeElement;
import cn.demomaster.huan.quickdeveloplibrary.widget.QDScrollView;
import cn.demomaster.qdlogger_library.QDLogger;

import static cn.demomaster.ajscript.fragment.TestFragment.getElement;

public class ViewHelper {

    /**
     * 事件绑定
     */
    public static interface EventBandding {
        void callViewEvent(MyRunnable.EventType eventType, String tag, String listener, Map<String, Object> map);

        Object getJsValueFromData(String dataName);

        //Object getJsValueFromData2(String dataName);
        void getAnsyJsValueFromData2(String script1, String functionName, MyRunnable.FunctionCall functionCall);

        void getAnsyJsValueFromData(String dataName, MyRunnable.FunctionCall functionCall);
    }

    public static MyNodeElement getElement(NodeElement nodeElement) {
        MyNodeElement myNodeElement = new MyNodeElement();
        myNodeElement.setNodeName(nodeElement.getNodeName());
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

    public static <T> MyNodeElement parseXml(Context context, String xmlStr) {
        XMLReader xr = null;
        try {
            //使用工厂方法初始化SAXParserFactory变量spf
            SAXParserFactory spf = SAXParserFactory.newInstance();
            //通过SAXParserFactory得到SAXParser的实例
            SAXParser sp = spf.newSAXParser();
            //通过SAXParser得到XMLReader的实例
            xr = sp.getXMLReader();
            DomSaxHandler handler = new DomSaxHandler(context);
            xr.setContentHandler(handler);
            xr.setErrorHandler(handler);

            //获取AssetManager管理器对象
            AssetManager as = context.getAssets();
            //通过AssetManager的open方法获取到beauties.xml文件的输入流
            //InputStream is = as.open(xmlPath);
            InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
            //通过获取到的InputStream来得到InputSource实例
            InputSource is2 = new InputSource(is);
            xr.parse(is2);
            NodeElement elementResult = handler.getRootElement();
            MyNodeElement myNodeElement = getElement(elementResult);
            //QDLogger.e("解析xml完成：" + JSON.toJSONString(myNodeElement));
            return myNodeElement;
        } catch (SAXException e) {
            QDLogger.e(e);
        } catch (IOException e) {
            QDLogger.e(e);
        } catch (ParserConfigurationException e) {
            QDLogger.e(e);
        }
        return null;
    }

    public static <T> MyNodeElement parseXmlStr(Context context, String xmlStr) {
        XMLReader xr = null;
        try {
            //使用工厂方法初始化SAXParserFactory变量spf
            SAXParserFactory spf = SAXParserFactory.newInstance();
            //通过SAXParserFactory得到SAXParser的实例
            SAXParser sp = spf.newSAXParser();
            //通过SAXParser得到XMLReader的实例
            xr = sp.getXMLReader();
            DomSaxHandler handler = new DomSaxHandler(context);
            xr.setContentHandler(handler);
            xr.setErrorHandler(handler);

            //InputStream is = as.open(xmlPath);
            InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
            //通过获取到的InputStream来得到InputSource实例
            InputSource is2 = new InputSource(is);
            xr.parse(is2);
            NodeElement elementResult = handler.getRootElement();
            MyNodeElement myNodeElement = getElement(elementResult);
            //QDLogger.e("解析xml完成：" + JSON.toJSONString(myNodeElement));
            return myNodeElement;
        } catch (SAXException e) {
            QDLogger.e(e);
        } catch (IOException e) {
            QDLogger.e(e);
        } catch (ParserConfigurationException e) {
            QDLogger.e(e);
        }
        return null;
    }

    public static View inflateXml(Context context, ViewGroup root, JSONObject jsonObject, EventBandding eventBandding) {
        MyNodeElement element = new MyNodeElement();
        if (jsonObject.containsKey("name")) {
            element.setNodeName(jsonObject.getString("name"));
        }
        for (Map.Entry entry : jsonObject.entrySet()) {
            element.addProperty((String) entry.getKey(), "" + entry.getValue());
        }
        return inflateXml(context, root, element, eventBandding);
    }

    /**
     * @param context       activity上下文对象
     * @param root          要添加的父窗口
     * @param element       要解析的view信息
     * @param eventBandding 某些view要绑定回调
     * @return
     */
    public static View inflateXml(Context context, ViewGroup root, MyNodeElement element, EventBandding eventBandding) {
        if (element == null || TextUtils.isEmpty(element.getNodeName())) {
            QDLogger.e("解析view失败", JSON.toJSONString(element));
            return null;
        }
        Map<String, String> attributes = element.getAttributesMap();
        View viewNew = null;
        if (attributes.containsKey("wx:for")) {
            androidx.recyclerview.widget.RecyclerView recyclerView = new RecyclerView(context);
            //这里使用线性布局像listview那样展示列表,第二个参数可以改为 HORIZONTAL实现水平展示
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 5);
            //使用网格布局展示
            recyclerView.setLayoutManager(linearLayoutManager);

            String forStr = attributes.get("wx:for") + "";
            /*str = buildAttributesString(eventBandding,str);
            QDLogger.e("wx:fpr=" + str);
            */
            RecycleViewAdapter recycleViewAdapter = new RecycleViewAdapter(context, element, eventBandding);
            recyclerView.setAdapter(recycleViewAdapter);
            viewNew = recyclerView;
        } else if ("layout".equals(element.getNodeName())) {
            viewNew = new LinearLayout(context);
            //QDLogger.e("layout attributes:" + JSON.toJSONString(attributes));
        } else if (element.getNodeName().equals("text")) {
            viewNew = new TextView(context);
        } else if (element.getNodeName().equals("button")) {
            viewNew = new Button(context);
        } else if (element.getNodeName().equals("img")) {
            viewNew = new ImageView(context);
            if (attributes.containsKey("src")) {
                String src = "" + attributes.get("src");
                if (!TextUtils.isEmpty(src)) {
                    Glide.with(context).load(src).into(((ImageView) viewNew));
                }
            }
        } else if (element.getNodeName().equals("scroll")) {
            viewNew = new QDScrollView(context);
        } else if (element.getNodeName().equals("progress")) {
            if (attributes.containsKey("style")) {
                String orientationStr = attributes.get("style") + "";
                if (orientationStr.equals("horizontal")) {
                    viewNew = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
                }
            }
            if (viewNew == null) {
                viewNew = new ProgressBar(context);
            }
        } else if (element.getNodeName().equals("seekbar")) {
            viewNew = new SeekBar(context);
            if (attributes.containsKey("minValue")) {
                int minValue = Integer.valueOf(attributes.get("minValue").toString());
                ((SeekBar) viewNew).setMin(minValue);
            }
            if (attributes.containsKey("maxValue")) {
                int maxValue = Integer.valueOf(attributes.get("maxValue").toString());
                ((SeekBar) viewNew).setMax(maxValue);
            }
            if (attributes.containsKey("value")) {
                int value = Integer.valueOf(attributes.get("value").toString());
                ((SeekBar) viewNew).setProgress(value);
            }

            if (attributes.containsKey("onseek")) {
                String onseek = attributes.get("onseek").toString();
                ((SeekBar) viewNew).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("progress", progress);
                        map.put("fromUser", fromUser);
                        eventBandding.callViewEvent(MyRunnable.EventType.seek, "" + seekBar.getTag(), onseek, map);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        } else if (element.getNodeName().equals("input")) {
            viewNew = new EditText(context);
            if (attributes.containsKey("oninput")) {
                String oninputEvent = "" + attributes.get("oninput");
                if (!TextUtils.isEmpty(oninputEvent) && eventBandding != null) {
                    ((EditText) viewNew).addTextChangedListener(new MyTextWatcher(viewNew) {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // callViewEvent(EventType.input,"" + mView.getTag(), oninputEvent);
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("s", s);
                            map.put("start", start);
                            map.put("before", before);
                            map.put("count", count);
                            eventBandding.callViewEvent(MyRunnable.EventType.input, "" + mView.getTag(), oninputEvent, map);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            //callViewEvent(EventType.input,"" + mView.getTag(), oninputEvent);
                        }
                    });
                }
            }
        } else {
            return null;
        }
        if (attributes.containsKey("tag") && attributes.get("tag") != null && !TextUtils.isEmpty(attributes.get("tag") + "")) {
            String tag = attributes.get("tag") + "";
            viewNew.setTag(tag);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                viewNew.setTag(View.generateViewId());
            }
        }
        //QDLogger.i("viewNew.tag:" + viewNew.getTag());
        if (attributes.containsKey("backgroundColor")) {
            String backgroundColorStr = attributes.get("backgroundColor") + "";
            if (!TextUtils.isEmpty(backgroundColorStr)) {
                int backgroundColor;
                try {
                    backgroundColor = Integer.valueOf(backgroundColorStr);
                } catch (Exception e) {
                    backgroundColor = Color.parseColor(backgroundColorStr);
                }
                viewNew.setBackgroundColor(backgroundColor);
            }
        }

        if (attributes.containsKey("orientation")) {
            int orientation = 0;
            String orientationStr = attributes.get("orientation") + "";
            if (orientationStr.equals("vertical")) {
                orientation = LinearLayout.VERTICAL;
            } else if (orientationStr.equals("horizontal")) {
                orientation = LinearLayout.HORIZONTAL;
            } else {
                orientation = Integer.valueOf(attributes.get("orientation"));
            }
            if (viewNew instanceof LinearLayout) {
                ((LinearLayout) viewNew).setOrientation(orientation);
            }
        }

        if (attributes.containsKey("text")) {
            if (viewNew instanceof TextView) {
                String text = (String) attributes.get("text");
                ((TextView) viewNew).setText(text);
            }
        }
        if (attributes.containsKey("click")) {
            String listenerEvent = "" + attributes.get("click");
            if (!TextUtils.isEmpty(listenerEvent) && eventBandding != null) {
                viewNew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eventBandding.callViewEvent(MyRunnable.EventType.click, "" + v.getTag(), listenerEvent, null);
                    }
                });
            }
        }
        if (attributes.containsKey("textColor")) {
            String textColorStr = (String) attributes.get("textColor");
            if (!TextUtils.isEmpty(textColorStr)) {
                int textColor = Color.parseColor(textColorStr);
                if (viewNew instanceof TextView) {
                    ((TextView) viewNew).setTextColor(textColor);
                }
            }
        }
        if (attributes.containsKey("hint")) {
            String hintStr = "" + attributes.get("hint");
            if (!TextUtils.isEmpty(hintStr)) {
                if (viewNew instanceof TextView) {
                    ((TextView) viewNew).setHint(hintStr);
                }
            }
        }
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int weight = -1;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        int padding = 0;
        int paddingLeft = 0;
        int paddingTop = 0;
        int paddingRight = 0;
        int paddingBottom = 0;
        int margin = 0;
        int marginLeft = 0;
        int marginTop = 0;
        int marginRight = 0;
        int marginBottom = 0;
        if (attributes.containsKey("width") && !TextUtils.isEmpty(attributes.get("width") + "")) {
            width = getValue(attributes, "width");
        }
        if (attributes.containsKey("weight") && !TextUtils.isEmpty(attributes.get("weight") + "")) {
            weight = getValue(attributes, "weight");
        }
        if (attributes.containsKey("height") && !TextUtils.isEmpty(attributes.get("height") + "")) {
            height = getValue(attributes, "height");
        }
        padding = getValue(attributes, "padding");
        paddingLeft = padding;
        paddingTop = padding;
        paddingRight = padding;
        paddingBottom = padding;

        if (attributes.containsKey("paddingLeft") && !TextUtils.isEmpty(attributes.get("paddingLeft") + "")) {
            paddingLeft = getValue(attributes, "paddingLeft");
        }
        if (attributes.containsKey("paddingTop") && !TextUtils.isEmpty(attributes.get("paddingTop") + "")) {
            paddingTop = getValue(attributes, "paddingTop");
        }
        if (attributes.containsKey("paddingRight") && !TextUtils.isEmpty(attributes.get("paddingRight") + "")) {
            paddingRight = getValue(attributes, "paddingRight");
        }
        if (attributes.containsKey("paddingBottom") && !TextUtils.isEmpty(attributes.get("paddingBottom") + "")) {
            paddingBottom = getValue(attributes, "paddingBottom");
        }
        viewNew.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        margin = getValue(attributes, "margin");
        marginLeft = margin;
        marginTop = margin;
        marginRight = margin;
        marginBottom = margin;
        if (attributes.containsKey("marginLeft") && !TextUtils.isEmpty(attributes.get("marginLeft") + "")) {
            marginLeft = getValue(attributes, "marginLeft");
        }
        if (attributes.containsKey("marginTop") && !TextUtils.isEmpty(attributes.get("marginTop") + "")) {
            marginTop = getValue(attributes, "marginTop");
        }
        if (attributes.containsKey("marginRight") && !TextUtils.isEmpty(attributes.get("marginRight") + "")) {
            marginRight = getValue(attributes, "marginRight");
        }
        if (attributes.containsKey("marginBottom") && !TextUtils.isEmpty(attributes.get("marginBottom") + "")) {
            marginBottom = getValue(attributes, "marginBottom");
        }

        int layout_gravity = 0;
        if (attributes.containsKey("layout_gravity")) {
            String gravityStr = attributes.get("layout_gravity") + "";
            //int layout_gravity = layout_gravityDefault;//UNSPECIFIED_GRAVITY;
            if (!TextUtils.isEmpty(gravityStr)) {
                String[] gravitys = gravityStr.split("\\|");
                int gravity = 0;
                for (String str : gravitys) {
                    if (str.equalsIgnoreCase("top")) {
                        gravity = gravity | Gravity.TOP;
                    } else if (str.equalsIgnoreCase("bottom")) {
                        gravity = gravity | Gravity.BOTTOM;
                    } else if (str.equalsIgnoreCase("left")) {
                        gravity = gravity | Gravity.LEFT;
                    } else if (str.equalsIgnoreCase("right")) {
                        gravity = gravity | Gravity.RIGHT;
                    } else if (str.equalsIgnoreCase("center")) {
                        gravity = gravity | Gravity.CENTER;
                    } else if (str.equalsIgnoreCase("CENTER_VERTICAL")) {
                        gravity = gravity | Gravity.CENTER_VERTICAL;
                    } else if (str.equalsIgnoreCase("CENTER_HORIZONTAL")) {
                        gravity = gravity | Gravity.CENTER_HORIZONTAL;
                    }
                }
                if (gravity != 0) {
                    layout_gravity = gravity;
                }
            }
        }
        int content_gravity = 0;
        if (attributes.containsKey("gravity")) {
            String gravityStr = attributes.get("gravity") + "";
            //int layout_gravity = layout_gravityDefault;//UNSPECIFIED_GRAVITY;
            if (!TextUtils.isEmpty(gravityStr)) {
                String[] gravitys = gravityStr.split("\\|");
                int gravity = 0;
                for (String str : gravitys) {
                    if (str.equalsIgnoreCase("top")) {
                        gravity = gravity | Gravity.TOP;
                    } else if (str.equalsIgnoreCase("bottom")) {
                        gravity = gravity | Gravity.BOTTOM;
                    } else if (str.equalsIgnoreCase("left")) {
                        gravity = gravity | Gravity.LEFT;
                    } else if (str.equalsIgnoreCase("right")) {
                        gravity = gravity | Gravity.RIGHT;
                    } else if (str.equalsIgnoreCase("center")) {
                        gravity = gravity | Gravity.CENTER;
                    } else if (str.equalsIgnoreCase("CENTER_VERTICAL")) {
                        gravity = gravity | Gravity.CENTER_VERTICAL;
                    } else if (str.equalsIgnoreCase("CENTER_HORIZONTAL")) {
                        gravity = gravity | Gravity.CENTER_HORIZONTAL;
                    }
                }
                if (gravity != 0) {
                    content_gravity = gravity;
                }
            }
        }
        if (content_gravity != 0 && viewNew instanceof TextView) {
            ((TextView) viewNew).setGravity(content_gravity);
        }

        LinearLayout.LayoutParams layoutParams = null;
        if (attributes.containsKey("width") && !TextUtils.isEmpty("" + attributes.get("width"))) {
            layoutParams = new LinearLayout.LayoutParams(width, height);
        }
        if (attributes.containsKey("height") && !TextUtils.isEmpty("" + attributes.get("height"))) {
            layoutParams = new LinearLayout.LayoutParams(width, height);
        }
        if (weight != -1) {
            layoutParams = new LinearLayout.LayoutParams(width, height, weight);
        }
        if (layout_gravity != 0) {
            layoutParams.gravity = layout_gravity;
        }
        if (layoutParams != null) {
            layoutParams.leftMargin = marginLeft;
            layoutParams.topMargin = marginTop;
            layoutParams.rightMargin = marginRight;
            layoutParams.bottomMargin = marginBottom;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            viewNew.setId(View.generateViewId());
        }
        Object obj = viewNew.getTag();
        if ((obj == null || TextUtils.isEmpty("" + obj)) && !TextUtils.isEmpty("" + viewNew.getId())) {
            viewNew.setTag(viewNew.getId());
        }
        ViewGroup root1 = root;
        View viewNew1 = viewNew;
        LinearLayout.LayoutParams layoutParams1 = layoutParams;
        QdThreadHelper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*if (v8.getLocker().hasLock()) {
                    v8.getLocker().release();
                }*/
                if (root1 != null) {
                    if (layoutParams1 != null) {
                        root1.addView(viewNew1, layoutParams1);
                    } else {
                        root1.addView(viewNew1);
                    }
                }
            }
        });

        if (!(viewNew instanceof RecyclerView)) {
            for (MyNodeElement nodeElement : element.getChildNodes()) {
                inflateXml(context, (ViewGroup) viewNew, nodeElement, eventBandding);
            }
        }
        if (root != null) {
            return root;
        } else {
            return viewNew;
        }
    }

    //执行动态绑定数据代码
    public static MyNodeElement buildAttributes(EventBandding eventBandding, MyNodeElement element) {
        Map<String, String> attributes = element.getAttributesMap();
        //long t1 = System.currentTimeMillis();
        for (Map.Entry entry : attributes.entrySet()) {
            String key = entry.getKey() + "";
            if (key.trim().startsWith("wx:")) {
                return element;
            }

            String resultValue = buildAttributesString(eventBandding, entry.getValue() + "");
            //QDLogger.e("动态key:"+entry.getKey()+"="+ resultValue);
            if (!TextUtils.isEmpty(resultValue)) {
                element.addProperty(key, resultValue);
            }
        }
        for (MyNodeElement nodeElement : element.getChildNodes()) {
            buildAttributes(eventBandding, nodeElement);
        }

       // QDLogger.e("time:" + (System.currentTimeMillis() - t1));
        return element;
    }

    /**
     * 替换动态js代码
     *
     * @param eventBandding
     * @param sourceStr
     * @return
     */
    private static String buildAttributesString(EventBandding eventBandding, String sourceStr) {
        Pattern p = Pattern.compile("(\\{\\{[^\\}]*\\}\\})");
        Matcher m = p.matcher(sourceStr);
        String resultValue = "";
        int position = 0;
        while (m.find()) {
            String str = (m.group().substring(2, m.group().length() - 2));
            String data = "" + eventBandding.getJsValueFromData(str);
            resultValue += sourceStr.substring(position, m.start()) + data;
            position = m.end();
        }
        if (position < sourceStr.length()) {
            resultValue += sourceStr.substring(position, sourceStr.length());
        }
        return resultValue;
    }

    /**
     * 替换动态js代码
     *
     * @param eventBandding
     * @param sourceStr
     * @return
     */
    public static String buildAttributesString2(EventBandding eventBandding, String sourceStr, String script1, String functionName) {
        Pattern p = Pattern.compile("(\\{\\{[^\\}]*\\}\\})");
        Matcher m = p.matcher(sourceStr);
        Object[] waitObj = new Object[]{false, "", 0,0};
        while (m.find()) {
            waitObj[0] = true;
            String str = (m.group().substring(2, m.group().length() - 2));
            waitObj[2] = m.start();
            //QDLogger.i("buildAttributesString2:" + String.format(script1, str));
            eventBandding.getAnsyJsValueFromData2(String.format(script1, str), functionName, new MyRunnable.FunctionCall(null) {
                @Override
                public void onCall(V8 v8, Object result) {
                    //QDLogger.i("替换动态js代码" + result);
                    waitObj[1] += sourceStr.substring((Integer) waitObj[3], (Integer) waitObj[2]) + result;
                    waitObj[2] = m.start();//
                    waitObj[3] = m.end();//
                    waitObj[0] = false;//等待状态
                }
            });
            while ((boolean) waitObj[0]) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if ((waitObj[3]+"").length() < sourceStr.length()) {
            waitObj[1] += sourceStr.substring((Integer) waitObj[3], sourceStr.length());
        }
        //QDLogger.i("替换动态js代码2" + waitObj[1]);
        return waitObj[1]+"";
    }

    //执行动态绑定数据代码
    public static String dynamicAttributes(EventBandding eventBandding, String sourceStr) {
        Pattern p = Pattern.compile("(\\{\\{[^\\}]*\\}\\})");
        Matcher m = p.matcher(sourceStr);
        String resultValue = "";
        int position = 0;
        while (m.find()) {
            String str = (m.group().substring(2, m.group().length() - 2));
            String data = "" + eventBandding.getJsValueFromData(str);
            resultValue += sourceStr.substring(position, m.start()) + data;
            position = m.end();
        }
        if (position < sourceStr.length()) {
            resultValue += sourceStr.substring(position, sourceStr.length());
        }
        //QDLogger.e("动态值1:"+ resultValue);
        return resultValue;
    }

    public static int getValue(Map<String, String> jsonObject, String valueName) {
        int height = 0;
        if (jsonObject.containsKey(valueName)) {
            String heightStr = jsonObject.get(valueName) + "";
            if (!TextUtils.isEmpty(heightStr)) {
                if (heightStr.equals("match_parent")) {
                    height = ViewGroup.LayoutParams.MATCH_PARENT;
                } else if (heightStr.equals("wrap_content")) {
                    height = ViewGroup.LayoutParams.WRAP_CONTENT;
                } else {
                    height = Integer.valueOf(heightStr);
                }
            }
        }
        return height;
    }

    public static View findViewByTag(View view, String tag) {
        // Log.i("AJSLog", "tag="+view.getTag() );
        if (tag.equals(view.getTag() + "")) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                View v = findViewByTag(child, tag);
                if (v != null) {
                    return v;
                }
            }
        }
        return null;
    }
}
