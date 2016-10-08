package edu.scau.buymesth.conversation.userlist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import edu.scau.Constant;
import edu.scau.buymesth.data.bean.Follow;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;

/**
 * Created by ！ on 2016/9/18.
 */
public class UserListModel implements UserListContract.Model{
    List<String> mUserIds;
    List<User> mUsers;
    private int loadedPageNum;

    public UserListModel() {
        this.mUserIds = new ArrayList<>();
        this.mUsers = new ArrayList<>();
        this.loadedPageNum = 0;
    }

    @Override
    public List<User> getDatas() {
        return mUsers;
    }

    @Override
    public void setDatas(List<User> list) {
        this.mUsers = list;
    }

    @Override
    public void resetPage() {
        loadedPageNum = 0;
    }

    public Observable<List<Follow>> getFollowRx(){
        BmobQuery<Follow> query=new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存两天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.addWhereEqualTo("fromUser", BmobUser.getCurrentUser().getObjectId());
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (loadedPageNum++));
        return query.findObjectsObservable(Follow.class);
    }

    @Override
    public Observable<List<User>> getUsersRx(BmobQuery.CachePolicy cachePolicy){
        BmobQuery<User> query=new BmobQuery<>();
        query.setCachePolicy(cachePolicy);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存两天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.addWhereContainedIn("objectId",mUserIds);
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (loadedPageNum++));
        return query.findObjectsObservable(User.class);
    }

    @Override
    public Observable<List<Follow>> getFollowedRx(BmobQuery.CachePolicy cachePolicy){
        BmobQuery<Follow> query=new BmobQuery<>();
        query.setCachePolicy(cachePolicy);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存两天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.addWhereEqualTo("fromUser", BmobUser.getCurrentUser().getObjectId());
        query.include("toUser");
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (loadedPageNum++));
        return query.findObjectsObservable(Follow.class);
    }
}
