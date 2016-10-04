package edu.scau.buymesth.data.bean;

import java.io.Serializable;
import java.util.List;

import adpater.MultiItemEntity;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import edu.scau.Constant;

/**
 * Created by John on 2016/8/6.
 */

public class Order extends BmobObject implements Serializable, MultiItemEntity{
    public static final short STATUS_CREATED=0;
    public static final short STATUS_ACCEPTED=1;
    public static final short STATUS_REJECTED=2;
    public static final short STATUS_DELIVERING=3;
    public static final short STATUS_FINISH=4;
    public static final short STATUS_SELLER_REJECT=5;
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
    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

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

    public void setUpdateAt(String date){
        setUpdatedAt(date);
    }

    @Override
    public int getItemType() {
        //////根据不同的类型返回不同的值，根据这个值来设置消息提示的布局
        User user = BmobUser.getCurrentUser(User.class);
        if(buyer.getObjectId().equals(user.getObjectId())&&status==STATUS_CREATED){
            ////自己是买家，发送个有人接收订单的提示
            return Constant.BUYER_STATUS_CREATE;
        }else if(seller.getObjectId().equals(user.getObjectId())&&status==STATUS_CREATED){
            //自己是卖家，提示已经接收订单，等待买家确认
            return Constant.SELLER_STATUS_CREATE;
        }else if(buyer.getObjectId().equals(user.getObjectId())&&status==STATUS_REJECTED){
            ////提示已拒接别人发来的订单
            return Constant.BUYER_STATUS_REJECT;
        }else if(seller.getObjectId().equals(user.getObjectId())&&status==STATUS_REJECTED){
            //提示发送的订单被别人拒绝
            return Constant.SELLER_STATUS_REJECT;
        }else if(buyer.getObjectId().equals(user.getObjectId())&&status==STATUS_ACCEPTED){
            //提示你已接收别人的订单，等待发货，此时有可能卖家发送照片
            return Constant.BUYER_STATUS_ACCEPT;
        }else if(seller.getObjectId().equals(user.getObjectId())&&status==STATUS_ACCEPTED){
            ///你发送的订单已被买家接收，可以开始发货了，此时可以发送照片
            return Constant.SELLER_STATUS_ACCEPT;
        }else if(buyer.getObjectId().equals(user.getObjectId())&&status==STATUS_DELIVERING){
            ///卖家已发货，等待接收
            return Constant.BUYER_STATUS_DELIVERING;
        }else if(seller.getObjectId().equals(user.getObjectId())&&status==STATUS_DELIVERING){
            ///你已发货，等待买家接收
            return Constant.SELLER_STATUS_DELIVERING;
        }else if(buyer.getObjectId().equals(user.getObjectId())&&status==STATUS_FINISH){
            ////你已收货，交易完成
            return Constant.BUYER_STATUS_FINISH;
        }else if(seller.getObjectId().equals(user.getObjectId())&&status==STATUS_FINISH){
            ////买家已收货，交易完成
            return Constant.SELLER_STATUS_FINISH;
        }else if(buyer.getObjectId().equals(user.getObjectId())&&status==STATUS_SELLER_REJECT){

            return Constant.BUYER_STATUS_SELLER_REJECT;
        }else if(seller.getObjectId().equals(user.getObjectId())&&status==STATUS_SELLER_REJECT){
            return Constant.SELLER_STATUS_SELLER_REJECT;
        }
        return 0;
    }
}
