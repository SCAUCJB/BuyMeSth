package edu.scau.buymesth.cash;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
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
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.data.bean.Wallet;


/**
 * Created by Jammy on 2016/10/7.
 */
public class CashMainActivity extends BaseActivity {
    User user;
//    @Bind(R.id.btn_withdraw)
//    Button btnWithdraw;
//    @Bind(R.id.btn_deposit)
//    Button btnDeposit;
    Wallet wallet;
//    @Bind(R.id.btn_withdraw)
//    Button btnWithdraw;
//    @Bind(R.id.btn_deposit)
//    Button btnDeposit;
    @Bind(R.id.tv_cash_detail)
    TextView tvCashDetail;
    @Bind(R.id.iv_icon)
    ImageView ivIcon;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_cash)
    TextView tvCash;
    @Bind(R.id.tv_id)
    TextView tvUserId;
    @Bind(R.id.iv_refresh)
    ImageView ivRefresh;
    @Bind(R.id.rv_2)
    ViewGroup mRv2;
    @Bind(R.id.ly_btn_deposit_withdraw)
    View mButtonDepositWithdraw;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cash;
    }

    @Override
    public void initView() {
        user = (User) getIntent().getSerializableExtra("user");
        if (user.getAvatar() != null) {
            Glide.with(mContext).load(user.getAvatar()).placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into(ivIcon);
        }
        tvName.setText(user.getNickname());
        query();
//        btnDeposit.setOnClickListener(v -> {
//            if(user!=null)
//                DepositActivity.navigate(CashMainActivity.this, user);
//        });
//
//        btnWithdraw.setOnClickListener(v -> {
//            if(user!=null)
//                WithdrawActivity.navigate(CashMainActivity.this, user);

//        btnDeposit.setOnClickListener(v -> {
//            if(user!=null)
//                DepositActivity.navigate(CashMainActivity.this, user);
//        });
//
//        btnWithdraw.setOnClickListener(v -> {
//            if(user!=null)
//                WithdrawActivity.navigate(CashMainActivity.this, user);
//        });
        mButtonDepositWithdraw.setOnClickListener(v -> {
            String[] items = {"充值","提现"};
            new AlertDialog.Builder(CashMainActivity.this)
                    .setItems(items, (dialog, which) -> {
                        switch (which){
                            case 0:
                                if(user!=null)
                                    DepositActivity.navigate(CashMainActivity.this, user);
                                break;
                            case 1:
                                if(user!=null)
                                    WithdrawActivity.navigate(CashMainActivity.this, user);
                                break;
                        }
                    }).show();
        });

        tvCashDetail.setOnClickListener(v -> {
            if(user!=null)
                CashBookActivity.navigate(CashMainActivity.this, user);
        });
        mRv2.setOnClickListener(v -> {
            if(user!=null)
                CashBookActivity.navigate(CashMainActivity.this, user);
        });
        ivRefresh.setOnClickListener(v -> ivRefresh.post(() -> query()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        query();
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

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }

    public void query(){
        showLoadingDialog();
        tvCash.setText("当前账户余额为：-----￥");
        BmobQuery<Wallet> query = new BmobQuery<>();
        query.addWhereEqualTo("user",BmobUser.getCurrentUser().getObjectId());
        query.findObjects( new FindListener<Wallet>() {
            @Override
            public void done(List<Wallet> list, BmobException e) {
                if(e==null){
                    wallet = list.get(0);
                    tvCash.setText("当前账户余额为：" + wallet.getCash()+"￥");
                    closeLoadingDialog();
                }else{
                    Toast.makeText(CashMainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    closeLoadingDialog();
                }}});
    }

}
