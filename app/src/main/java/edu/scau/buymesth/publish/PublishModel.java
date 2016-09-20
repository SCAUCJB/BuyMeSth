package edu.scau.buymesth.publish;

import android.util.Log;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import edu.scau.buymesth.data.bean.Request;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Jammy on 2016/8/16.
 */
public class PublishModel implements  PublishContract.Model {
    Request request ;
    @Override
    public void submit( List<String> picHeights,List<String> picWidths,Subscriber<Integer> subscriber, List<String> list) {
         Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                //排除加号图片的URL
                if (list .size()>1) {
                    list.remove(list.size()-1);
                    String[] fileList = new String[list.size()];
                    list.toArray(fileList);

                    BmobFile.uploadBatch(fileList, new UploadBatchListener() {
                        @Override
                        public void onSuccess(List<BmobFile> files, List<String> urls) {
                            if (urls.size() == fileList.length) {//如果数量相等，则代表文件全部上传完成
                                request.setUrls(urls);
                                request.setPicHeights(picHeights);
                                request.setPicWidths(picWidths);
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
                        public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                            //1、curIndex--表示当前第几个文件正在上传
                            //2、curPercent--表示当前上传文件的进度值（百分比）
                            //3、total--表示总的上传文件数
                            //4、totalPercent--表示总的上传进度（百分比）
                            subscriber.onNext(curPercent);
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

    @Override
    public void setRequest(Request request) {
        this.request=request;
    }
}
