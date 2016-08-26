package edu.scau.buymesth.publish;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import adpater.BaseQuickAdapter;
import base.BaseActivity;
import base.util.ToastUtil;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.PictureAdapter;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by Jammy on 2016/8/11.
 * Updated by John on 2016/8/18
 */
public class PublishActivity extends BaseActivity implements View.OnClickListener, PublishContract.View {
    @Bind(R.id.btn_submit)
    Button btnSubmit;
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_detail)
    EditText etDetail;
    @Bind(R.id.rv)
    RecyclerView mRecyclerView;

    PublishPresenter presenter;
    PictureAdapter adapter;


    List<PhotoInfo> list = new ArrayList<>();
    @Bind(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_add;
    }

    @Override
    public void initView() {
        presenter = new PublishPresenter();
        presenter.setVM(this, new PublishModel());
        btnSubmit.setOnClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
        adapter = new PictureAdapter(list);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ////这里设置点击事件
                if (adapter.getItemId(position) == 1) {
                    FunctionConfig functionConfig = new FunctionConfig.Builder()
                            .setEnableCamera(true)
                            .setSelected(list)
                            .setMutiSelectMaxSize(9)
                            .build();
                    GalleryFinal.openGalleryMuti(1, functionConfig, new GalleryFinal.OnHanlderResultCallback() {
                        @Override
                        public void onHanlderSuccess(int requestCode, List<PhotoInfo> resultList) {
                            //这个传过来的resultList的生命周期跟当前activity的生命周期不一致，所以要复制一份，否则recycler view没更新完，resultList就被垃圾回收了
                            list = new ArrayList<>();
                            for (int i = 0; i < resultList.size(); i++) {
                                list.add(resultList.get(i));
                            }
                            adapter.setList(list);
                        }

                        @Override
                        public void onHanlderFailure(int requestCode, String errorMsg) {
                            ToastUtil.show("出错了");
                        }
                    });
                } else {
                    //TODO: 使用ImageLoader来放大查看图片
                }
            }
        });
        initToolBar();
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                Request request = new Request();
                request.setTitle(etTitle.getText().toString());
                request.setContent(etDetail.getText().toString());
                request.setAuthor(BmobUser.getCurrentUser(User.class));
                presenter.submit(request, list);
                break;
        }
    }

    @Override
    public void onSubmitFinish() {
        ToastUtil.show("提交成功");
        this.finish();
    }

    @Override
    public void onSubmitFail() {
        ToastUtil.show("提交失败");
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
