package edu.scau.buymesth.publish;

import java.util.List;

import base.BasePresenter;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.home.HomeContract;
import rx.Observable;
import rx.Observer;
import rx.Single;
import rx.SingleSubscriber;

/**
 * Created by Jammy on 2016/8/16.
 */
public class PublishPresenter extends BasePresenter<PublishContract.Model, PublishContract.View>{
    @Override
    public void onStart() {

    }

    public void submit(Request request, List<PhotoInfo> list) {

        SingleSubscriber singleSubscriber = new SingleSubscriber() {
            @Override
            public void onSuccess(Object o) {
                mView.onSubmitFinish();
            }

            @Override
            public void onError(Throwable throwable) {
                mView.onSubmitFail();
            }
        };

        mModel.submit(request,singleSubscriber,list);
    }


}
