package cn.demomaster.quickjs_library.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.demomaster.quickjs_library.ajs.JsRunnable;
import cn.demomaster.quickjs_library.core.ViewHelper;
import cn.demomaster.quickjs_library.model.MyNodeElement;
import cn.demomaster.quickjs_library.model.wx.ForBean;
import cn.demomaster.huan.quickdeveloplibrary.helper.QdThreadHelper;
import cn.demomaster.huan.quickdeveloplibrary.util.xml.NodeElement;
import cn.demomaster.qdlogger_library.QDLogger;

/**
 * Created by Squirrel桓 on 2018/11/11.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private JSONArray lists=null;
    private Context context;
    private MyNodeElement element;
    private ViewHelper.EventBandding eventBandding;
    public static String dataName;
    public static String elementXml;
    public RecycleViewAdapter(Context context, MyNodeElement element, ViewHelper.EventBandding eventBandding) {
        this.context = context;
        this.element=element;
        this.eventBandding = eventBandding;
        dataName = element.getAttributesMap().get("wx:for");
        dataName = getAttributes_For(dataName);

        MyNodeElement myNodeElement = element;
        myNodeElement.removeAttribute("wx:for");
        //转成xml
        elementXml = getXml(myNodeElement);//转换后结果
        QDLogger.d("转换后结果=>"+elementXml);
        //异步获取返回数据
        eventBandding.getAnsyJsValueFromData(dataName, new JsRunnable.FunctionCall(null) {
            @Override
            public void onCall(V8 v8, Object result) {
                QDLogger.e("异步获取返回数据 "+JSON.toJSONString(((V8Object)result).getKeys()));
                lists = JSON.parseArray(JSON.toJSONString(((V8Object)result).getKeys()));
                QdThreadHelper.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (v8.getLocker().hasLock()) {
                            v8.getLocker().release();
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private String getXml(MyNodeElement element) {
        String xml ="<"+element.getNodeName()+" ";
        for(NodeElement.NodeProperty nodeProperty : element.getAttributes()){
            xml+=nodeProperty.getName()+"="+"\""+nodeProperty.getValue()+"\" ";
        }
        xml+=">";

        for(MyNodeElement element1 : element.getChildNodes()){
            xml+=getXml(element1);
        }

        xml+="</"+element.getNodeName()+">";
        return xml;
    }

    /**
     * 替换动态js代码
     * @param sourceStr
     * @return
     */
    private static String getAttributes_For(String sourceStr) {
        Pattern p = Pattern.compile("(\\{\\{[^\\}]*\\}\\})");
        Matcher m = p.matcher(sourceStr);
        if (m.find()) {
            String str = (m.group().substring(2, m.group().length() - 2));
            return str;
        }
        return null;
    }

    //创建View,被LayoutManager所用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(context).inflate(R.layout.item_option_menu,parent,false);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        element.removeAttribute("wx:for");
        ViewHelper.inflateXml(context, frameLayout, element, eventBandding);
        ViewHolder holder = new ViewHolder(eventBandding,frameLayout);
        return holder;
    }

    //数据的绑定
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int index =position;
        int count = lists.size();
        Object item = lists.get(index);
        ForBean forBean = new ForBean(index,count,item);
        //holder.textView.setText();
        holder.setForBean(forBean);//.setTextSize(60);
    }

    @Override
    public int getItemCount() {
        return lists==null?0:lists.size();
    }
    //自定义ViewHolder,包含item的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //public final TextView textView;
        ViewHelper.EventBandding eventBandding;
        public ViewHolder(ViewHelper.EventBandding eventBandding, View itemView) {
            super(itemView);
            this.eventBandding = eventBandding;
        }

        public void setForBean(ForBean forBean) {
            ((ViewGroup)itemView).removeAllViews();
            Random random = new Random();
            String t1 = (""+System.currentTimeMillis()).substring(0,random.nextInt(5)+5)+random.nextInt(1000);
            String functionStr = "function getJsValueFromData2"+t1+"(){" +
                    "var "+ "tmp_data"+"=data." +dataName+";"+
                    "var "+ forBean.getIndexName()+"=" +forBean.getIndex()+";"+
                    "var "+ forBean.getCountName()+"=tmp_data.length;"+
                    "var "+ forBean.getItemName()+"=tmp_data["+forBean.getIndex()+"]" +";"+
                    "return %s;"+
                    "}";
            //QDLogger.println("functionStr="+functionStr);
            String string2 = ViewHelper.buildAttributesString2(eventBandding,elementXml,functionStr,"getJsValueFromData2"+t1);
            MyNodeElement element = ViewHelper.parseXmlStr(itemView.getContext(), string2);
            View view=  ViewHelper.inflateXml(itemView.getContext(),null,element,eventBandding);
            if(view!=null)
           ((ViewGroup) itemView).addView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}

