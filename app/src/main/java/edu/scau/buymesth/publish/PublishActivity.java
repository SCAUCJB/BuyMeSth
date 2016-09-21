package edu.scau.buymesth.publish;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.PictureAdapter;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.util.CompressHelper;
import gallery.PhotoActivity;
import me.iwf.photopicker.PhotoPicker;
import ui.widget.SelectableSeekBar;

import static java.util.concurrent.Executors.newFixedThreadPool;

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
    List<String> list = new ArrayList<>();
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
    private String price;
    private String rangePrice = "￥0~￥0";
    private String thePrice = "￥0";

    ItemTouchHelper helper;
    private AlertDialog mTagInputDialog = null;
    private   ArrayList<String> mUrlList = new ArrayList<>(9);
    private volatile  ArrayList<String> mCompressList = new ArrayList<>(9);
    private volatile boolean mCompressing;

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
    //    mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
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
                if (mCompressing) {
                    toast("正在压缩图片");
                    return false;
                }
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
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(true)
                        .setSelected(mUrlList)
                        .start(PublishActivity.this, PhotoPicker.REQUEST_CODE);
            } else {
                PhotoActivity.navigate(PublishActivity.this, view, adapter.getItem(position), position);
            }
        });
        initToolBar();
        initPriceSelect();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                mCompressList=data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Log.d("zhx","size="+mCompressList.size());
                for(int i=0;i<mCompressList.size();++i)
                    mUrlList.add(i,mCompressList.get(i));
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
        int count=mCompressList.get(mCompressList.size()-1)!=null?mCompressList.size():mCompressList.size()-1;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (int i = 0; i <count ; i++) {
            final  int finalI = i;
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
                if(mCompressList.size()!=9)
                    mCompressList.remove(mCompressList.size() - 1);
                adapter.setList(mCompressList);
            });
            mCompressing = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    List<String> picWidths = new LinkedList<>();
    List<String> picHeights = new LinkedList<>();
    ExecutorService threadPoolExecutor = newFixedThreadPool(4);


    private void initPriceSelect() {
        mSelectableSeekBar.setParent(parent);
        mSelectableSeekBar.setOnStateSelectedListener(pos -> {
            if (mSelectableSeekBar.getSelectedPosition() == 0)
                mPriceNumber.setText(thePrice);
            else if (mSelectableSeekBar.getSelectedPosition() == 1)
                mPriceNumber.setText(rangePrice);
        });
        View priceInputView = getLayoutInflater().inflate(R.layout.dialog_input, null);
        EditText priceInputEt = (EditText) priceInputView.findViewById(R.id.et_input);
        //     editText.requestFocus();
        priceInputEt.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        priceInputDialog = new AlertDialog.Builder(mContext).setTitle("请输入价格").setView(priceInputView).setNegativeButton("取消", null).setPositiveButton("确定", (dialog, which) -> {
            price = priceInputEt.getText().toString();
            if (!price.equals("")) {
                thePrice = "￥" + price;
            }
            mPriceNumber.setText(thePrice);
        }).create();
        View priceRangeView=getLayoutInflater().inflate(R.layout.dialog_price_range,null);
        EditText priceLowEt = (EditText)priceRangeView.findViewById(R.id.et_low);
        EditText priceHighEt = (EditText) priceRangeView.findViewById(R.id.et_high);
        priceRangeDialog = new AlertDialog.Builder(mContext).setTitle("请输入价格范围").setView(priceRangeView).setNegativeButton("取消", null).setPositiveButton("确定", (dialog, which) -> {
            low = priceLowEt.getText().toString();
            high = priceHighEt.getText().toString();
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
                if (mCompressing) {
                    toast("还在压缩图片，请稍等");
                    return;
                }
                Request request = new Request();
                request.setTitle(etTitle.getText().toString());
                request.setContent(etDetail.getText().toString());
                request.setUser(BmobUser.getCurrentUser(User.class));
                if (mSelectableSeekBar.getSelectedPosition() == 1) {
                    try {
                        request.setMaxPrice(Integer.valueOf(high));
                        request.setMinPrice(Integer.valueOf(low));
                    } catch (NumberFormatException e) {
                        toast("请填整数价格");
                        return;
                    }
                } else {
                    try {
                        request.setMaxPrice(Integer.valueOf(price));
                        request.setMinPrice(null);
                    } catch (NumberFormatException e) {
                        toast("请填整数价格");
                        return;
                    }
                }

                List<String> tag = new ArrayList<>();
                for (TextView tv : tagList) {
                    tag.add(tv.getText().toString());
                }
                request.setTags(tag);
                presenter.setRequest(request);
                showLoadingDialog();
                presenter.submit(picHeights, picWidths, mUrlList);
                break;

            case R.id.tv_add:
                if (mTagInputDialog == null)
                    initTagInputDialog();
                mTagInputDialog.show();
                break;
        }
    }

    private void initTagInputDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_input, null);
        EditText editText = (EditText) view.findViewById(R.id.et_input);
        mTagInputDialog = new AlertDialog.Builder(mContext).setTitle("请输入标签").setView(view).setNegativeButton("取消",null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText() == null) return;
                TextView tv = (TextView) LayoutInflater.from(PublishActivity.this).inflate(R.layout.tv_tag, null);
                ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                marginLayoutParams.setMargins(4, 4, 4, 4);
                tv.setLayoutParams(marginLayoutParams);
                tv.setText(editText.getText());
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
    }

    @Override
    public void onSubmitFinish() {
        closeLoadingDialog();
        this.finish();
    }

    @Override
    public void onSubmitFail() {
        Toast.makeText(mContext, "上传失败，请重试", Toast.LENGTH_SHORT).show();
        closeLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setCancelable(false);
            mDialog.setMessage("请稍等");
            mDialog.setMax(100);
            mDialog.setProgress(0);
        }
        mDialog.show();
    }

    @Override
    public void closeLoadingDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void setProgress(Integer progress) {
        if (mDialog != null) {
            mDialog.setProgress(progress);
            Log.d("zhx", "progress=" + progress);
        }
    }

    protected void initToolBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        presenter = null;
        threadPoolExecutor.shutdownNow();
    }

}
