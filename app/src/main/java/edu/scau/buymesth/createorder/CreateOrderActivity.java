package edu.scau.buymesth.createorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.publish.FlowLayout;

import static edu.scau.buymesth.homedetail.RequestDetailActivity.EXTRA_REQUEST;

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
    @Bind(R.id.tv_tweet_date)
    TextView tvTweetDate;
    @Bind(R.id.tv_tweet_title)
    TextView tvTweetTitle;
    @Bind(R.id.tv_tweet_text)
    TextView tvTweetText;

    DatePickerDialogFragment dialog = null;
    CreateOrderPresenter createOrderPresenter;
    private AlertDialog tagInputDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_order;
    }

    protected void initPresenter() {
        mPresenter = new CreateOrderPresenter();
        createOrderPresenter = (CreateOrderPresenter) mPresenter;
        CreateOrderModel model = new CreateOrderModel();
        model.setRequest((Request) getIntent().getSerializableExtra(EXTRA_REQUEST));

        createOrderPresenter.setVM(this, model);
    }

    @Override
    public void initView() {
        final EditText editText = new EditText(mContext);
        tagInputDialog = new AlertDialog.Builder(mContext).setView(editText).setPositiveButton("确定", (dialog, which) -> {
            String tag = editText.getText().toString();
            TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tv_tag, null);
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginLayoutParams.setMargins(4, 4, 4, 4);
            tv.setLayoutParams(marginLayoutParams);
            tv.setText(tag);
            tv.setClickable(true);
            tv.setOnClickListener(v ->
                    tv.setBackground(getResources().getDrawable(R.drawable.rect_accent))
            );
            flTags.addView(tv);
        }).create();
    }

    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    protected void setListener() {
        tvDeliverTime.setOnClickListener(v -> createOrderPresenter.onDeliverTimeClicked());
        tvAdd.setOnClickListener(v->tagInputDialog.show());
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }


    public static void navigateTo(Context context, Request request) {
        Intent intent = new Intent(context, CreateOrderActivity.class);
        intent.putExtra(EXTRA_REQUEST, request);
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
    public void setRequestInfo(User buyer, String title, String content, String createdAt) {
        tvTweetTitle.setText(title);
        tvTweetText.setText(content);
        tvTweetDate.setText(createdAt);
        tvNickName.setText(buyer.getNickname());
        Glide.with(mContext).load(buyer.getAvatar()).into(ivAvatar);
    }

    @Override
    public void setTagList(List<String> tags) {

        for (String tag : tags) {
            TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tv_tag, null);
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginLayoutParams.setMargins(4, 4, 4, 4);
            tv.setLayoutParams(marginLayoutParams);
            tv.setText(tag);
            tv.setClickable(true);
            tv.setOnClickListener(v -> {
                        tv.setBackground(getResources().getDrawable(R.drawable.rect_accent));
                        createOrderPresenter.addTag(tag);
                    }
            );
            flTags.addView(tv);
        }
    }


}
