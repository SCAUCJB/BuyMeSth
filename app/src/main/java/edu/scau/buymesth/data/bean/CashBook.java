package edu.scau.buymesth.data.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Jammy on 2016/10/7.
 */
public class CashBook extends BmobObject {

    public static final int PAY = 0;
    public static final int GET = 1;
    public static final int CANCLE = 2;
    public static final int DEPOSIT = 3;
    public static final int WITHDRAW = 4;

    User user;
    int type;
    Order order;
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

    public Order getorder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


    public Float getCash() {
        return cash;
    }

    public void setCash(Float cash) {
        this.cash = cash;
    }
}



