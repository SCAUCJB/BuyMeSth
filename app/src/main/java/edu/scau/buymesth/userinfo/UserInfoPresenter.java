package edu.scau.buymesth.userinfo;

import edu.scau.buymesth.data.bean.User;

/**
 * Created by John on 2016/9/24.
 */

public class UserInfoPresenter implements Contract.Presenter {

    private final Contract.View mView;
    private final UserInfoModel mModel;

    UserInfoPresenter(Contract.View view, UserInfoModel model) {
        mView = view;
        mModel = model;
    }

    public void subscribe() {
        showUserInfo();
        showTab();
    }


    public void unsubscribe() {

    }

    @Override
    public void showUserInfo() {
        User user = mModel.getUser();
        mView.setAvatar(user.getAvatar());
        if (user.getSignature() != null)
            mView.setSignature(user.getSignature());
        else
            mView.setSignature("还没有个性签名~");
        mView.setLevel(user.getExp());
        if (user.getResidence() == null) {
            mView.setlocation("未知的位置");
        } else
            mView.setlocation(user.getResidence());
        mView.setUserName(user.getNickname());
        if (user.getScore() != null) {
            mView.setScore(user.getScore() + "分");
            mView.setRatingBar(user.getScore());
            mView.setPopulation(user.getRatePop() + "人评价");
        } else {
            mView.setScore("5分");
            mView.setRatingBar(5.0f);
            mView.setPopulation("0人评价");
        }
    }

    @Override
    public void showTab() {
        mView.initTab();
    }
}
