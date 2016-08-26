package edu.scau.buymesth.data.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by IamRabbit on 2016/8/22.
 */
public class MomentsLike extends BmobObject{
    private Moment moment;
    private User liker;

    public Moment getMoment() {
        return moment;
    }

    public void setMoment(Moment moment) {
        this.moment = moment;
    }

    public User getLiker() {
        return liker;
    }

    public void setLiker(User liker) {
        this.liker = liker;
    }
}
