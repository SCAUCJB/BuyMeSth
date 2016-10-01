package edu.scau.buymesth.user;


import android.util.Log;

import cn.bmob.v3.BmobUser;
import edu.scau.buymesth.data.bean.User;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by John on 2016/8/31.
 */
public   class UserPresenter  implements UserContract.Presenter{
    private final UserContract.View mView;
    private final UserModel mModel;
    CompositeSubscription mSubscriptions=new CompositeSubscription();
    UserPresenter(UserContract.View view, UserModel model) {
        mView = view;
        mModel = model;
    }
    public void subscribe() {
        showUserInfo();
        showTab();
    }


    public void unsubscribe() {
        mSubscriptions.unsubscribe();
    }

    @Override
    public void showUserInfo() {
        Subscription subscription=mModel.getUser(BmobUser.getCurrentUser().getObjectId()).subscribe(new Subscriber<User>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onNext(User user) {
                Log.d("zhx","onnext");
                Log.d("zhx","user="+user.toString());
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
        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void showTab() {
        mView.initTab();
    }

}
