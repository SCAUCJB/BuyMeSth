package edu.scau.buymesth.home;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import base.BasePresenter;
import edu.scau.buymesth.data.bean.Request;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by John on 2016/8/5.
 */

public class HomePresenter extends BasePresenter<HomeContract.Model, HomeContract.View> {

    /**
     * 初始化工作写在这里
     */
    @Override
    public void onStart() {

    }

    /**
     * Created by John on 2016/8/9
     * 调用比目云的接口，拿到分页数据，先把列表转换成单个数据再分别添加到数据集里面，这个数据集只需要存储新拿到的数据即可，所以要在之前做clear操作，
     * 最后通过view来通知adapter部分更新recycler view，性能极佳。
     * 但是现在还没处理已经没有数据了的情况
     */
    public void onLoadMore() {
        List<Request> tempList = new LinkedList<>();
        mModel.getRxRequests().flatMap(new Func1<List<Request>, Observable<Request>>() {
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
                        if (isAlive())
                            mView.showError("获取数据出了些问题");
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
        mModel.getDatas().clear();
        mModel.getRxRequests().flatMap(new Func1<List<Request>, Observable<Request>>() {
            @Override
            public Observable<Request> call(List<Request> requests) {
                return Observable.from(requests);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Request>() {
                    @Override
                    public void onCompleted() {
                        if(isAlive()) mView.onRefreshComplete(mModel.getDatas());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if(isAlive())  mView.showError("获取数据出了些问题");
                    }

                    @Override
                    public void onNext(Request request) {
                        if(isAlive()) mModel.getDatas().add(request);
                    }
                });
    }
}
