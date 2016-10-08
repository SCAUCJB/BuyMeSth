package edu.scau.buymesth.conversation.userlist;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import base.BasePresenter;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import edu.scau.buymesth.data.bean.Follow;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ！ on 2016/9/18.
 */
public class UserListPresenter extends BasePresenter<UserListContract.Model,UserListContract.View> {

    Context mContext;
    String mSearchKey = "";

    public UserListPresenter(Context context){
        this.mContext = context;
    }

    public void setSearchKey(String searchKey){
        mSearchKey = searchKey;
    }

    @Override
    public void onStart() {

    }

    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getActiveNetworkInfo() != null && connManager.getActiveNetworkInfo().isAvailable();
    }

    public void refresh(){

        BmobQuery.CachePolicy cachePolicy;
        if(isOpenNetwork())cachePolicy = BmobQuery.CachePolicy.NETWORK_ONLY;
        else cachePolicy = BmobQuery.CachePolicy.CACHE_ONLY;
        mModel.getFollowedRx(cachePolicy).flatMap(new Func1<List<Follow>, Observable<Follow>>() {
            @Override
            public Observable<Follow> call(List<Follow> follows) {
                return Observable.from(follows);
            }
        }).map(follow -> follow.getToUser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    ArrayList<User> tempList = new ArrayList<>();
                    @Override
                    public void onCompleted() {
                        if(isAlive()){
                            mModel.resetPage();
                            mModel.getDatas().clear();
                            mModel.getDatas().add(BmobUser.getCurrentUser(User.class));
                            mModel.getDatas().addAll(tempList);
                            //
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
                    public void onNext(User user) {
                        if(user.getNickname().contains(mSearchKey)||user.getUsername().contains(mSearchKey))
                            tempList.add(user);
                    }
                });
    }

    public void loadMore(){
        BmobQuery.CachePolicy cachePolicy;
        if(isOpenNetwork())cachePolicy = BmobQuery.CachePolicy.CACHE_THEN_NETWORK;
        else cachePolicy = BmobQuery.CachePolicy.CACHE_ONLY;
        mModel.getFollowedRx(cachePolicy).flatMap(new Func1<List<Follow>, Observable<Follow>>() {
            @Override
            public Observable<Follow> call(List<Follow> follows) {
                return Observable.from(follows);
            }
        }).map(follow -> follow.getToUser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    int size = mModel.getDatas().size();
                    @Override
                    public void onCompleted() {
                        if(isAlive()){
                            mView.onLoadMoreSuccess(mModel.getDatas());
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
                    public void onNext(User user) {
                        boolean add = true;
                        for(int i = 0;i<size;i++){
                            if(user.getObjectId().equals(mModel.getDatas().get(i).getObjectId())){
                                add = false;
                            }
                        }
                        if(add){
                            mModel.getDatas().add(user);
                        }
                    }
                });
    }
}
