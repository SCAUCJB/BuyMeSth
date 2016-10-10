package edu.scau.buymesth.user;


import android.content.Context;

/**
 * Created by Jammy on 2016/8/31.
 */
public interface UserContract {
    interface View {
        void setUserName(String name);
        void setUserId(String id);
        void setAvatar(String url);
        void setLevel(Integer level);
        void setlocation(String location);
        void setSignature(String description);
        void setScore(String score);
        void setPopulation(String population);
        void setRatingBar(Float score);
        void initTab();

        void setEvaluateCount(Integer integer);
    }

    interface Presenter {
        void subscribe();
        void unsubscribe();
        void showUserInfo();
        void showTab();
    }


}
