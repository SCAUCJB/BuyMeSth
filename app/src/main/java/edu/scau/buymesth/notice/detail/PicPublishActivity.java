package edu.scau.buymesth.notice.detail;


import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.MyPictureAdapter;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.OrderMoment;
import edu.scau.buymesth.util.CompressHelper;
import gallery.PhotoActivity;
import me.iwf.photopicker.PhotoPicker;
import rx.Observable;
import util.FileUtils;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class PicPublishActivity extends BaseActivity {


    ArrayList<MyPictureAdapter.ImageItem> mUrlList;
    MyPictureAdapter adapter;
    boolean mCompressing = false;
    boolean mCompressed = false;
    boolean mCompress = false;
    long mImageSize = 0;
    @Bind(R.id.btn_commit)
    Button btnCommit;
    @Bind(R.id.et)
    EditText et;
    @Bind(R.id.rv)
    RecyclerView recyclerView;
    @Bind(R.id.sw_compress)
    Switch swCompress;
    @Bind(R.id.tv_size)
    TextView tvSize;
    private ExecutorService threadPoolExecutor;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pic_publish;
    }

    @Override
    public void initView() {
        mUrlList = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new MyPictureAdapter(mUrlList);
        recyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener((view, position) -> {
            ////这里设置点击事件
            ArrayList<String> selectImages = new ArrayList<>();
            if (adapter.getItemId(position) == 1) {
                for (MyPictureAdapter.ImageItem ii : mUrlList) selectImages.add(ii.sourceImage);
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(true)
                        .setSelected(selectImages)
                        .start(PicPublishActivity.this, PhotoPicker.REQUEST_CODE);
            } else {
                for (MyPictureAdapter.ImageItem ii : mUrlList)
                    selectImages.add(mCompress ? ii.compressedImage : ii.sourceImage);
                PhotoActivity.navigate(PicPublishActivity.this, recyclerView, selectImages, position);
            }
        });

        swCompress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mCompress = isChecked;
            if (isChecked && !mCompressed && !mCompressing) {
                compress();
            } else if (mCompressed && !mCompressing) {
                mImageSize = 0;
                adapter.setList(mUrlList, mCompress ? 1 : 0);
                Observable.from(mUrlList)
                        .map(imageItem -> new File(mCompress ? imageItem.compressedImage : imageItem.sourceImage))
                        .subscribe(file -> mImageSize += file.length(),
                                o -> {
                                },
                                () -> tvSize.setText("图片大小："+ FileUtils.convert(mImageSize)));
            }
        });


        btnCommit.setOnClickListener(v -> {
            //upload images
            if (mUrlList.size() > 0) {
                if (mCompressing) {
                    toast("压缩中");
                    return;
                }
                btnCommit.setEnabled(false);
                ArrayList<String> selectImages = new ArrayList<>();
                for (MyPictureAdapter.ImageItem ii : mUrlList)
                    selectImages.add(mCompress ? ii.compressedImage : ii.sourceImage);
                String[] sendList = new String[selectImages.size()];
                selectImages.toArray(sendList);

                BmobFile.uploadBatch(sendList, new UploadBatchListener() {
                    @Override
                    public void onSuccess(List<BmobFile> files, List<String> urls) {
                        if (urls.size() >= mUrlList.size()) {
                            OrderMoment orderMonent = new OrderMoment();
                            orderMonent.setText(et.getText().toString());
                            orderMonent.setPicList(urls);
                            orderMonent.setOrder((Order) getIntent().getSerializableExtra("order"));
                            orderMonent.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        closeLoadingDialog();
                                        Toast.makeText(PicPublishActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Log.e("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                        closeLoadingDialog();
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
                        View view = recyclerView.getChildAt(curIndex - 1);
                        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_upload);
                        if (progressBar != null) {
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
            } else {
                OrderMoment orderMonent = new OrderMoment();
                orderMonent.setText(et.getText().toString());
                orderMonent.setOrder((Order) getIntent().getSerializableExtra("order"));
                orderMonent.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            closeLoadingDialog();
                            Toast.makeText(PicPublishActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Log.e("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                            closeLoadingDialog();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                mUrlList.clear();
                for (String url : photos) {
                    mUrlList.add(new MyPictureAdapter.ImageItem(url, null));
                }
                adapter.setList(mUrlList, mCompress ? 1 : 0);
                mImageSize = 0;
                mCompressed = false;
                Observable.from(mUrlList)
                        .map(imageItem -> new File(mCompress ? imageItem.compressedImage : imageItem.sourceImage))
                        .subscribe(file -> mImageSize += file.length(),
                                o -> {
                                },
                                () -> tvSize.setText("图片大小："+ FileUtils.convert(mImageSize)));
                if (mCompress) compress();
            }
        }
    }

    private void compress() {
        if(threadPoolExecutor==null)
        {
            threadPoolExecutor = newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
        swCompress.setEnabled(false);
        tvSize.setText("压缩中");
        new Thread(() -> {
            mCompressing = true;
            CompressHelper compressHelper = new CompressHelper(mContext);
            CountDownLatch countDownLatch = new CountDownLatch(mUrlList.size());
            for (int i = 0; i < mUrlList.size(); i++) {
                final int finalI = i;
                threadPoolExecutor.execute(() -> {
                    compressHelper.setFilename("cc_" + finalI);
//                    mUrlList.set(finalI,compressHelper.thirdCompress(new File(mUrlList.get(finalI))));
                    mUrlList.get(finalI).compressedImage = compressHelper.thirdCompress(new File(mUrlList.get(finalI).sourceImage));
                    countDownLatch.countDown();
                });
            }
            try {
                countDownLatch.await();
                runOnUiThread(() -> {
                    toast("压缩完成");
                    mImageSize = 0;
                    Observable.from(mUrlList)
                            .map(imageItem -> new File(mCompress ? imageItem.compressedImage : imageItem.sourceImage))
                            .subscribe(file -> mImageSize += file.length(),
                                    o -> {
                                    },
                                    () -> tvSize.setText("图片大小："+ FileUtils.convert(mImageSize)));
                    adapter.setList(mUrlList, mCompress ? 1 : 0);
                    swCompress.setEnabled(true);
                });
                mCompressing = false;
                mCompressed = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadPoolExecutor.shutdownNow();
    }
}
