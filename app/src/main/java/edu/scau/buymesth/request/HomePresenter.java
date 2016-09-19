package edu.scau.buymesth.request;

import java.util.LinkedList;
import java.util.List;

import base.BasePresenter;
import base.util.ToastUtil;
import cn.bmob.v3.datatype.BmobQueryResult;
import edu.scau.buymesth.data.bean.Request;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by John on 2016/8/5.
 */

public class HomePresenter extends BasePresenter<HomeContract.Model, HomeContract.View> {

    public static final String FILTER_AUTHOR_ID = "USERID";
    public static final String FILTER_AUTHOR_IDS = "USERIDS";
    public static final String FILTER_FUZZY_SEARCH = "FUZZY";
    public static final String FILTER_FOLLOW_ONLY = "FOLLOW";
    private Subscription mSubscription=null;

    /**
     * 初始化工作写在这里
     */
    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if(mSubscription!=null&&!mSubscription.isUnsubscribed())
        mSubscription.unsubscribe();
        super.onDestroy();

    }

    /**
     * Created by John on 2016/8/9
     * 调用比目云的接口，拿到分页数据，先把列表转换成单个数据再分别添加到数据集里面，这个数据集只需要存储新拿到的数据即可，所以要在之前做clear操作，
     * 最后通过view来通知adapter部分更新recycler view，性能极佳。
     * 但是现在还没处理已经没有数据了的情况
     */
    public void onLoadMore() {
        List<Request> tempList = new LinkedList<>();
        mModel.getRxRequests(HomeModel.FROM_NETWORK).flatMap(new Func1<List<Request>, Observable<Request>>() {
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
                });
    }

    /**
     * Created by John on 2016/8/9
     * 这里的处理有点粗暴，直接把新数据拿下来覆盖旧数据而不是在头部追加，待优化
     */
    public void onRefresh() {
        mModel.resetPage();
        List<Request> tempList=new LinkedList<>();
        mModel.getRxRequests(HomeModel.FROM_NETWORK).flatMap(new Func1<List<Request>, Observable<Request>>() {
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
                });
    }

    public void onRefresh(String filter , Object key) {
        Observable<List<Request>> requestObservable;
        mModel.resetPage();
        List<Request> tempList=new LinkedList<>();

        if(filter==FILTER_AUTHOR_ID){
            requestObservable = mModel.getSomeonesRxRequests(HomeModel.FROM_NETWORK,(String) key);
        }else if(filter==FILTER_AUTHOR_IDS){
            requestObservable = mModel.getSomeonesRxRequests(HomeModel.FROM_NETWORK,(List<String>)key);
        }else if(filter==FILTER_FUZZY_SEARCH){
            requestObservable = mModel.getFuzzySearchRxRequests(HomeModel.FROM_NETWORK,(String)key);
        }else if(filter == FILTER_FOLLOW_ONLY){
            Observable<BmobQueryResult<Request>> bqlRequestObservable = mModel.getFollowedRxRequests(HomeModel.FROM_NETWORK, (String) key);
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
            requestObservable = mModel.getRxRequests(HomeModel.FROM_NETWORK);
        }

        mSubscription=requestObservable.flatMap(new Func1<List<Request>, Observable<Request>>() {
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
                });
    }

    public void onLoadMore(String filter , Object key) {
        Observable<List<Request>> requestObservable;
        List<Request> tempList = new LinkedList<>();

        if(filter==FILTER_AUTHOR_ID){
            requestObservable = mModel.getSomeonesRxRequests(HomeModel.FROM_NETWORK,(String) key);
        }else if(filter==FILTER_AUTHOR_IDS){
            requestObservable = mModel.getSomeonesRxRequests(HomeModel.FROM_NETWORK,(List<String>)key);
        }else if(filter==FILTER_FUZZY_SEARCH){
            requestObservable = mModel.getFuzzySearchRxRequests(HomeModel.FROM_NETWORK,(String)key);
        }else if(filter == FILTER_FOLLOW_ONLY){
            Observable<BmobQueryResult<Request>> bqlRequestObservable = mModel.getFollowedRxRequests(HomeModel.FROM_NETWORK, (String) key);
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
                    });
            return;
        }else {
            requestObservable = mModel.getRxRequests(HomeModel.FROM_NETWORK);
        }

        requestObservable.flatMap(new Func1<List<Request>, Observable<Request>>() {
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
                });
    }

    public void initAdapter() {
        mModel.resetPage();
        mModel.getDatas().clear();
        mModel.getRxRequests(HomeModel.FROM_CACHE).flatMap(new Func1<List<Request>, Observable<Request>>() {
            @Override
            public Observable<Request> call(List<Request> requests) {
                return Observable.from(requests);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Request>() {
                    @Override
                    public void onCompleted() {
                        if(isAlive()) mView.setAdapter(mModel.getDatas());
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
                        if(isAlive()) mModel.getDatas().add(request);
                    }
                });
    }
}
