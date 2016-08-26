package edu.scau.buymesth.data.bean;

import adpater.MultiItemEntity;
import cn.bmob.v3.BmobObject;

/**
 * Created by IamRabbit on 2016/8/23.
 */
public class MomentsComment extends BmobObject implements MultiItemEntity {
    private Moment moment;
    private User user;
    private String content;

    public Moment getMoment() {
        return moment;
    }

    public void setMoment(Moment moment) {
        this.moment = moment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
