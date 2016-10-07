package edu.scau.buymesth.userinfo.evaluate;

import android.content.Context;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import edu.scau.Constant;
import edu.scau.buymesth.data.bean.Evaluate;
import edu.scau.buymesth.util.NetworkHelper;
import rx.Observable;

/**
 * Created by John on 2016/10/7.
 */

public class Model {
    Context mContext;
    int pageNum=0;
    private  String mId;
    private boolean mIsBuyer;

    public Model(Context context){
        mContext=context;
    }

    public Observable<List<Evaluate>> getEvaluates(BmobQuery.CachePolicy policy) {
         if(mIsBuyer){
             return getBuyerEvaluates(policy,mId);
         }else {
             return getSellerEvaluates(policy,mId);
         }
    }
    public Observable<List<Evaluate>> getBuyerEvaluates(BmobQuery.CachePolicy policy,String id) {
        BmobQuery<Evaluate>query=new BmobQuery<>();
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(7));//此表示缓存一天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.include("buyer");
        query.addWhereEqualTo("buyer",id);
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        boolean hasNetwork= NetworkHelper.isOpenNetwork(mContext);
        if(hasNetwork&&policy== BmobQuery.CachePolicy.NETWORK_ONLY)
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        else
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);//先从缓存再从网络

        return query.findObjectsObservable(Evaluate.class);
    }
    public Observable<List<Evaluate>> getSellerEvaluates(BmobQuery.CachePolicy policy,String id) {
        BmobQuery<Evaluate>query=new BmobQuery<>();
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(7));//此表示缓存一天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.include("buyer");
        query.addWhereEqualTo("seller",id);
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        boolean hasNetwork= NetworkHelper.isOpenNetwork(mContext);
        if(hasNetwork&&policy== BmobQuery.CachePolicy.NETWORK_ONLY)
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        else
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);//先从缓存再从网络

        return query.findObjectsObservable(Evaluate.class);
    }

    public void setId(String id) {
        mId=id;
    }

    public void setIsBuyer(boolean isBuyer) {
        mIsBuyer=isBuyer;
    }
}
