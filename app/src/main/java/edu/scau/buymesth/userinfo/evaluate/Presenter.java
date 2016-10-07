package edu.scau.buymesth.userinfo.evaluate;


import java.util.List;

import cn.bmob.v3.BmobQuery;
import edu.scau.buymesth.data.bean.Evaluate;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by John on 2016/10/7.
 */

public class Presenter {
    Model mModel;
    Contract.View mView;
    CompositeSubscription mSubscriptions = new CompositeSubscription();

    public Presenter(Contract.View view, Model model) {
        mView = view;
        mModel = model;
    }

    public void initView() {
        mSubscriptions.add(mModel.getEvaluates(BmobQuery.CachePolicy.CACHE_ONLY).subscribe(new Subscriber<List<Evaluate>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(List<Evaluate> evaluates) {
                mView.setEvaluates(evaluates);
            }
        }));
    }

    public void subscribe() {
        mModel.pageNum = 0;
        mSubscriptions.add(mModel.getEvaluates(BmobQuery.CachePolicy.NETWORK_ONLY).subscribe(new Subscriber<List<Evaluate>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(List<Evaluate> evaluates) {

                mView.setEvaluates(evaluates);

            }
        }));
    }

    public void unsubscribe() {
        mSubscriptions.clear();
    }

    public void onLoadMore() {
        mSubscriptions.add(mModel.getEvaluates(BmobQuery.CachePolicy.NETWORK_ONLY).subscribe(new Subscriber<List<Evaluate>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {
                mView.onLoadMore(null);
            }

            @Override
            public void onNext(List<Evaluate> evaluates) {
                mView.setEvaluates(evaluates);
                if (evaluates == null) {
                    mView.onLoadMore(null);
                }
                     else {
                    mView.onLoadMore(evaluates);
                }
            }
        }));
    }
}
