package edu.scau.buymesth.publish;

import java.util.List;

import base.BasePresenter;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.data.bean.Request;
import rx.Subscriber;

/**
 * Created by Jammy on 2016/8/16.
 */
public class PublishPresenter extends BasePresenter<PublishContract.Model, PublishContract.View> {
    @Override
    public void onStart() {

    }

    public void submit(Request request, List<PhotoInfo> list) {
        mView.showLoadingDialog();
        Subscriber<String> subscriber = new Subscriber<String>() {

            @Override
            public void onCompleted() {
                mView.onSubmitFinish();
                mView.closeLoadingDialog();
            }

            @Override
            public void onError(Throwable throwable) {
                mView.onSubmitFail();
                mView.closeLoadingDialog();
            }

            @Override
            public void onNext(String s) {

            }
        };
        mModel.submit(request, subscriber, list);
    }


}
