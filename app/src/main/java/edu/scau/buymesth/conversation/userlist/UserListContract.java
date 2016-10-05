package edu.scau.buymesth.conversation.userlist;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import edu.scau.buymesth.data.bean.Follow;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;

/**
 * Created by ！ on 2016/9/18.
 */
public class UserListContract {
    interface  View{
        void onLoadMoreSuccess(List<User> list);
        void onError(Throwable throwable, String msg);
        void onRefreshComplete(List<User> list);
        void onRefreshInterrupt();
    }

    interface Model{
        List<User> getDatas();
        void setDatas(List<User> list);
        void resetPage();
        Observable<List<User>> getUsersRx(BmobQuery.CachePolicy cachePolicy);
        Observable<List<Follow>> getFollowedRx(BmobQuery.CachePolicy cachePolicy);
    }
}
