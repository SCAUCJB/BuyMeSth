package edu.scau.buymesth.createorder;

import java.util.LinkedList;

import base.BasePresenter;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by John on 2016/8/30.
 */

class CreateOrderPresenter extends BasePresenter<CreateOrderContract.Model,CreateOrderContract.View> {
    @Override
    public void onStart() {
        mView.setRequestInfo(mModel.getBuyer(), mModel.getRequest().getTitle(), mModel.getRequest().getContent(), mModel.getRequest().getCreatedAt());
        if (mModel.getRequest().getTags() != null)
            mView.setTagList(mModel.getRequest().getTags());
        mView.setDeliverTime(mModel.getYear(), mModel.getMonth()+1, mModel.getDay());
        mView.initPickerView();
        mModel.setSeller(mView.getSeller());
        mModel.getOrder().setTags(new LinkedList<>());
    }

    void onDeliverTimeClicked() {
        mView.showDatePickDialog(mModel.getYear(), mModel.getMonth(), mModel.getDay());
    }

    void addTag(String tag) {
        mModel.getOrder().getTags().add(tag);
    }

    void removeTag(String tag) {
        mModel.getOrder().getTags().remove(tag);
    }

    void setPriceType(String text) {
        mModel.setPriceType(text);
    }

    void setTipType(String text) {
        mModel.setTipType(text);
    }

    void onSubmitClicked() {
        try {
            mModel.setTip(Float.valueOf(mView.getTip()));
        } catch (NumberFormatException e) {
            mView.showMsg("服务费只能是数字和小数点");
            return;
        }
        try {
            mModel.setPrice(Float.valueOf(mView.getPrice()));
        } catch (NumberFormatException e) {
            mView.showMsg("价格只能是数字和小数点");
            return;
        }
        mView.showLoadingDialog();
        mModel.submit().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {
                mView.closeLoadingDialog();
                mView.showMsg("网络太差了");
            }

            @Override
            public void onNext(String s) {
                mView.closeLoadingDialog();
                mView.showMsg("发送成功");
                mView.exit();
            }
        });

    }
}