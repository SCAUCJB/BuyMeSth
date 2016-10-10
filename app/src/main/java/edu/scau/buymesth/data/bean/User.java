package edu.scau.buymesth.data.bean;

import cn.bmob.v3.BmobUser;
import edu.scau.buymesth.publish.FlowLayout;

/**
 * Created by IamRabbit on 2016/8/3.
 * 用户
 */
public class User extends BmobUser {
    private String nickname;//昵称
    private Integer age ;//年龄
    private String gender ;//性别
    private Integer exp ;//经验值
    private String avatar;
    private String signature;
    private String residence;
    private Float score;
    private Integer ratePopulation;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getExp() {
        if(exp==null){
            return 0;
        }
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }


    public void setScore(Float score) {
        this.score = score;
    }

    public Float getScore() {
        return score;
    }

    public Integer getRatePop() {
        return ratePopulation;
    }

    public void setRatePopulation(Integer ratePopulation) {
        this.ratePopulation = ratePopulation;
    }
}
