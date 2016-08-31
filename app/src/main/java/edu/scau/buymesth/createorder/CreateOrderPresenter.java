package edu.scau.buymesth.createorder;

import base.BasePresenter;

/**
 * Created by John on 2016/8/30.
 */

public class CreateOrderPresenter extends BasePresenter<CreateOrderContract.Model,CreateOrderContract.View> {
    @Override
    public void onStart() {
        mView.setRequestInfo(mModel.getBuyer(),mModel.getRequest().getTitle(),mModel.getRequest().getContent(),mModel.getRequest().getCreatedAt());
        if(mModel.getRequest().getTags()!=null)
        mView.setTagList(mModel.getRequest().getTags());
        mView.setDeliverTime(mModel.getYear(),mModel.getMonth(),mModel.getDay());
    }
    public void onDeliverTimeClicked(){
        mView.showDatePickDialog(mModel.getYear(),mModel.getMonth(),mModel.getDay());
    }

    public void addTag(String tag) {
        mModel.getRequest().getTags().add(tag);
    }
}
