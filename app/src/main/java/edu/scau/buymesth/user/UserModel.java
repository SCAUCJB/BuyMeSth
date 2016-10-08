package edu.scau.buymesth.user;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import edu.scau.Constant;
import edu.scau.buymesth.data.bean.Evaluate;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.util.NetworkHelper;
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
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(7));
        if (sp.getBoolean("UserInfoNew", true)) {
            sp.edit().putBoolean("UserInfoNew", false).apply();
            return Observable.just(BmobUser.getCurrentUser(User.class));
        } else {
            query.addWhereEqualTo("objectId", id);
            if(query.hasCachedResult(User.class)|| !NetworkHelper.isOpenNetwork(mContext)){
                query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);
            }else {
                query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
            }
            return query.findObjectsObservable(User.class).map(users -> {
                if (users == null || users.size() <= 0) return null;
                return users.get(0);
            });
        }

    }
public Observable<Integer> getEvaluateCount(String id){
    BmobQuery<Evaluate> query=new BmobQuery<>();
    query.addWhereEqualTo("seller",id);
    if(query.hasCachedResult(Evaluate.class)|| !NetworkHelper.isOpenNetwork(mContext)){
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);
    }else {
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
    }
    return query.countObservable(Evaluate.class);
}
    public Observable<JSONArray> getScore(String id){
        BmobQuery<Evaluate> query=new BmobQuery<>();
        query.addWhereEqualTo("seller",id);
        if(query.hasCachedResult(Evaluate.class)|| !NetworkHelper.isOpenNetwork(mContext)){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);
        }else {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        }
        query.average(new String[]{"score"});
        return query.findStatisticsObservable(Evaluate.class);
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
