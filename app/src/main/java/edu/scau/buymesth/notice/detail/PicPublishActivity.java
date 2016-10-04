package edu.scau.buymesth.notice.detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
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
import edu.scau.buymesth.notice.PicAdapter;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.OrderMoment;
import edu.scau.buymesth.util.CompressHelper;
import gallery.PhotoActivity;
import me.iwf.photopicker.PhotoPicker;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Created by Jammy on 2016/10/1.
 */
public class PicPublishActivity extends BaseActivity {
    @Bind(R.id.et)
    EditText et;
    @Bind(R.id.rv)
    RecyclerView mRecyclerView;
    PicAdapter adapter;
    List<String> list = new ArrayList<>();
    public ArrayList<String> mUrlList = new ArrayList<>(9);
    @Bind(R.id.btn_commit)
    Button btnCommit;
    private volatile ArrayList<String> mCompressList = new ArrayList<>(9);
    public volatile boolean mCompressing;
    ExecutorService threadPoolExecutor = newFixedThreadPool(1);
    List<String> picWidths = new LinkedList<>();
    List<String> picHeights = new LinkedList<>();
    private ProgressDialog mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pic_publish;
    }
    public void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setCancelable(false);
            mDialog.setMessage("请稍等");
            mDialog.setMax(100);
            mDialog.setProgressStyle(1);
        }
        mDialog.show();
    }
    public void setProgress(Integer progress){
        mDialog.setProgress(progress);
    }
    @Override
    public void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
        //    mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        adapter = new PicAdapter(list);

        mRecyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener((view, position) -> {
            ////这里设置点击事件
            if (adapter.getItemId(position) == 1) {
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(true)
                        .setSelected(mUrlList)
                        .start(PicPublishActivity.this, PhotoPicker.REQUEST_CODE);
            } else {
                PhotoActivity.navigate(PicPublishActivity.this, view, adapter.getItem(position), position);
            }
        });
        btnCommit.setOnClickListener(v -> {
            showLoadingDialog();
            if(mCompressList.get(mCompressList.size()-1)==null)
            mCompressList.remove(mCompressList.size()-1);
            String[] fileList = new String[mCompressList.size()];
            mCompressList.toArray(fileList);
            BmobFile.uploadBatch(fileList, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> urls) {
                    if (urls.size() == fileList.length) {//如果数量相等，则代表文件全部上传完成
                        OrderMoment orderMonent = new OrderMoment();
                        orderMonent.setText(et.getText().toString());
                        orderMonent.setPicList(urls);
                        orderMonent.setOrder((Order) getIntent().getSerializableExtra("order"));
                        orderMonent.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    closeLoadingDialog();
                                    Toast.makeText(PicPublishActivity.this,"上传成功",Toast.LENGTH_LONG).show();
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
                    setProgress(totalPercent);
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        });
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                mCompressList = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Log.d("zhx", "size=" + mCompressList.size());
                for (int i = 0; i < mCompressList.size(); ++i)
                    mUrlList.add(i, mCompressList.get(i));
                adapter.setList(mCompressList);
                toast("开始压缩图片");
                new Thread(this::compress).start();
            }
        }
    }

    private void compress() {
        mCompressing = true;
        CompressHelper compressHelper = new CompressHelper(mContext);
        //判断最后一个元素是否包含空
        int count = mCompressList.get(mCompressList.size() - 1) != null ? mCompressList.size() : mCompressList.size() - 1;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            final int finalI = i;
            threadPoolExecutor.execute(() -> {
                compressHelper.setFilename("cc_" + finalI);
                compressHelper.setWidthList(picWidths);
                compressHelper.setHeightList(picHeights);
                mCompressList.set(finalI, compressHelper.thirdCompress(new File(mCompressList.get(finalI))));
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
            runOnUiThread(() -> {
                toast("压缩完成");
                //因为会自动加上一个NULL，这里要把之前被加上的null去除。。。
                if (mCompressList.size() != 9)
                    mCompressList.remove(mCompressList.size() - 1);
                adapter.setList(mCompressList);
            });
            mCompressing = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

}
