package edu.scau.buymesth.publish;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import base.BaseActivity;
import base.util.ToastUtil;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by Jammy on 2016/8/11.
 */
public class PublishActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.btn_submit)
    Button btnSubmit;
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_detail)
    EditText etDetail;
    @Bind(R.id.gallery)
    Button btn_gallery;
    @Bind(R.id.rv)
    RecyclerView rv;
    private PictureAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add;
    }

    @Override
    public void initView() {
        btn_gallery.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        mAdapter=new PictureAdapter();
        rv.setAdapter(mAdapter);
        rv.setLayoutManager(new GridLayoutManager(this,3));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                this.finish();
                break;

            case R.id.btn_submit:
                Request request = new Request();
                request.setTitle(etTitle.getText().toString());
                request.setContent(etDetail.getText().toString());
                request.setAuthor(BmobUser.getCurrentUser(User.class));
                request.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            toast("创建数据成功：" + s);
                            finish();
                        } else {
                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
                break;

            case R.id.gallery:
                GalleryFinal.openGalleryMuti(1,8, new GalleryFinal.OnHanlderResultCallback() {
                    @Override
                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                        ////这里的List就是选择的List
                    mAdapter.setNewData(resultList);
                    }

                    @Override
                    public void onHanlderFailure(int requestCode, String errorMsg) {
                        ToastUtil.show("出错了");
                    }
                });
                break;
        }
    }

}
