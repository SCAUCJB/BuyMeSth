package edu.scau.buymesth.data.adapter;

import edu.scau.buymesth.data.bean.Moment;

/**
 * Created by IamRabbit on 2016/8/17.
 */
public class MomentItem extends Moment{
    private int position = 0;
    private int likesCount = 0;
    private int commentsCount = 0;

    public MomentItem(Moment moment){
        this.setUser(moment.getUser());
        this.setLikes(moment.getLikes());
        this.setObjectId(moment.getObjectId());
        this.setImages(moment.getImages());
        this.setComments(moment.getComments());
        this.setContent(moment.getContent());
        this.setLocation(moment.getLocation());
        this.setUrl(moment.getUrl());
        this.setRequest(moment.getRequest());
        this.setCreatedAt(moment.getCreatedAt());
        this.setUpdatedAt(moment.getUpdatedAt());
        this.setTableName(moment.getTableName());
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
}
