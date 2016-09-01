package edu.scau.buymesth.publish;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import base.BaseActivity;
import base.util.ToastUtil;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.PictureAdapter;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import ui.widget.SelectableSeekBar;

/**
 * Created by Jammy on 2016/8/11.
 * Updated by John on 2016/8/18
 */
public class PublishActivity extends BaseActivity implements View.OnClickListener, PublishContract.View {
    List<TextView> tagList = new LinkedList<>();
    @Bind(R.id.btn_submit)
    Button btnSubmit;
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_detail)
    EditText etDetail;
    @Bind(R.id.rv)
    RecyclerView mRecyclerView;
    @Bind(R.id.range_bar)
    SelectableSeekBar mSelectableSeekBar;
    PublishPresenter presenter;
    PictureAdapter adapter;
    @Bind(R.id.rl_price_bar)
    RelativeLayout mPriceBar;
    @Bind(R.id.tv_price_number)
    TextView mPriceNumber;
    List<PhotoInfo> list = new ArrayList<>();
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.sv_parent)
    ScrollView parent;
    @Bind(R.id.tv_add)
    TextView tvAdd;
    @Bind(R.id.flowlayout)
    FlowLayout flowlayout;
    private ProgressDialog mDialog = null;
    private AlertDialog priceInputDialog;
    private AlertDialog priceRangeDialog;
    private String low;
    private String high;
    private String rangePrice = "￥0~￥0";
    private String thePrice = "￥0";

    ItemTouchHelper helper;


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

        helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP |
                        ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT |
                        ItemTouchHelper.RIGHT;
                int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (adapter.getItemId(fromPosition) == 1) return false;
                if (adapter.getItemId(toPosition) == 1) return false;
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(adapter.getData(), i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(adapter.getData(), i, i - 1);
                    }
                }
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(mRecyclerView);
        adapter = new PictureAdapter(list);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnRecyclerViewItemClickListener((view, position) -> {
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
        });
        initToolBar();
        mSelectableSeekBar.setParent(parent);
        mSelectableSeekBar.setOnStateSelectedListener(pos -> {
            if (mSelectableSeekBar.getSelectedPosition() == 0)
                mPriceNumber.setText(thePrice);
            else if (mSelectableSeekBar.getSelectedPosition() == 1)
                mPriceNumber.setText(rangePrice);
        });
        final EditText editText = new EditText(mContext);
        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        priceInputDialog = new AlertDialog.Builder(mContext).setView(editText).setPositiveButton("确定", (dialog, which) -> {
            String price = editText.getText().toString();
            if (!price.equals("")) {
                thePrice = "￥" + price;
            }
            mPriceNumber.setText(thePrice);
        }).create();
        View view = getLayoutInflater().inflate(R.layout.dialog_price_range, null);
        EditText etLow = (EditText) view.findViewById(R.id.et_low);
        EditText etHigh = (EditText) view.findViewById(R.id.et_high);
        priceRangeDialog = new AlertDialog.Builder(mContext).setView(view).setPositiveButton("确定", (dialog, which) -> {
            low = etLow.getText().toString();
            high = etHigh.getText().toString();
            if (!low.equals("") && !high.equals("") && Integer.valueOf(low) < Integer.valueOf(high))
                rangePrice = "￥" + low + "~￥" + high;
            mPriceNumber.setText(rangePrice);
        }).create();
        mPriceBar.setOnClickListener((v) -> {
            if (mSelectableSeekBar.getSelectedPosition() == 0)
                priceInputDialog.show();
            else if (mSelectableSeekBar.getSelectedPosition() == 1)
                priceRangeDialog.show();
        });
        tvAdd.setOnClickListener(this);
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
                List<String> tag = new ArrayList<>();
                for (TextView tv : tagList) {
                    String text = (String) tv.getText();
                    text = text.substring(1, text.length() - 1);
                    tag.add(text);
                }
                request.setTags(tag);
                presenter.submit(request, list);
                break;

            case R.id.tv_add:
                EditText et = new EditText(mContext);
                AlertDialog dialog = new AlertDialog.Builder(mContext).setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView tv = (TextView) LayoutInflater.from(PublishActivity.this).inflate(R.layout.tv_tag, null);
                        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        marginLayoutParams.setMargins(4, 4, 4, 4);
                        tv.setLayoutParams(marginLayoutParams);
                        tv.setText("#" + et.getText());
                        flowlayout.addView(tv);
                        tagList.add(tv);
                        tv.setOnClickListener(v1 -> {
                            AlertDialog dialog1 = new AlertDialog.Builder(mContext).setTitle("是否删除").setPositiveButton("确定", (dialog2, which1) -> {
                                flowlayout.removeView(tv);
                                tagList.remove(tv);
                            }).create();
                            dialog1.show();
                        });
                    }
                }).create();
                dialog.show();
                break;
        }
    }

    @Override
    public void onSubmitFinish() {
        this.finish();
    }

    @Override
    public void onSubmitFail() {
        Toast.makeText(mContext, "上传失败，请重试", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setCancelable(false);
            mDialog.setMessage("上传中");
        }
        mDialog.show();
    }

    @Override
    public void closeLoadingDialog() {
        if (mDialog == null) {
            mDialog.dismiss();
        }
    }

    protected void initToolBar() {
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
            if (parent.getChildPosition(view) != 0)
                outRect.top = space;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        presenter = null;
    }

}
