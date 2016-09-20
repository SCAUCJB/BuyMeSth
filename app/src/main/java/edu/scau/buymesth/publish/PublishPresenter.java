package edu.scau.buymesth.publish;

import java.util.List;

import base.BasePresenter;
import edu.scau.buymesth.data.bean.Request;
import rx.Subscriber;

/**
 * Created by Jammy on 2016/8/16.
 */
public class PublishPresenter extends BasePresenter<PublishContract.Model, PublishContract.View> {
    @Override
    public void onStart() {

    }

      void submit( List<String> picHeights,List<String> picWidths, List<String> list) {

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {

            @Override
            public void onCompleted() {
                mView.onSubmitFinish();
            }

            @Override
            public void onError(Throwable throwable) {
                mView.onSubmitFail();
            }

            @Override
            public void onNext(Integer progress) {
                mView.setProgress(progress);
            }
        };
        mModel.submit(picHeights,picWidths, subscriber, list);
    }


    public void setRequest(Request request) {
        mModel.setRequest(request);
    }
}
