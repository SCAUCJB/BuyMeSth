package edu.scau.buymesth.user.moment;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.LinkedList;
import java.util.List;

import base.BasePresenter;
import cn.bmob.v3.BmobQuery;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.data.bean.MomentsLike;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by IamRabbit on 2016/8/10.
 */
public class MomentPresenter extends BasePresenter<MomentContract.Model,MomentContract.View> {

    private Context mContext;

    public MomentPresenter(Context context){
        this.mContext = context;
    }

    @Override
    public void onStart() {
    }

    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getActiveNetworkInfo() != null && connManager.getActiveNetworkInfo().isAvailable();
    }

    public void Refresh(){
        mModel.updateLikeList();
        mModel.resetPage();
        mModel.getDatas().clear();
        BmobQuery.CachePolicy cachePolicy;
        if(isOpenNetwork())cachePolicy = BmobQuery.CachePolicy.CACHE_THEN_NETWORK;
        else cachePolicy = BmobQuery.CachePolicy.CACHE_ONLY;
        mModel.getRxMoments(cachePolicy).flatMap(new Func1<List<Moment>, Observable<Moment>>() {
            @Override
            public Observable<Moment> call(List<Moment> moments) {
                return Observable.from(moments);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Moment>() {
                    int size = mModel.getDatas().size();
                    @Override
                    public void onCompleted() {
                        if(isAlive()){
                            mView.onRefreshComplete(mModel.getDatas());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if(isAlive()){
                            mView.onError(null,"获取数据出了些问题");
                            mView.onRefreshInterrupt();
                        }
                    }

                    @Override
                    public void onNext(Moment moment) {
                        boolean add = true;
                        for(int i = 0;i<size;i++){
                            if(moment.getObjectId().equals(mModel.getDatas().get(i).getObjectId())){
                                add = false;
                            }
                        }
                        if(add){
                            for(MomentsLike obj : mModel.getLikesList()){
                                if(obj.getMoment().getObjectId().contains(moment.getObjectId())) {
                                    moment.setLike(true);
                                    break;
                                }
                            }
                            mModel.getDatas().add(moment);
                        }
                    }
                });
    }

    public void LoadMore(){
        BmobQuery.CachePolicy cachePolicy;
    //    if(isOpenNetwork())cachePolicy = BmobQuery.CachePolicy.CACHE_THEN_NETWORK;
   //     else
        cachePolicy = BmobQuery.CachePolicy.CACHE_ONLY;
        List<Moment> tempList=new LinkedList<>();
        mModel.getRxMoments(cachePolicy).flatMap(new Func1<List<Moment>, Observable<Moment>>() {
            @Override
            public Observable<Moment> call(List<Moment> moments) {
                return Observable.from(moments);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Moment>() {
                    @Override
                    public void onCompleted() {
                        mView.onLoadMoreSuccess(tempList);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mView.onError(throwable, "获取数据出了些问题");
                    }

                    @Override
                    public void onNext(Moment moment) {
                        mModel.getDatas().add(moment);
                        tempList.add(moment);
                    }
                });
    }
}
