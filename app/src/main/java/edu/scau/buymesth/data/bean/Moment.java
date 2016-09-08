package edu.scau.buymesth.data.bean;

import java.util.List;

import adpater.MultiItemEntity;
import cn.bmob.v3.BmobObject;

/**
 * Created by IamRabbit on 2016/8/12.
 */
public class Moment extends BmobObject implements MultiItemEntity{
    private User user;
    private String content;
    private List<String> images;
    private String url;
    private Request request;
    private int likes;
    private int comments;
    private String location;
    private boolean isLike;
    private boolean authorDelete;

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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    @Override
    public int getItemType() {
        return request==null?0:1;
    }

    public boolean isAuthorDelete() {
        return authorDelete;
    }

    public void setAuthorDelete(boolean authorDelete) {
        this.authorDelete = authorDelete;
    }
}
