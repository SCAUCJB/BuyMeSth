package edu.scau.buymesth.createorder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.publish.FlowLayout;
import edu.scau.buymesth.util.InputMethodHelper;
import ui.widget.PickerView;

/**
 * Created by John on 2016/8/29.
 */

public class CreateOrderActivity extends BaseActivity implements CreateOrderContract.View {
    @Bind(R.id.tv_deliver_time)
    public TextView tvDeliverTime;
    @Bind(R.id.tv_add)
    TextView tvAdd;
    @Bind(R.id.fl_tags)
    FlowLayout flTags;
    @Bind(R.id.iv_avatar_author)
    ImageView ivAvatar;
    @Bind(R.id.tv_name)
    TextView tvNickName;
    @Bind(R.id.tv_tweet_title)
    TextView tvTweetTitle;
    @Bind(R.id.tv_tweet_text)
    TextView tvTweetText;
    @Bind(R.id.pv_tip)
    PickerView pvTip;
    @Bind(R.id.pv_price)
    PickerView pvPrice;
    @Bind(R.id.et_tip)
    EditText etTip;
    @Bind(R.id.et_price)
    EditText etPrice;
    @Bind(R.id.btn_submit)
    Button btnSubmit;
    @Bind(R.id.tv_price)
    TextView mPriceTv;
    DatePickerDialogFragment dialog = null;
    CreateOrderPresenter createOrderPresenter;
    private AlertDialog tagInputDialog;
    private ProgressDialog mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_order;
    }

    protected void initPresenter() {
        mPresenter = new CreateOrderPresenter();
        createOrderPresenter = (CreateOrderPresenter) mPresenter;
        CreateOrderModel model = new CreateOrderModel();
        model.setRequest((Request) getIntent().getSerializableExtra(Constant.EXTRA_REQUEST));
        createOrderPresenter.setVM(this, model);
    }

    private Drawable clickerDrawable;
    private Drawable unclickerDrawable;

    @Override
    public void initView() {
        findViewById(R.id.card_view).setOnClickListener(v -> toast("i was clicked"));
        clickerDrawable = getResources().getDrawable(R.drawable.rect_accent);
        unclickerDrawable = getResources().getDrawable(R.drawable.rect_grey);
        View view = getLayoutInflater().inflate(R.layout.dialog_input, null);
        EditText editText = (EditText) view.findViewById(R.id.et_input);

        tagInputDialog = new AlertDialog.Builder(mContext).setTitle("请输入标签").setView(view).setNegativeButton("取消", null).setPositiveButton("确定", (dialog, which) -> {

                    String tag = editText.getText().toString();
                    TextView tvTag = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tv_tag, null);
                    ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    marginLayoutParams.setMargins(20, 4, 20, 4);
                    tvTag.setLayoutParams(marginLayoutParams);
                    tvTag.setText(tag);
                    createOrderPresenter.addTag(tag);
                    tvTag.setClickable(true);
                    tvTag.setBackground(clickerDrawable);
                    tvTag.setOnClickListener(
                            new View.OnClickListener() {
                                boolean flag = false;

                                @Override
                                public void onClick(View v) {
                                    if (flag) {
                                        tvTag.setBackground(clickerDrawable);
                                        createOrderPresenter.addTag(tag);
                                        flag = false;
                                    } else {
                                        tvTag.setBackground(unclickerDrawable);
                                        createOrderPresenter.removeTag(tag);
                                        flag = true;
                                    }
                                }
                            }
                    );
                    flTags.addView(tvTag);
                }
        ).
      create();




    }
    private void showIntro(View view, String usageId, String text){
        new MaterialIntroView.Builder(this)
                .enableDotAnimation(true).dismissOnTouch(true)
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
            showIntro(tvDeliverTime,"tvDeliverTime","选择准确的发货时间");
        }else if(materialIntroViewId=="tvDeliverTime"){
            showIntro(pvTip,"pvTip","滑动可以选择货币类型");
        }else if(materialIntroViewId=="tvTag"){
            showIntro(tvAdd, "tvAdd", "在这里添加服务的优势");
        }
    };

    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    protected void setListener() {
        findViewById(R.id.ll_background).setOnClickListener(v ->
        {
            if (InputMethodHelper.isOpen(mContext))
                InputMethodHelper.close(mContext, CreateOrderActivity.this);
        });
        tvDeliverTime.setOnClickListener(v -> createOrderPresenter.onDeliverTimeClicked());
        tvAdd.setOnClickListener(v -> tagInputDialog.show());
        btnSubmit.setOnClickListener(v -> createOrderPresenter.onSubmitClicked());
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }


    public static void navigateTo(Context context, Request request) {
        Intent intent = new Intent(context, CreateOrderActivity.class);
        intent.putExtra(Constant.EXTRA_REQUEST, request);
        context.startActivity(intent);
    }

    public void setDeliverTime(int year, int month, int day) {
        tvDeliverTime.setText(year + "年" + month + "月" + day + "日");
    }

    public void showDatePickDialog(int year, int month, int day) {
        if (dialog == null) {
            dialog = new DatePickerDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("year", year);
            bundle.putInt("month", month);
            bundle.putInt("day", day);
            dialog.setArguments(bundle);
        }
        dialog.show(getFragmentManager(), "DatePicker");
    }

    public void closeDatePickDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void setRequestInfo(User buyer, String title, String content) {
        tvTweetTitle.setText(title);
        tvTweetText.setText(content);
        tvNickName.setText(buyer.getNickname());
        Glide.with(mContext).load(buyer.getAvatar()).into(ivAvatar);
    }

    @Override
    public void setTagList(List<String> tags) {
        boolean isFirst=true;
        if(tags==null||tags.size()==0){
            showIntro(tvAdd, "tvAdd", "在这里添加服务的优势");
        }
        for (String tag : tags) {
            TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tv_tag, null);
            if(isFirst){
                showIntro(tv,"tvTag","点击选择你能满足的买家要求");
            }
            isFirst=false;
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginLayoutParams.setMargins(20, 4, 20, 4);
            tv.setLayoutParams(marginLayoutParams);
            tv.setText(tag);
            tv.setClickable(true);
            tv.setOnClickListener(
                    new View.OnClickListener() {
                        boolean flag = true;

                        @Override
                        public void onClick(View v) {
                            if (flag) {
                                tv.setBackground(clickerDrawable);
                                createOrderPresenter.addTag(tag);
                                flag = false;
                            } else {
                                tv.setBackground(unclickerDrawable);
                                createOrderPresenter.removeTag(tag);
                                flag = true;
                            }
                        }
                    }
            );
            flTags.addView(tv);
        }
    }

    @Override
    public void initPickerView() {
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("人民币");
        list1.add("美元");
        list1.add("港币");
        list1.add("%");
        pvTip.setOnSelectListener(text -> createOrderPresenter.setTipType(text));
        pvTip.setData(list1);
        ArrayList<String> list2 = new ArrayList<>();
        list2.add("人民币");
        list2.add("美元");
        list2.add("港币");
        pvPrice.setOnSelectListener(text -> createOrderPresenter.setPriceType(text));
        pvPrice.setData(list2);
    }

    @Override
    public String getTip() {
        return etTip.getText().toString();
    }

    @Override
    public String getPrice() {
        return etPrice.getText().toString();
    }

    @Override
    public void showMsg(String msg) {
        toast(msg);
    }

    @Override
    public User getSeller() {
        return BmobUser.getCurrentUser(User.class);
    }

    @Override
    public void exit() {
        finish();
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

    @Override
    public void setPrice(String price) {
        mPriceTv.setText(price);
    }

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }
}
