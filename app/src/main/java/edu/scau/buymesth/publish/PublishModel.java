package edu.scau.buymesth.publish;

import android.util.Log;

import java.util.List;

import base.BaseModel;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.data.bean.Request;
import rx.Observer;
import rx.SingleSubscriber;

/**
 * Created by Jammy on 2016/8/16.
 */
public class PublishModel implements BaseModel, PublishContract.Model {

    @Override
    public void submit(Request request, SingleSubscriber<String> observable, List<PhotoInfo> list) {

        if (list != null && list.size() != 0) {
            String[] fileList = new String[list.size()];
            for(int i=0;i<list.size();i++){
                fileList[i]=list.get(i).getPhotoPath();
            }
            BmobFile.uploadBatch(fileList, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> files, List<String> urls) {
                    if (urls.size() == fileList.length) {//如果数量相等，则代表文件全部上传完成
                        //do something
                        request.setUrls(urls);
                        request.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                ////这里进行回调
                                if (e == null) {
                                    observable.onSuccess(s);
                                } else {
                                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                    observable.onError(e);
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
                    observable.onError(new Throwable());
                }
            });
        } else {
            request.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    ////这里进行回调
                    if (e == null) {
                        observable.onSuccess(s);
                    } else {
                        Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                        observable.onError(e);
                    }
                }
            });
        }
    }
}
