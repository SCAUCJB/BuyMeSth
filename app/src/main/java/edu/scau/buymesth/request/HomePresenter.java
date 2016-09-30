package edu.scau.buymesth.request;

import java.util.LinkedList;
import java.util.List;

import base.BasePresenter;
import cn.bmob.v3.datatype.BmobQueryResult;
import edu.scau.buymesth.data.bean.Request;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by John on 2016/8/5.
 */

public class HomePresenter extends BasePresenter<HomeContract.Model, HomeContract.View> {

    public static final String FILTER_AUTHOR_ID = "USERID";
    public static final String FILTER_AUTHOR_IDS = "USERIDS";
    public static final String FILTER_FUZZY_SEARCH = "FUZZY";
    public static final String FILTER_FOLLOW_ONLY = "FOLLOW";
    private CompositeSubscription mSubscriptions=new CompositeSubscription();

    /**
     * 初始化工作写在这里
     */
    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

     void onRefresh(String filter , Object key) {
        if(!isAlive())return ;
        Observable<List<Request>> requestObservable;
        mModel.resetPage();
        List<Request> tempList=new LinkedList<>();
         int  policy=mView.hasNetwork()?HomeModel.FROM_NETWORK:HomeModel.FROM_CACHE;

        if(filter==FILTER_AUTHOR_ID){
            requestObservable = mModel.getSomeonesRxRequests(policy,(String) key);
        }else if(filter==FILTER_AUTHOR_IDS){
            requestObservable = mModel.getSomeonesRxRequests(policy ,(List<String>)key);
        }else if(filter==FILTER_FUZZY_SEARCH){
            requestObservable = mModel.getFuzzySearchRxRequests(policy,(String)key);
        }else if(filter == FILTER_FOLLOW_ONLY){
            Observable<BmobQueryResult<Request>> bqlRequestObservable = mModel.getFollowedRxRequests(policy, (String) key);
            bqlRequestObservable.flatMap(new Func1<BmobQueryResult<Request>, Observable<Request>>() {
                @Override
                public Observable<Request> call(BmobQueryResult<Request> requestBmobQueryResult) {
                    return Observable.from(requestBmobQueryResult.getResults());
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Request>() {
                        @Override
                        public void onCompleted() {
                            if(isAlive())
                            {
                                mModel.getDatas().clear();
                                mModel.setDatas(tempList);
                                mView.onRefreshComplete(mModel.getDatas());}
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            if (isAlive()){
                                mView.showError("仿佛网络有点差");
                                mView.onRefreshFail();
                            }
                        }

                        @Override
                        public void onNext(Request request) {
                            if(isAlive()) tempList.add(request);
                        }
                    });
            return;
        } else {
            requestObservable = mModel.getRxRequests(policy);
        }

         mSubscriptions.add(requestObservable.flatMap(new Func1<List<Request>, Observable<Request>>() {
            @Override
            public Observable<Request> call(List<Request> requests) {
                return Observable.from(requests);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Request>() {
                    @Override
                    public void onCompleted() {
                        if(isAlive())
                        {
                            mModel.getDatas().clear();
                            mModel.setDatas(tempList);
                            mView.onRefreshComplete(mModel.getDatas());}
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (isAlive()){
                            mView.showError("仿佛网络有点差");
                            mView.onRefreshFail();
                        }
                    }

                    @Override
                    public void onNext(Request request) {
                        if(isAlive()) tempList.add(request);
                    }
                }));
    }

    public void onLoadMore(String filter , Object key) {
        Observable<List<Request>> requestObservable;
        List<Request> tempList = new LinkedList<>();
        int  policy=mView.hasNetwork()?HomeModel.FROM_NETWORK:HomeModel.FROM_CACHE;
        if(filter==FILTER_AUTHOR_ID){
            requestObservable = mModel.getSomeonesRxRequests(policy,(String) key);
        }else if(filter==FILTER_AUTHOR_IDS){
            requestObservable = mModel.getSomeonesRxRequests(policy,(List<String>)key);
        }else if(filter==FILTER_FUZZY_SEARCH){
            requestObservable = mModel.getFuzzySearchRxRequests(policy,(String)key);
        }else if(filter == FILTER_FOLLOW_ONLY){
            Observable<BmobQueryResult<Request>> bqlRequestObservable = mModel.getFollowedRxRequests(HomeModel.FROM_NETWORK, (String) key);
            mSubscriptions.add(bqlRequestObservable.flatMap(new Func1<BmobQueryResult<Request>, Observable<Request>>() {
                @Override
                public Observable<Request> call(BmobQueryResult<Request> requestBmobQueryResult) {
                    return Observable.from(requestBmobQueryResult.getResults());
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Request>() {
                        @Override
                        public void onCompleted() {
                            if (isAlive()) {
                                if (tempList.size() > 0)
                                    mView.onLoadMoreSuccess(tempList);
                                else
                                    mView.onLoadMoreSuccess(null);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            if (isAlive()){
                                mView.showError("仿佛网络有点差");
                                mView.onRefreshFail();
                            }

                        }

                        @Override
                        public void onNext(Request request) {
                            if (isAlive()) tempList.add(request);
                        }
                    }));
            return;
        }else {
            requestObservable = mModel.getRxRequests(policy);
        }

        mSubscriptions.add(requestObservable.flatMap(new Func1<List<Request>, Observable<Request>>() {
            @Override
            public Observable<Request> call(List<Request> requests) {
                return Observable.from(requests);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Request>() {
                    @Override
                    public void onCompleted() {
                        if (isAlive()) {
                            if (tempList.size() > 0)
                                mView.onLoadMoreSuccess(tempList);
                            else
                                mView.onLoadMoreSuccess(null);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (isAlive()){
                            mView.showError("仿佛网络有点差");
                            mView.onRefreshFail();
                        }

                    }

                    @Override
                    public void onNext(Request request) {
                        if (isAlive()) tempList.add(request);
                    }
                }));
    }

    public void initAdapter() {
        mModel.resetPage();
        mSubscriptions.add(mModel.getRxRequests(HomeModel.FROM_CACHE).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Request>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (isAlive()){
                            mView.showError("仿佛网络有点差");
                            mView.onRefreshFail();
                        }
                    }


                    @Override
                    public void onNext(List<Request> requests) {
                        if(isAlive()){
                            mModel.setDatas(requests);
                            mView.setAdapter(mModel.getDatas());
                        }

                    }
                }));
    }


    public void onResume() {
        initAdapter();
    }


    public void onPause() {
        mSubscriptions.unsubscribe();
    }
}
