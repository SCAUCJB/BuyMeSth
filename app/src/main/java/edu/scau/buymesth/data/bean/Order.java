package edu.scau.buymesth.data.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by John on 2016/8/6.
 */

public class Order extends BmobObject {
    public static final short STATUS_CREATED=0;
    public static final short STATUS_ACCEPTED=1;
    public static final short STATUS_REJECTED=2;
    public static final short STATUS_DELIVERING=3;
    public static final short STATUS_FINISH=4;
    private Float price;
    private Short status;
    private BmobDate accomplishAt;
    private BmobDate acceptAt;
    private User seller;
    private User buyer;
    private Request request;
    private String expressInc;
    private String expressNumber;
    private String deliverAt;
    private Float tip;
    private String tipType;
     private String priceType;
    private List<String> tags ;
    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public BmobDate getAccomplishAt() {
        return accomplishAt;
    }

    public void setAccomplishAt(BmobDate accomplishAt) {
        this.accomplishAt = accomplishAt;
    }

    public BmobDate getAcceptAt() {
        return acceptAt;
    }

    public void setAcceptAt(BmobDate acceptAt) {
        this.acceptAt = acceptAt;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getExpressInc() {
        return expressInc;
    }

    public void setExpressInc(String expressInc) {
        this.expressInc = expressInc;
    }

    public String getExpressNumber() {
        return expressNumber;
    }

    public void setExpressNumber(String expressNumber) {
        this.expressNumber = expressNumber;
    }

    public String getDeliverAt() {
        return deliverAt;
    }

    public void setDeliverAt(String deliverAt) {
        this.deliverAt = deliverAt;
    }

    public Float getTip() {
        return tip;
    }

    public void setTip(Float tip) {
        this.tip = tip;
    }


    public String getTipType() {
        return tipType;
    }

    public void setTipType(String tipType) {
        this.tipType = tipType;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
