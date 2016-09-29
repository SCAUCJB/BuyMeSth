package edu.scau.buymesth.data.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by ！ on 2016/9/7.
 */
public class Collect extends BmobObject{
    public static final String TABLE = "collect";
    public static final String ID = "_id";
    public static final String USER_ID = "user_id";
    public static final String REQUEST_ID  = "request_id";
    private User user;
    private Request request;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
