package edu.scau.buymesth.discover.publish;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.PictureAdapter;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.util.CompressHelper;
import gallery.PhotoActivity;
import me.iwf.photopicker.PhotoPicker;

/**
 * Created by ！ on 2016/8/29.
 */
public class MomentPublishActivity extends BaseActivity{

    @Bind(R.id.rv_images)
    RecyclerView recyclerView;
    @Bind(R.id.bt_send_moment)
    Button momentSendButton;
    @Bind(R.id.et_moment_content)
    EditText momentContent;
    @Bind(R.id.tv_request)
    TextView tvRequest;
    List<String> localUrlList;
    List<String> urlList;
    List<String> photoInfoList;
    PictureAdapter adapter;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_moment_publish;
    }

    @Override
    public void initView() {
        localUrlList = new ArrayList<>();
        urlList = new ArrayList<>();
        photoInfoList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new PictureAdapter(photoInfoList);
        recyclerView.setAdapter(adapter);

        adapter.setOnRecyclerViewItemClickListener((view, position) -> {
            ////这里设置点击事件
            if (adapter.getItemId(position) == 1) {
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(true)
                        .start(MomentPublishActivity.this, PhotoPicker.REQUEST_CODE);
            } else {
                PhotoActivity.navigate(MomentPublishActivity.this,recyclerView,localUrlList,position);
            }
        });

        tvRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MomentPublishActivity.this,SelectActivity.class);
                startActivity(intent);
            }
        });

        momentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upload images
                if(localUrlList.size()>1) {
                    localUrlList.remove(localUrlList.size()-1);
                    String[] sendList = new String[localUrlList.size()];
                    localUrlList.toArray(sendList);

                    BmobFile.uploadBatch(sendList, new UploadBatchListener() {
                        @Override
                        public void onSuccess(List<BmobFile> files, List<String> urls) {
                            urlList = urls;
                            if(urls.size()>=localUrlList.size()){
                                Moment moment = new Moment();
                                moment.setUser(BmobUser.getCurrentUser(User.class));
                                moment.setContent(momentContent.getText().toString());
                                moment.setImages(urlList);
                                moment.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(e==null){
                                            //succeed
                                            finish();
                                        }else {
                                            toast(e.toString());
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
                            View view = recyclerView.getChildAt(curIndex-1);
                            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_upload);
                            if(progressBar!=null){
                                progressBar.setVisibility(View.VISIBLE);
                                progressBar.setMax(100);
                                progressBar.setProgress(curPercent);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            toast(s);
                        }
                    });
                }
                else {
                    Moment moment = new Moment();
                    moment.setUser(BmobUser.getCurrentUser(User.class));
                    moment.setContent(momentContent.getText().toString());
                    moment.setImages(urlList);
                    moment.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                //succeed
                                finish();
                            }else {
                                toast(e.toString());
                            }
                        }
                    });
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                new Thread(() -> {
                    compress(photos);
                }).start();
    //            adapter.setList(photos);
            }
        }
    }
    /**
     * 压缩单张图片 RxJava 方式
     */
    private void compress(ArrayList<String> photos) {

            if (photos.size() > 1) {
                new Thread(() -> {
                    CompressHelper compressHelper = new CompressHelper(mContext);
                    List<String> list = new LinkedList<>();
                    CountDownLatch countDownLatch = new CountDownLatch(photos.size() - 1);
                    for (int i = 0; i < photos.size() - 1; ++i) {
                        list.add(compressHelper.thirdCompress(new File(photos.get(i))));
                        countDownLatch.countDown();
                    }
                    try {
                        countDownLatch.await();
                        MomentPublishActivity.this.runOnUiThread(() -> {
                            adapter.setList(list);
                            localUrlList=list;} );
                    } catch (InterruptedException e) {

                    }
                }).start();
            }
    }
    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    public boolean showColorStatusBar() {
        return true;
    }

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }
}
