package edu.scau.buymesth.data.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by ！ on 2016/9/5.
 */
public class Follow extends BmobObject{
    public static final String TABLE = "collect";
    public static final String ID = "_id";
    public static final String FROM_USER_ID = "id_from_user";
    public static final String TO_USER_ID  = "id_to_user";
    private User fromUser;
    private User toUser;

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
}
