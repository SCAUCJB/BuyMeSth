package edu.scau.buymesth.user;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import edu.scau.buymesth.data.bean.User;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by John on 2016/8/31.
 */
public class UserPresenter implements UserContract.Presenter {
    private final UserContract.View mView;
    private final UserModel mModel;
    CompositeSubscription mSubscriptions = new CompositeSubscription();

    UserPresenter(UserContract.View view, UserModel model) {
        mView = view;
        mModel = model;
    }

    public void subscribe() {
        showUserInfo();

    }


    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void showUserInfo() {
        mSubscriptions.add(mModel.getUser(BmobUser.getCurrentUser().getObjectId())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onNext(User user) {
                        mView.setAvatar(user.getAvatar());
                        if (user.getSignature() != null)
                            mView.setSignature("个性签名：" + user.getSignature());
                        else
                            mView.setSignature("还没有个性签名~");
                        mView.setLevel(user.getExp());
                        if (user.getResidence() == null) {
                            mView.setlocation("未知的位置");
                        } else
                            mView.setlocation(user.getResidence());
                        mView.setUserName(user.getNickname());
                        mView.setUserId(user.getUsername());
                    }
                }));
        mSubscriptions.add(mModel.getEvaluateCount(BmobUser.getCurrentUser().getObjectId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        mView.setEvaluateCount(integer);
                    }
                }));
        mSubscriptions.add(mModel.getScore(BmobUser.getCurrentUser().getObjectId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONArray>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(JSONArray jsonArray) {
                        if (jsonArray != null) {
                            JSONObject obj = null;
                            try {
                                obj = jsonArray.getJSONObject(0);
                                double score = obj.getDouble("_avgScore");
                                mView.setScore(score + "");
                                mView.setRatingBar((float) score);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }));
    }

    @Override
    public void showTab() {
        mView.initTab();
    }

}
