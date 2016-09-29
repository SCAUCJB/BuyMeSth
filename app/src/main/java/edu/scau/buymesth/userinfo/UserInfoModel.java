package edu.scau.buymesth.userinfo;

import cn.bmob.v3.BmobQuery;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;

/**
 * Created by John on 2016/9/24.
 */

public class UserInfoModel {
    private User user;

    public Observable<User> getUser(String id) {
        BmobQuery<User> query=new BmobQuery<>();
        return query.getObjectObservable(User.class,id);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
