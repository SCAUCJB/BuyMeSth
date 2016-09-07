package edu.scau.buymesth.discover.list;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import base.BasePresenter;
import base.util.ToastUtil;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.data.bean.MomentsLike;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by IamRabbit on 2016/8/10.
 */
public class DiscoverPresenter extends BasePresenter<DiscoverContract.Model,DiscoverContract.View> {

    private Context mContext;

    public DiscoverPresenter(Context context){
        this.mContext = context;
    }

    @Override
    public void onStart() {
    }

    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connManager.getActiveNetworkInfo()!=null){
            return connManager.getActiveNetworkInfo().isAvailable();
        }else {
            return false;
        }
    }

    public void Refresh(){
        mModel.updateLikeList();
        mModel.resetPage();
        mModel.getDatas().clear();
        BmobQuery.CachePolicy cachePolicy;
        if(isOpenNetwork())cachePolicy = BmobQuery.CachePolicy.CACHE_THEN_NETWORK;
        else cachePolicy = BmobQuery.CachePolicy.CACHE_ONLY;
        mModel.getRxMoments(cachePolicy).flatMap(new Func1<List<Moment>, Observable<Moment>>() {
            @Override
            public Observable<Moment> call(List<Moment> moments) {
                return Observable.from(moments);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Moment>() {
                    int size = mModel.getDatas().size();
                    @Override
                    public void onCompleted() {
                        if(isAlive()){
                            mView.onRefreshComplete(mModel.getDatas());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if(isAlive()){
                            mView.onError(null,"获取数据出了些问题");
                            mView.onRefreshInterrupt();
                        }
                    }

                    @Override
                    public void onNext(Moment moment) {
                        boolean add = true;
                        for(int i = 0;i<size;i++){
                            if(moment.getObjectId().equals(mModel.getDatas().get(i).getObjectId())){
                                add = false;
                            }
                        }
                        if(add){
                            for(MomentsLike obj : mModel.getLikesList()){
                                if(obj.getMoment().getObjectId().contains(moment.getObjectId())) {
                                    moment.setLike(true);
                                    break;
                                }
                            }
                            mModel.getDatas().add(moment);
                        }
                    }
                });
    }

    public void LoadMore(){
        BmobQuery.CachePolicy cachePolicy;
        if(isOpenNetwork())cachePolicy = BmobQuery.CachePolicy.CACHE_THEN_NETWORK;
        else cachePolicy = BmobQuery.CachePolicy.CACHE_ONLY;
        List<Moment> tempList=new LinkedList<>();
        mModel.getRxMoments(cachePolicy).flatMap(new Func1<List<Moment>, Observable<Moment>>() {
            @Override
            public Observable<Moment> call(List<Moment> moments) {
                return Observable.from(moments);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Moment>() {
                    @Override
                    public void onCompleted() {
                        mView.onLoadMoreSuccess(tempList);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mView.onError(throwable, "获取数据出了些问题");
                    }

                    @Override
                    public void onNext(Moment moment) {
                        mModel.getDatas().add(moment);
                        tempList.add(moment);
                    }
                });
    }

    public void AddLike(View v, Moment item, int position){
        User user = BmobUser.getCurrentUser(User.class);
        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
        //第一个参数是上下文对象，第二个参数是云端逻辑的方法名称，第三个参数是上传到云端逻辑的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
        JSONObject params = new JSONObject();
        try {
            params.put("userid",user.getObjectId());
            params.put("momentsid",item.getObjectId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ace.callEndpoint("likemoment",params , new CloudCodeListener() {
            @Override
            public void done(Object o, BmobException e) {
                if(!((String)o).contains("succeed")){
                    DisLike(v,item,position);
                }else {
                    mView.onError(null,o.toString());
                    item.setLikes(item.getLikes()+1);
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView)v.findViewById(R.id.iv_likes))
                                    .setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_red)
                            );
                            ((TextView)v.findViewById(R.id.tv_likes)).setText(String.valueOf(item.getLikes()));
                        }
                    });
                }
            }
        });
    }

    public void DisLike(View v, Moment item, int position){
        User user = BmobUser.getCurrentUser(User.class);
        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
        //第一个参数是上下文对象，第二个参数是云端逻辑的方法名称，第三个参数是上传到云端逻辑的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
        JSONObject params = new JSONObject();
        try {
            params.put("userid",user.getObjectId());
            params.put("momentsid",item.getObjectId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ace.callEndpoint("dislikemoment",params , new CloudCodeListener() {
            @Override
            public void done(Object o, BmobException e) {
                if(((String)o).contains("succeed")){
                    mView.onError(null,o.toString());
                    item.setLikes(item.getLikes()-1);
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView)v.findViewById(R.id.iv_likes))
                                    .setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite)
                            );
                            ((TextView)v.findViewById(R.id.tv_likes)).setText(String.valueOf(item.getLikes()));
                        }
                    });
                }else {
                    mView.onError(null,o.toString());
                }
            }
        });
    }

    public void DeleteOne(Moment moment,int position){
        if(moment.getAuthor().getObjectId().equals(BmobUser.getCurrentUser().getObjectId())){
            new AlertDialog.Builder(mContext)
                    .setTitle(mContext.getResources().getString(R.string.text_delete))
                    .setMessage("delete ?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Moment deleteMoment = new Moment();
                            deleteMoment.setObjectId(moment.getObjectId());
                            deleteMoment.setAuthorDelete(true);
                            deleteMoment.delete(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null)
                                        mView.onDeleteSuccess(moment.getObjectId(),position);
                                    else
                                        mView.onError(null,e.toString());
                                }
                            });
                        }
                    })
                    .setNegativeButton("no",null)
                    .show();
        }
    }
}
