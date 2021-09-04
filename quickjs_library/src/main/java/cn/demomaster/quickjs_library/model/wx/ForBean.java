package cn.demomaster.quickjs_library.model.wx;

import java.util.HashMap;
import java.util.Map;

public class ForBean {
    int index;
    int count ;
    Object item;

    String indexName ="index";
    String countName ="count";
    String itemName ="item";
    public ForBean(int index,int count,Object item) {
        this.index = index;
        this.count = count;
        this.item = item;
        map.put(indexName,index);
        map.put(countName,count);
        map.put(itemName,item);
    }
    Map<String,Object> map = new HashMap<>();

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getCountName() {
        return countName;
    }

    public void setCountName(String countName) {
        this.countName = countName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
