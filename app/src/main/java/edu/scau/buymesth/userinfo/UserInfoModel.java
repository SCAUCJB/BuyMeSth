package edu.scau.buymesth.userinfo;

import android.content.Context;

import cn.bmob.v3.BmobQuery;
import edu.scau.buymesth.data.bean.Evaluate;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.util.NetworkHelper;
import rx.Observable;

/**
 * Created by John on 2016/9/24.
 */

public class UserInfoModel {
    private User user;
private Context mContext;
    UserInfoModel(Context context)
    {
        mContext=context;
    }
    public Observable<User> getUser(String id) {
        BmobQuery<User> query=new BmobQuery<>();
        return query.getObjectObservable(User.class,id);
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
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
