package edu.scau.buymesth.discover.detail;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import base.BasePresenter;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.data.bean.MomentsComment;
import edu.scau.buymesth.data.bean.MomentsLike;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by IamRabbit on 2016/8/24.
 */
public class MomentDetailPresenter extends BasePresenter<MomentDetailContract.Model, MomentDetailContract.View> {
    private Context mContext;

    public MomentDetailPresenter(Context context) {
        mContext = context;
    }

    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        } else {
            return false;
        }
    }

    @Override
    public void onStart() {

    }

    public void Refresh() {
        if (mModel.getMoment() == null) return;
        BmobQuery<Moment> bmobQuery = new BmobQuery<>();
        bmobQuery.include("user");
        try {
            //这里捕获一个Bmob在没网络的时候抛出的异常
            bmobQuery.getObject(mModel.getMoment().getObjectId(), new QueryListener<Moment>() {
                @Override
                public void done(Moment moment, BmobException e) {
                    //不然在没网的时候返回会报空 指针异常
                    if (e != null) return;
                    mModel.setMoment(moment);
                    mView.initMomentView();
                }
            });
        } catch (RuntimeException e) {
            Log.e("zhx", "网络异常");
        }
        BmobQuery<MomentsLike> bmobQuery1 = new BmobQuery<>();
        bmobQuery1.addWhereEqualTo("liker", BmobUser.getCurrentUser().getObjectId());
        bmobQuery1.addWhereEqualTo("moment", mModel.getMoment().getObjectId());
        bmobQuery1.findObjects(new FindListener<MomentsLike>() {
            @Override
            public void done(List<MomentsLike> list, BmobException e) {
                if (list != null && list.size() > 0) {
                    mModel.getMoment().setLike(true);
                    mView.setLike(true, 0);
                } else {
                    mModel.getMoment().setLike(true);
                    mView.setLike(false, 0);
                }
            }
        });
        RefreshComments();
    }

    public void RefreshComments() {
        if (mModel.getMoment() != null) {
            mModel.resetPage();
            mModel.getDatas().clear();
            BmobQuery.CachePolicy cachePolicy;
            if (isOpenNetwork()) cachePolicy = BmobQuery.CachePolicy.CACHE_THEN_NETWORK;
            else cachePolicy = BmobQuery.CachePolicy.CACHE_ONLY;
            mSubscriptions.add(mModel.getRxComments(cachePolicy).flatMap(new Func1<List<MomentsComment>, Observable<MomentsComment>>() {
                @Override
                public Observable<MomentsComment> call(List<MomentsComment> moments) {
                    return Observable.from(moments);
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<MomentsComment>() {
                        int size = mModel.getDatas().size();

                        @Override
                        public void onCompleted() {
                            if (isAlive()) {
                                mView.onRefreshComplete(mModel.getDatas());
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            if (isAlive()) {
                                mView.onError(null, "获取数据出了些问题");
                                mView.onRefreshInterrupt();
                            }
                        }

                        @Override
                        public void onNext(MomentsComment momentsComment) {
                            mModel.getDatas().add(momentsComment);
                        }
                    }));
        }
    }

    public void LoadMore() {
        BmobQuery.CachePolicy cachePolicy;
        if (isOpenNetwork()) cachePolicy = BmobQuery.CachePolicy.CACHE_THEN_NETWORK;
        else cachePolicy = BmobQuery.CachePolicy.CACHE_ONLY;
        List<MomentsComment> tempList = new LinkedList<>();
        mSubscriptions.add(mModel.getRxComments(cachePolicy).flatMap(new Func1<List<MomentsComment>, Observable<MomentsComment>>() {
            @Override
            public Observable<MomentsComment> call(List<MomentsComment> momentsComment) {
                return Observable.from(momentsComment);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MomentsComment>() {
                    @Override
                    public void onCompleted() {
                        mView.onLoadMoreSuccess(tempList);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mView.onError(null, "获取数据出了些问题");
                        mView.onLoadMoreInterrupt();
                    }

                    @Override
                    public void onNext(MomentsComment momentsComment) {
                        mModel.getDatas().add(momentsComment);
                        tempList.add(momentsComment);
                    }
                }));
    }

    void postComment(String text) {
        //应改写在云逻辑上
        MomentsComment momentsComment = new MomentsComment();
        momentsComment.setContent(text);
        momentsComment.setUser(BmobUser.getCurrentUser(User.class));
        momentsComment.setMoment(mModel.getMoment());
        momentsComment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    mView.onPostCommentSuccess(s);
                }
            }
        });
        Moment moment = new Moment();
        moment.setObjectId(mModel.getMoment().getObjectId());
        moment.increment("comments");
        moment.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                System.out.println(e == null ? "PPPPPPPPPPPP" : e.toString());
            }
        });
    }

    public void like() {
        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
        //第一个参数是上下文对象，第二个参数是云端逻辑的方法名称，第三个参数是上传到云端逻辑的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
        JSONObject params = new JSONObject();
        try {
            params.put("liker", BmobUser.getCurrentUser().getObjectId());
            params.put("moment", mModel.getMoment().getObjectId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ace.callEndpoint("like", params, new CloudCodeListener() {
            @Override
            public void done(Object o, BmobException e) {
                if (o != null) {
                    if (((String) o).equals("true")) {
                        if (mModel.getMoment().isLike()) return;
                        mView.setLike(true, 1);
                    } else {
                        if (!mModel.getMoment().isLike()) return;
                        mView.setLike(false, 0);
                    }
                }
            }
        });
    }
}
