package edu.scau.buymesth.user;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import edu.scau.Constant;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Jammy on 2016/8/31.
 */
public class UserModel {
    private final Context mContext;
    private User user;

    public UserModel(Context context) {
        mContext = context;
    }

    public Observable<User> getUser(String id) {
        BmobQuery<User> query = new BmobQuery<>();
        SharedPreferences sp = mContext.getSharedPreferences(Constant.SHARE_PREFERENCE_CACHE_FREASHNESS, MODE_PRIVATE);
        query.addWhereEqualTo("objectId",id);
        if (sp.getBoolean("UserInfoNew", true)||!query.hasCachedResult(User.class)) {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
            sp.edit().putBoolean("UserInfoNew", false).apply();
        } else {
                query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);
        }
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(7));
        return query.findObjectsObservable(User.class).map(users -> {
            if(users==null||users.size()<=0)return null;
            return users.get(0);
        });

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
