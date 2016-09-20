package edu.scau.buymesth.data.bean;

import adpater.MultiItemEntity;
import cn.bmob.v3.BmobObject;

/**
 * Created by John on 2016/9/19.
 */

public class CommentsLike extends BmobObject implements MultiItemEntity {
    private Comment comment;
    private User user;
    private String content;

    public Comment getMoment() {
        return comment;
    }

    public void setMoment(Comment comment) {
        this.comment = comment;
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
