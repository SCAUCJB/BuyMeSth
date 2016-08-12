package edu.scau.buymesth.home;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import edu.scau.Constant;
import edu.scau.buymesth.data.bean.Request;
import rx.Observable;

/**
 * Created by John on 2016/8/5.
 * Updated by John on 2016/8/9
 */

public class HomeModel implements HomeContract.Model {
    private int pageNum;
    private List<Request> requestList;
    public HomeModel(){
        requestList=new LinkedList<>();
        pageNum=0;
    }

    @Override
    public List<Request> getDatas() {
        return requestList;
    }

    @Override
    public void setDatas(List<Request>list) {
        requestList=list;
    }

    /**
     * 把显示的页数重置为0，on refresh 之前调用一下
     */
    @Override
    public void resetPage() {
        pageNum=0;
    }

    /**
     * 通常在load more的时候用
     * @return Observable
     */
    @Override
    public Observable<List<Request>> getRxRequests() {
        BmobQuery<Request> query=new BmobQuery<>();
              query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);//先从缓存再从网络
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));//此表示缓存一天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.include("author");
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        return query.findObjectsObservable(Request.class);
    }
}
