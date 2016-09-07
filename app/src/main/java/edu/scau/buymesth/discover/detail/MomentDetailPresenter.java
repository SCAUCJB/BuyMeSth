package edu.scau.buymesth.discover.detail;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.LinkedList;
import java.util.List;

import base.BasePresenter;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import edu.scau.buymesth.data.bean.MomentsComment;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by IamRabbit on 2016/8/24.
 */
public class MomentDetailPresenter extends BasePresenter<MomentDetailModel,MomentDetailActivity>{
    private Context mContext;

    public MomentDetailPresenter(Context context) {
        mContext = context;
    }

    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connManager.getActiveNetworkInfo()!=null){
            return connManager.getActiveNetworkInfo().isAvailable();
        }else {
            return false;
        }
    }

    @Override
    public void onStart() {

    }

    public void Refresh(){
        if(mModel.getMoment()!=null){
            mModel.resetPage();
            mModel.getDatas().clear();
            BmobQuery.CachePolicy cachePolicy;
            if(isOpenNetwork())cachePolicy = BmobQuery.CachePolicy.CACHE_THEN_NETWORK;
            else cachePolicy = BmobQuery.CachePolicy.CACHE_ONLY;
            mModel.getRxComments(cachePolicy).flatMap(new Func1<List<MomentsComment>, Observable<MomentsComment>>() {
                @Override
                public Observable<MomentsComment> call(List<MomentsComment> moments) {
                    return Observable.from(moments);
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<MomentsComment>() {
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
                        public void onNext(MomentsComment momentsComment) {
                            mModel.getDatas().add(momentsComment);
                        }
                    });
        }
    }

    public void LoadMore(){
        BmobQuery.CachePolicy cachePolicy;
        if(isOpenNetwork())cachePolicy = BmobQuery.CachePolicy.CACHE_THEN_NETWORK;
        else cachePolicy = BmobQuery.CachePolicy.CACHE_ONLY;
        List<MomentsComment> tempList = new LinkedList<>();
        mModel.getRxComments(cachePolicy).flatMap(new Func1<List<MomentsComment>, Observable<MomentsComment>>() {
            @Override
            public Observable<MomentsComment> call(List<MomentsComment> momentsComment) {
                return Observable.from(momentsComment);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MomentsComment>() {
                    @Override
                    public void onCompleted() {
                        mView.onLoadMoreSuccess(tempList);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mView.onError(null,"获取数据出了些问题");
                        mView.onLoadMoreInterrupt();
                    }

                    @Override
                    public void onNext(MomentsComment momentsComment) {
                        mModel.getDatas().add(momentsComment);
                        tempList.add(momentsComment);
                    }
                });
    }

     void postComment(String text){
        MomentsComment momentsComment = new MomentsComment();
        momentsComment.setContent(text);
        momentsComment.setUser(BmobUser.getCurrentUser(User.class));
        momentsComment.setMoment(mModel.getMoment());
        momentsComment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    mView.onPostCommentSuccess(s);
                }
            }
        });
    }
}
