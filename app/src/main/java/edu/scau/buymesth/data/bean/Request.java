package edu.scau.buymesth.data.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by John on 2016/8/4.
 * 用户发的一个代购请求贴
 */

public class Request extends BmobObject {
    private String title;
    private String content;
    private List<String> urls;
    private User author;
    private Boolean isAnonymous;
    private Integer likes;
    private Integer comments;
    private Boolean isAccecpted;//是否被接单
    private Integer minPrice;
    private Integer maxPrice;

    /**
     * 帖子标题
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 帖子内容
     */
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 图片地址
     */
    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    /**
     * 发表人
     */
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    /**
     * 是否匿名
     */
    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }

    /**
     * 赞同数
     */
    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    /**
     * 评论数
     */
    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }
}
