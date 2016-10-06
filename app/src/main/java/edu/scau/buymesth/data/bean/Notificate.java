package edu.scau.buymesth.data.bean;

import android.net.wifi.WifiConfiguration;

import cn.bmob.v3.BmobObject;

/**
 * Created by Jammy on 2016/10/5.
 */
public class Notificate extends BmobObject{
    User user;
    Order order;
    int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
