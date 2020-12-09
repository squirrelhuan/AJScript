package cn.demomaster.ajscript.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.demomaster.huan.quickdeveloplibrary.base.fragment.QDFragment;
import cn.demomaster.huan.quickdeveloplibrary.util.xml.NodeElement;
import cn.demomaster.huan.quickdeveloplibrary.view.banner.qdlayout.AjsLayoutInflater;
import cn.demomaster.huan.quickdeveloplibrary.view.banner.qdlayout.Element;
import cn.demomaster.qdlogger_library.QDLogger;

import static cn.demomaster.huan.quickdeveloplibrary.view.banner.qdlayout.AjsLayoutInflater.generateLayout;

public class TestFragment extends QDFragment {
    @Override
    public View onGenerateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = new FrameLayout(getContext());
        NodeElement nodeElement = AjsLayoutInflater.parseXmlAssets(getContext(), "config/test.xml", null);
        if (nodeElement != null) {
            Element myElement = getElement(nodeElement);
            view.addView(generateLayout(getContext(), null, myElement));
        }
        return view;
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
    @Override
    public void initView(View rootView) {

    }

    @Override
    public void onFragmentResume() {

    }
}
