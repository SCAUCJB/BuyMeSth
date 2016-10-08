package edu.scau.buymesth.userinfo;

/**
 * Created by John on 2016/9/24.
 */

public interface Contract  {
    interface View{
        void setUserName(String name);
        void setAvatar(String url);
        void setLevel(Integer level);
        void setlocation(String location);
        void setSignature(String description);
        void setScore(String score);
        void setPopulation(String population);
        void showMsg(String msg);
        boolean hasNet();
        void setUserId(String id);
        void setRatingBar(Float score);

        void initTab();

        void setEvaluateCount(Integer integer);

        void setFollow(boolean b);
    }
    interface Presenter{
        void subscribe();
        void unsubscribe();
        void showUserInfo();
        void follow();
        void showTab();
    }
}
