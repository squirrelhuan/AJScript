package cn.demomaster.quickjs_library.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.demomaster.huan.quickdeveloplibrary.util.xml.NodeElement;

public class MyNodeElement {
    String nodeName;
    Object nodeValue;
    //MyNodeElement parent;
    int nodeType =1;

    private List<MyNodeElement> childNodes;
    private List<NodeElement.NodeProperty> attributes;
    private Map<String,String> attributesMap;


    public MyNodeElement() {
        this.childNodes = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.attributesMap = new LinkedHashMap<>();
    }

    public void addProperty(String key, String object) {
        attributesMap.put(key,object);
        attributes.clear();
        for(Map.Entry entry:attributesMap.entrySet()){
            NodeElement.NodeProperty nodeProperty = new NodeElement.NodeProperty((String) entry.getKey(), (String) entry.getValue());
            attributes.add(nodeProperty);
        }
    }

    public void removeAttribute(String key) {
        attributesMap.remove(key);
        attributes.clear();
        for(Map.Entry entry:attributesMap.entrySet()){
            NodeElement.NodeProperty nodeProperty = new NodeElement.NodeProperty((String) entry.getKey(), (String) entry.getValue());
            attributes.add(nodeProperty);
        }
    }

   /* public MyNodeElement getParent() {
        return parent;
    }*/

    public List<NodeElement.NodeProperty> getAttributes() {
        return attributes;
    }

    public Map<String, String> getAttributesMap() {
        return attributesMap;
    }

   /* public void setParent(MyNodeElement parent) {
        this.parent = parent;
    }*/

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Object getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(Object nodeValue) {
        this.nodeValue = nodeValue;
    }

    public List<MyNodeElement> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<MyNodeElement> childNodes) {
        this.childNodes = childNodes;
    }

    public void setAttributes(List<NodeElement.NodeProperty> attributes) {
        this.attributes = attributes;
    }

    public void addNode(MyNodeElement node){
        if(childNodes==null){
            childNodes = new ArrayList<>();
        }
        //node.setParent(this);
        childNodes.add(node);
    }
}
