package edu.scau.buymesth.data.bean;

import java.util.List;

import adpater.MultiItemEntity;
import cn.bmob.v3.BmobObject;

/**
 * Created by Jammy on 2016/10/1.
 */
public class OrderMoment extends BmobObject{
    private List<String> PicList;
    private String text;
    Order order;

    public List<String> getPicList() {
        return PicList;
    }

    public void setPicList(List<String> picList) {
        PicList = picList;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
