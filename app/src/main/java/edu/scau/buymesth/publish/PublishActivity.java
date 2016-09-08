package edu.scau.buymesth.publish;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.PictureAdapter;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.util.CompressHelper;
import me.iwf.photopicker.PhotoPicker;
import rx.Observable;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
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
        mRecyclerView.setHasFixedSize(true);
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
                        .start(PublishActivity.this, PhotoPicker.REQUEST_CODE);
            } else {
                //TODO: 使用ImageLoader来放大查看图片
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
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                adapter.setList(photos);
            }
        }
    }

    List<String> dstList;
    volatile Semaphore semaphore = new Semaphore(1);

    /**
     * 压缩单张图片 RxJava 方式
     */
    private void compress(List<String> photos) {
        if (photos.size() <= 1) return;
        List<String> list = Collections.synchronizedList(new LinkedList<>());
        CountDownLatch countDownLatch = new CountDownLatch(photos.size() - 1);
        for (int i = 0; i < photos.size() - 1; ++i) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Luban.get(this)
                    .load(new File(photos.get(i)))
                    .putGear(Luban.THIRD_GEAR)
                    .asObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnError(Throwable::printStackTrace)
                    .onErrorResumeNext(throwable -> {
                        return Observable.empty();
                    })
                    .subscribe(file -> {
                        list.add(file.getAbsolutePath());
                        countDownLatch.countDown();
                        semaphore.release();
                    });
        }
        try {
            countDownLatch.await();
            dstList = list;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    CompressHelper compressHelper = null;
    List<String> picWidths =new LinkedList<>();
    List<String> picHeights =new LinkedList<>();
    public void compressAndSubmit(List<String> photos) {
        if (photos.size() > 1) {
            new Thread(() -> {
                compressHelper = new CompressHelper(mContext);
                compressHelper.setWidthList(picWidths);
                compressHelper.setHeightList(picHeights);
                List<String> list = new LinkedList<>();
                CountDownLatch countDownLatch = new CountDownLatch(photos.size() - 1);
                for (int i = 0; i < photos.size() - 1; ++i) {
                    list.add(compressHelper.thirdCompress(new File(photos.get(i))));
                    countDownLatch.countDown();
                }
                try {
                    countDownLatch.await();
                    dstList = list;
                    PublishActivity.this.runOnUiThread(() -> presenter.submit(picHeights, picWidths,dstList));
                } catch (InterruptedException e) {
                    dstList = null;
                    PublishActivity.this.runOnUiThread(this::closeLoadingDialog);
                }
            }).start();
        }
    }

    private void initPriceSelect() {
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
            price = editText.getText().toString();
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
                list = adapter.getData();
                showLoadingDialog();
                //压缩图片
                compressAndSubmit(list);
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
                        tv.setText(et.getText());
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
        compressHelper.cleanCache(mContext);
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
            mDialog.setMessage("上传中");
        }
        mDialog.show();
    }

    @Override
    public void closeLoadingDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
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
    }

}
