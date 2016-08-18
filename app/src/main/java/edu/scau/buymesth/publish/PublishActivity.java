package edu.scau.buymesth.publish;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import base.util.ToastUtil;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.PictureAdapter;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by Jammy on 2016/8/11.
 * Updated by John on 2016/8/18
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
    RecyclerView mRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add;
    }

    @Override
    public void initView() {
        btn_gallery.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                GalleryFinal.openGalleryMuti(1, 8, new GalleryFinal.OnHanlderResultCallback() {
                    @Override
                    public void onHanlderSuccess(int requestCode, List<PhotoInfo> resultList) {
                        //这个传过来的resultList的生命周期跟当前activity的生命周期不一致，所以要复制一份，否则recycler view没更新完，resultList就被垃圾回收了
                        List<PhotoInfo> list = new ArrayList<>();
                        for (int i = 0; i < resultList.size(); i++) {
                            list.add(resultList.get(i));
                        }
                        mRecyclerView.setAdapter(new PictureAdapter(list));
                    }

                    @Override
                    public void onHanlderFailure(int requestCode, String errorMsg) {
                        ToastUtil.show("出错了");
                    }
                });
                break;
        }
    }
    private final class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//TODO 这里建议换成 getChildAdapterPosition(View) 或者 getChildLayoutPosition(View)
            if (parent.getChildPosition(view) != 0)
                outRect.top = space;
        }
    }
}
