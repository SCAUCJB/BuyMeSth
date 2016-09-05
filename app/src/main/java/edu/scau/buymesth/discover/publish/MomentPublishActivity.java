package edu.scau.buymesth.discover.publish;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import base.util.ToastUtil;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.PictureAdapter;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.data.bean.User;
import gallery.PhotoDialogFragment;
import photoview.PhotoView;
import ui.layout.NineGridLayout;

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
    List<PhotoInfo> photoInfoList;
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
        adapter = new PictureAdapter(photoInfoList);
        recyclerView.setAdapter(adapter);

        adapter.setOnRecyclerViewItemClickListener((view, position) -> {
            ////这里设置点击事件
            if (adapter.getItemId(position) == 1) {
                FunctionConfig functionConfig = new FunctionConfig.Builder()
                        .setEnableCamera(true)
                        .setSelected(photoInfoList)
                        .setMutiSelectMaxSize(9)
                        .build();
                GalleryFinal.openGalleryMuti(1, functionConfig, new GalleryFinal.OnHanlderResultCallback() {
                    @Override
                    public void onHanlderSuccess(int requestCode, List<PhotoInfo> resultList) {
                        //这个传过来的resultList的生命周期跟当前activity的生命周期不一致，所以要复制一份，否则recycler view没更新完，resultList就被垃圾回收了
                        photoInfoList.clear();
                        localUrlList.clear();
                        for (int i = 0; i < resultList.size(); i++) {
                            photoInfoList.add(resultList.get(i));
                            localUrlList.add(resultList.get(i).getPhotoPath());
                        }
                        adapter.setList(photoInfoList);
                    }

                    @Override
                    public void onHanlderFailure(int requestCode, String errorMsg) {
                        ToastUtil.show("出错了");
                    }
                });
            } else {
                PhotoDialogFragment.navigate(MomentPublishActivity.this,view.findViewById(R.id.iv),localUrlList.get(position),position);
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
                if(localUrlList.size()>0) {
                    String[] sendList = new String[localUrlList.size()];
                    for(int i=0;i<localUrlList.size();i++){
                        sendList[i] = localUrlList.get(i);
                    }
                    toast("upload");
                    BmobFile.uploadBatch(sendList, new UploadBatchListener() {
                        @Override
                        public void onSuccess(List<BmobFile> files, List<String> urls) {
                            urlList = urls;
                            if(urls.size()>=localUrlList.size()){
                                Moment moment = new Moment();
                                moment.setAuthor(BmobUser.getCurrentUser(User.class));
                                moment.setContent(momentContent.getText().toString());
                                moment.setImages(urlList);
                                moment.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(e==null){
                                            //succeed
                                            toast("send");
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
                    moment.setAuthor(BmobUser.getCurrentUser(User.class));
                    moment.setContent(momentContent.getText().toString());
                    moment.setImages(urlList);
                    moment.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                //succeed
                                toast("send");
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
