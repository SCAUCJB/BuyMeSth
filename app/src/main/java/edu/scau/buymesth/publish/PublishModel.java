package edu.scau.buymesth.publish;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import base.BaseModel;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.data.bean.Request;
import rx.Observable;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Jammy on 2016/8/16.
 */
public class PublishModel implements BaseModel, PublishContract.Model {

    @Override
    public void submit(Request request, Subscriber<String> subscriber, List<PhotoInfo> list) {



         Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                List<PhotoInfo> photoInfoList = new ArrayList<>(list);
                if (photoInfoList != null && photoInfoList.size() != 0) {
                    if(photoInfoList.get(photoInfoList.size()-1).getPhotoPath()==null){
                        photoInfoList.remove(photoInfoList.size()-1);
                    }
                    String[] fileList = new String[photoInfoList.size()];
                    for(int i=0;i<photoInfoList.size();i++){
                        fileList[i]=photoInfoList.get(i).getPhotoPath();
                    }
                    BmobFile.uploadBatch(fileList, new UploadBatchListener() {
                        @Override
                        public void onSuccess(List<BmobFile> files, List<String> urls) {
                            if (urls.size() == fileList.length) {//如果数量相等，则代表文件全部上传完成
                                request.setUrls(urls);
                                request.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        ////这里进行回调
                                        if (e == null) {
                                            subscriber.onCompleted();
                                        } else {
                                            Log.e("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                            subscriber.onError(e);
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onProgress(int i, int i1, int i2, int i3) {

                        }

                        @Override
                        public void onError(int i, String s) {
                            subscriber.onError(new Throwable());
                        }
                    });
                } else {
                    request.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            ////这里进行回调
                            if (e == null) {
                                subscriber.onCompleted();
                            } else {
                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                subscriber.onError(e);
                            }
                        }
                    });
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
