package edu.scau.buymesth.data.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by John on 2016/8/6.
 */

public class Order extends BmobObject {
    private Float price;
    private Short status;
    private String accomplishAt;
    private String acceptAt;
    private User seller;
    private User buyer;
    private Request request;
    private String expressInc;
    private String expressNumber;
    private String deliverAt;
    private Float tip;
    private Float tax;

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

    public String getAccomplishAt() {
        return accomplishAt;
    }

    public void setAccomplishAt(String accomplishAt) {
        this.accomplishAt = accomplishAt;
    }

    public String getAcceptAt() {
        return acceptAt;
    }

    public void setAcceptAt(String acceptAt) {
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

    public Float getTax() {
        return tax;
    }

    public void setTax(Float tax) {
        this.tax = tax;
    }
}
