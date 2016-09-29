package edu.scau.buymesth.data.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by John on 2016/9/27.
 */

public class Address extends BmobObject {
    public static final String ID="_id";
    public static final String USER_ID="user_id";
    public static final String RECIPIENT="recipient";
    public static final String PHONE="phone";
    public static final String SPECIFIC="specific";
    public static final String TABLE_NAME="address";
    private User user;
    //收货人
    private String recipient;
    //手机号
    private String phone;
    //省市区
    private String region;
    //详细地址
    private String specific;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSpecific() {
        return specific;
    }

    public void setSpecific(String specific) {
        this.specific = specific;
    }
}
