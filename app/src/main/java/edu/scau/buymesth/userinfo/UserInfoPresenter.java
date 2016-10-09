package edu.scau.buymesth.userinfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

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
 * Created by John on 2016/9/24.
 */

public class UserInfoPresenter implements Contract.Presenter {

    private final Contract.View mView;
    public final UserInfoModel mModel;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    UserInfoPresenter(Contract.View view, UserInfoModel model) {
        mView = view;
        mModel = model;
    }

    public void subscribe() {
        showUserInfo();
        showTab();
    }


    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void showUserInfo() {
        User user = mModel.getUser();
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
                                DecimalFormat decimalFormat=new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                                String p= decimalFormat.format(score);//format 返回的是字符串
                                mView.setScore(p + "");
                                mView.setRatingBar((float) score);
                            } catch (JSONException e) {
                                mView.setScore("0.0");
                                mView.setRatingBar((float) 0.1);
                                e.printStackTrace();
                            }
                        }
                    }
                }));
    }

    public void follow() {
        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
        //第一个参数是上下文对象，第二个参数是云端逻辑的方法名称，第三个参数是上传到云端逻辑的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
        JSONObject params = new JSONObject();
        try {
            params.put("fromUser", BmobUser.getCurrentUser().getObjectId());
            params.put("toUser", mModel.getUser().getObjectId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ace.callEndpoint("follow", params, new CloudCodeListener() {
            @Override
            public void done(Object o, BmobException e) {
                if (e != null) return;
                if (o != null) {
                    if ((o.toString()).equals("true")) {
                        mView.setFollow(true);
                    } else {
                        mView.setFollow(false);
                    }
                }
            }
        });
    }

    @Override
    public void showTab() {
        mView.initTab();
    }
}
