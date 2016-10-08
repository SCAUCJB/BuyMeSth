package edu.scau.buymesth.request;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import edu.scau.Constant;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;

/**
 * Created by John on 2016/8/5.
 * Updated by John on 2016/8/9
 */

public class HomeModel implements HomeContract.Model {
    public static final int FROM_CACHE = 0;
    public static final int FROM_NETWORK = 1;
    private int pageNum;
    private List<Request> requestList;

    public HomeModel() {
        requestList = new LinkedList<>();
        pageNum = 0;
    }

    @Override
    public List<Request> getDatas() {
        return requestList;
    }

    @Override
    public void setDatas(List<Request> list) {
        requestList = list;
    }

    /**
     * 把显示的页数重置为0，on refresh 之前调用一下
     */
    @Override
    public void resetPage() {
        pageNum = 0;
    }

    /**
     * 通常在load more的时候用
     *
     * @return Observable
     */
    @Override
    public Observable<List<Request>> getRxRequests(int policy) {
        BmobQuery<Request> query = new BmobQuery<>();
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));//此表示缓存一天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.include("user");
        query.addWhereEqualTo("isAccepted", false);
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        if (policy == FROM_CACHE && query.hasCachedResult(Request.class))
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        else
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);//先从缓存再从网络

        return query.findObjectsObservable(Request.class);
    }

    @Override
    public Observable<List<Request>> getSomeonesRxRequests(int policy, String userId) {
        BmobQuery<Request> eq1 = new BmobQuery<>();
        eq1.addWhereEqualTo("user", userId);
        BmobQuery<Request> eq2 = new BmobQuery<>();
        eq2.addWhereEqualTo("isAccepted", false);
        List<BmobQuery<Request>> queries = new ArrayList<>();
        queries.add(eq1);
        queries.add(eq2);
        BmobQuery<Request> query = new BmobQuery<>();
        query.and(queries);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));//此表示缓存一天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.include("user");
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        if (policy == FROM_CACHE && query.hasCachedResult(Request.class))
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        else
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);

        return query.findObjectsObservable(Request.class);
    }

    @Override
    public Observable<List<Request>> getSomeonesRxRequests(int policy, List<String> userIds) {
        BmobQuery<Request> eq1 = new BmobQuery<>();
        eq1.addWhereContainedIn("user", userIds);
        BmobQuery<Request> eq2 = new BmobQuery<>();
        eq2.addWhereEqualTo("isAccepted", false);
        List<BmobQuery<Request>> queries = new ArrayList<>();
        queries.add(eq1);
        queries.add(eq2);
        BmobQuery<Request> query = new BmobQuery<>();
        query.and(queries);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));//此表示缓存一天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.include("user");

        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        if (policy == FROM_CACHE && query.hasCachedResult(Request.class))
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        else
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);//先从缓存再从网络

        return query.findObjectsObservable(Request.class);
    }

    @Override
    public Observable<List<Request>> getFuzzySearchRxRequests(int policy, String key) {
        BmobQuery<Request> query = new BmobQuery<>();
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));//此表示缓存一天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.include("user");

        BmobQuery<Request> q1 = new BmobQuery<Request>();
        BmobQuery<Request> q2 = new BmobQuery<Request>();
        BmobQuery<Request> q3 = new BmobQuery<Request>();
        BmobQuery<Request> q4 = new BmobQuery<Request>();
        BmobQuery<Request> q5 = new BmobQuery<Request>();
        BmobQuery<User> innerQuery = new BmobQuery<User>();
        innerQuery.addWhereContains("nickname", key);
        q1.addWhereMatchesQuery("user", "_User", innerQuery);
        q2.addWhereContains("tags", key);
        q3.addWhereContains("title", key);
        q4.addWhereContains("content", key);
        q5.addWhereEqualTo("isAccepted", false);
        List<BmobQuery<Request>> mq = new ArrayList<>();
        List<BmobQuery<Request>> mq2 = new ArrayList<>();
        mq.add(q1);
        mq.add(q2);
        mq.add(q3);
        mq.add(q4);
        mq2.add(q5);
        query.and(mq2);
        query.or(mq);

        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        if (policy == FROM_CACHE && query.hasCachedResult(Request.class))
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        else
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);//先从缓存再从网络

        return query.findObjectsObservable(Request.class);
    }

    @Override
    public Observable<BmobQueryResult<Request>> getFollowedRxRequests(int policy, String key) {
        String userId = key != null ? key : BmobUser.getCurrentUser().getObjectId();
        String sql = "select include user,* from Request where user in (select toUser from Follow where fromUser = '" + userId + "')";
        BmobQuery<Request> query = new BmobQuery<>();
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));//此表示缓存一天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.include("user");
        query.addWhereEqualTo("isAccepted",false);
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        query.setSQL(sql);
        if (policy == FROM_CACHE && query.hasCachedResult(Request.class))
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        else
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);//先从缓存再从网络
        return query.doSQLQueryObservable(Request.class);
    }

}
