package edu.scau.buymesth.publish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
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
import android.widget.Switch;
import android.widget.TextView;

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
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.MyPictureAdapter;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.util.CompressHelper;
import edu.scau.buymesth.util.InputMethodHelper;
import gallery.PhotoActivity;
import me.iwf.photopicker.PhotoPicker;
import rx.Observable;
import ui.widget.SelectableSeekBar;
import util.FileUtils;

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
    MyPictureAdapter adapter;
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
    @Bind(R.id.tv_size)
    TextView tvSize;
    @Bind(R.id.sw_compress)
    Switch swCompress;
    ArrayList<MyPictureAdapter.ImageItem> mUrlList = new ArrayList<>();

    public volatile boolean mCompressing = false;
    boolean mCompressed = false;
    boolean mCompress = false;
    long mImageSize = 0;
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
        adapter = new MyPictureAdapter(mUrlList);
        mRecyclerView.setAdapter(adapter);
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
                        .start(PublishActivity.this, PhotoPicker.REQUEST_CODE);
            } else {
                for (MyPictureAdapter.ImageItem ii : mUrlList)
                    selectImages.add(mCompress ? ii.compressedImage : ii.sourceImage);
                PhotoActivity.navigate(PublishActivity.this, mRecyclerView, selectImages, position);
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
                                () -> tvSize.setText("图片大小：" + FileUtils.convert(mImageSize)));
            }
        });
        initToolBar();
        initPriceSelect();
        showIntro(tvAdd,"tvAdd","点击这里添加你的特殊要求吧");
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
                                () -> tvSize.setText("图片大小：" + FileUtils.convert(mImageSize)));
                if (mCompress) compress();
            }
        }
    }

    private void compress() {
        if(threadPoolExecutor==null)
        {
            Runtime.getRuntime().gc();
            short mem= (short) (Runtime.getRuntime().freeMemory()>>20);
            threadPoolExecutor = newFixedThreadPool(mem>6?mem/5:1);

        }
        swCompress.setEnabled(false);
        tvSize.setText("压缩中");
        showLoadingDialog();
        mDialog.setMax(mUrlList.size());
        new Thread(() -> {
            mCompressing = true;
            CompressHelper compressHelper = new CompressHelper(mContext);
            compressHelper.setHeightList(picHeights);
            compressHelper.setWidthList(picWidths);
            CountDownLatch countDownLatch = new CountDownLatch(mUrlList.size());
            for (int i = 0; i < mUrlList.size(); i++) {
                final int finalI = i;
                threadPoolExecutor.execute(() -> {
                    compressHelper.setFilename("cc_" + finalI);
                    mUrlList.get(finalI).compressedImage = compressHelper.thirdCompress(new File(mUrlList.get(finalI).sourceImage));
                    countDownLatch.countDown();
                    runOnUiThread(() -> mDialog.setProgress((int)(mUrlList.size()-countDownLatch.getCount())));
                });
            }

            try {
                countDownLatch.await();
                runOnUiThread(() -> {
                    closeLoadingDialog();
                    toast("压缩完成");
                    mImageSize = 0;
                    Observable.from(mUrlList)
                            .map(imageItem -> new File(mCompress ? imageItem.compressedImage : imageItem.sourceImage))
                            .subscribe(file -> mImageSize += file.length(),
                                    o -> {
                                    },
                                    () -> tvSize.setText("图片大小：" + FileUtils.convert(mImageSize)));
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


    List<String> picWidths = new LinkedList<>();
    List<String> picHeights = new LinkedList<>();
    ExecutorService threadPoolExecutor =null;


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
            hideBroad();
        }).create();
        View priceRangeView = getLayoutInflater().inflate(R.layout.dialog_price_range, null);
        EditText priceLowEt = (EditText) priceRangeView.findViewById(R.id.et_low);
        EditText priceHighEt = (EditText) priceRangeView.findViewById(R.id.et_high);
        priceRangeDialog = new AlertDialog.Builder(mContext).setTitle("请输入价格范围").setView(priceRangeView).setNegativeButton("取消", null).setPositiveButton("确定", (dialog, which) -> {
            low = priceLowEt.getText().toString();
            high = priceHighEt.getText().toString();
            if (!low.equals("") && !high.equals("") && Integer.valueOf(low) < Integer.valueOf(high))
                rangePrice = "￥" + low + "~￥" + high;
            mPriceNumber.setText(rangePrice);
            hideBroad();
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
                ArrayList<String> selectImages = new ArrayList<>();
                for (MyPictureAdapter.ImageItem ii : mUrlList) {
                    selectImages.add(mCompress ? ii.compressedImage : ii.sourceImage);
                    //如果是没压缩的图片，需要计算图片的宽和高
                    if(!mCompress){
                        final BitmapFactory.Options options=new BitmapFactory.Options();
                        options.inJustDecodeBounds=true;
                        BitmapFactory.decodeFile(ii.sourceImage,options);
                        picHeights.add(String.valueOf(options.outHeight));
                        picWidths.add(String.valueOf(options.outWidth));
                    }
                }
                String[] sendList = new String[selectImages.size()];
                selectImages.toArray(sendList);
                try{
                presenter.submit(picHeights, picWidths, sendList);}
                catch (Exception e){
                    closeLoadingDialog();
                }
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
        mTagInputDialog = new AlertDialog.Builder(mContext).setTitle("请输入标签").setView(view).setNegativeButton("取消", null).setPositiveButton("确定", (dialog, which) -> {
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
            hideBroad();
            editText.setText("");
        }).create();
    }

    @Override
    public void onSubmitFinish() {
        closeLoadingDialog();
        this.finish();
    }

    @Override
    public void onSubmitFail() {
        toast("上传失败，请重试");
        closeLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setCancelable(false);
            mDialog.setMessage("请稍等");
            mDialog.setProgressStyle(1);
            mDialog.setProgress(0);
        }
        mDialog.setMax(100);
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
        if(threadPoolExecutor!=null)
        threadPoolExecutor.shutdownNow();
    }

    public void hideBroad() {
        InputMethodHelper.toggle(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodHelper.closeFromView(mContext, etTitle);
            }
        }, 100);
    }
    private void showIntro(View view, String usageId, String text){
        new MaterialIntroView.Builder(this)
                .enableDotAnimation(false)
                //.enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.ALL)
                .setDelayMillis(200)
                .enableFadeAnimation(true)
                .setListener(materialIntroListener)
                .performClick(false)
                .setInfoText(text)
                .setTarget(view)
                .setUsageId(usageId) //THIS SHOULD BE UNIQUE ID
                .show();
    }
    MaterialIntroListener materialIntroListener= materialIntroViewId -> {
        if(materialIntroViewId=="tvAdd"){
            showIntro(mPriceBar,"mPriceBar","填写你理想中的商品价格");
        }else if(materialIntroViewId=="mPriceBar"){
            showIntro(swCompress,"swCompress","点击可以无损压缩图片节约流量哦");//这里用了夸张的广告手法
        }
    };
}
