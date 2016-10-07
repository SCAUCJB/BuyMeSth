package edu.scau.buymesth.cash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import base.BaseActivity;
import base.util.GlideCircleTransform;
import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;


/**
 * Created by Jammy on 2016/10/7.
 */
public class CashMainActivity extends BaseActivity {
    User user;
    @Bind(R.id.btn_withdraw)
    Button btnWithdraw;
    @Bind(R.id.btn_deposit)
    Button btnDeposit;
    @Bind(R.id.tv_cash_detail)
    TextView tvCashDetail;
    @Bind(R.id.iv_icon)
    ImageView ivIcon;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_cash)
    TextView tvCash;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cash;
    }

    @Override
    public void initView() {
        user = (User) getIntent().getSerializableExtra("user");
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User u, BmobException e) {
                if (e == null) {
                    user = u;
                    if (user.getAvatar() != null) {
                        Glide.with(mContext).load(user.getAvatar()).placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into(ivIcon);
                    }
                    tvName.setText(user.getNickname());
                    tvCash.setText("当前账户余额为：" + user.getBalance()+"￥");
                }else{
                    Toast.makeText(CashMainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnDeposit.setOnClickListener(v -> {
            DepositActivity.navigate(CashMainActivity.this, user);
        });

        btnWithdraw.setOnClickListener(v -> {
            WithdrawActivity.navigate(CashMainActivity.this, user);
        });

        tvCashDetail.setOnClickListener(v -> {
            CashBookActivity.navigate(CashMainActivity.this, user);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvCash.setText("当前账户余额为：-----￥");
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User u, BmobException e) {
                if (e == null) {
                    user = u;
                    if (user.getAvatar() != null) {
                        Glide.with(mContext).load(user.getAvatar()).placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into(ivIcon);
                    }
                    tvName.setText(user.getNickname());
                    tvCash.setText("当前账户余额为：" + user.getBalance()+"￥");
                }else{
                    Toast.makeText(CashMainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    public static void navigate(Activity activity, User user) {
        Intent intent = new Intent(activity, CashMainActivity.class);
        intent.putExtra("user", user);
        activity.startActivity(intent);
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }
}
