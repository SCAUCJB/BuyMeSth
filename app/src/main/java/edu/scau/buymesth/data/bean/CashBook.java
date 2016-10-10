package edu.scau.buymesth.data.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Jammy on 2016/10/7.
 */
public class CashBook extends BmobObject {

    public static final int BUYER_PAY = 0;
    public static final int SELLER_GET = 1;
    public static final int SELLER_CANCLE = 2;
    public static final int DEPOSIT = 3;
    public static final int WITHDRAW = 4;

    User user;
    int type;
    User toUser;
    Order toOrder;
    String describe;
    Float cash;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public Order getToOrder() {
        return toOrder;
    }

    public void setToOrder(Order toOrder) {
        this.toOrder = toOrder;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Float getCash() {
        return cash;
    }

    public void setCash(Float cash) {
        this.cash = cash;
    }
}



