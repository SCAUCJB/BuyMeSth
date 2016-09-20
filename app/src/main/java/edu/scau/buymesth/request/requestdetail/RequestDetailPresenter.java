package edu.scau.buymesth.request.requestdetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import base.BasePresenter;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import edu.scau.buymesth.data.bean.Collect;
import edu.scau.buymesth.data.bean.Comment;
import edu.scau.buymesth.data.bean.Follow;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by John on 2016/8/23.
 */

public class RequestDetailPresenter extends BasePresenter<RequestDetailContract.Model, RequestDetailContract.View> {
    public boolean mNeedQueryRequest = false;

    @Override
    public void onStart() {
        if (mNeedQueryRequest) {
            try {
                initRequestUgly();
            } catch (RuntimeException e) {
                mView.toast("请打开网络");
            }
        } else {
            try {
                initUserInfo();
                initCommentBar();
                initContent();
                initPrice();
                initComment();
                initTags();
                initFollow();
                initCollect();
            } catch (RuntimeException e) {
                mView.toast("请打开网络");
            }
        }
    }

    private void initRequestUgly() {
        BmobQuery<Request> bmobQuery = new BmobQuery<>();
        bmobQuery.include("user");


        bmobQuery.getObject(mModel.getRequest().getObjectId(), new QueryListener<Request>() {
            @Override
            public void done(Request request, BmobException e) {
                if (e != null) return;
                mModel.setRequest(request);

                initUserInfo();
                initCommentBar();
                initContent();
                initPrice();
                initComment();
                initTags();
                initFollow();
                initCollect();
            }
        });
    }

    void collect() {
        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
        //第一个参数是上下文对象，第二个参数是云端逻辑的方法名称，第三个参数是上传到云端逻辑的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
        JSONObject params = new JSONObject();
        try {
            params.put("userid", BmobUser.getCurrentUser().getObjectId());
            params.put("requestid", mModel.getRequest().getObjectId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ace.callEndpoint("collect", params, new CloudCodeListener() {
            @Override
            public void done(Object o, BmobException e) {
                initCollect();
            }
        });
    }

    void initCollect() {
        BmobQuery<Collect> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("user", BmobUser.getCurrentUser());
        bmobQuery.addWhereEqualTo("request", mModel.getRequest());
        bmobQuery.findObjects(new FindListener<Collect>() {
            @Override
            public void done(List<Collect> list, BmobException e) {
                if (e != null) return;
                if (list != null && list.size() > 0) {
                    //collected
                    mView.setCollect(true);
                } else {
                    mView.setCollect(false);
                }
            }
        });
    }

    void follow() {
        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
        //第一个参数是上下文对象，第二个参数是云端逻辑的方法名称，第三个参数是上传到云端逻辑的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
        JSONObject params = new JSONObject();
        try {
            params.put("fromUser", BmobUser.getCurrentUser().getObjectId());
            params.put("toUser", mModel.getRequest().getUser().getObjectId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ace.callEndpoint("follow", params, new CloudCodeListener() {
            @Override
            public void done(Object o, BmobException e) {
                if (e != null) return;
                if (o != null) {
                    if (((String) o).equals("true")) {
                        mView.setFollow(true);
                    } else {
                        mView.setFollow(false);
                    }
                }
            }
        });
    }

    private void initFollow() {
        BmobQuery<Follow> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("fromUser", BmobUser.getCurrentUser(User.class));
        bmobQuery.addWhereEqualTo("toUser", mModel.getRequest().getUser());
        bmobQuery.findObjects(new FindListener<Follow>() {
            @Override
            public void done(List<Follow> list, BmobException e) {
                if (e != null) return;
                if (list != null && list.size() > 0) {
                    //followed
                    mView.setFollow(true);
                } else {
                    mView.setFollow(false);
                }
            }
        });
    }

    private void initPrice() {
        Integer high = mModel.getRequest().getMaxPrice();
        Integer low = mModel.getRequest().getMinPrice();
        if (low != null) {
            mView.setPrice("期望价格：￥" + low + "~￥" + high);
        } else {
            mView.setPrice("期望价格：￥" + high);
        }
    }

    public void initUserInfo() {
        Request request = mModel.getRequest();
        mView.setAuthorAvatar(request.getUser().getAvatar());
        mView.setAuthorExp(request.getUser().getExp());
        mView.setAuthorName(request.getUser().getNickname());
        mView.setAuthorOnClicked();
        mView.setOnAcceptClicked();
        //
        mView.setOnCollectClicked();
    }

    public void initCommentBar() {
        mView.setCommentBtn(mModel.getCommentBtnStr());
        mView.setUserAvatar();
    }

    public void initContent() {
        Request request = mModel.getRequest();
        mView.setTitle(request.getTitle());
        mView.setContent(request.getContent());
        mView.setLikes(request.getLikes());
        mView.setTime(request.getCreatedAt());

        if (request.getUrls() != null) {
            mView.setUpViewPager(request.getPicHeights(), request.getPicWidths(), request.getUrls());
        } else {
            mView.hideViewPager();
        }
    }

    void initComment() {
        mModel.getRxComment(mModel.getRequest().getObjectId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Comment>>() {
            @Override
            public void onCompleted() {
                mView.setComment(mModel.getCommentList());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(List<Comment> comments) {
                mModel.setCommentList(comments);
            }
        });
    }

    public void initTags() {
        if (mModel.getRequest().getTags() != null)
            mView.setTagList(mModel.getRequest().getTags());
    }

    public Request getRequest() {
        return mModel.getRequest();
    }

    void onResume() {
        initComment();
    }

}
