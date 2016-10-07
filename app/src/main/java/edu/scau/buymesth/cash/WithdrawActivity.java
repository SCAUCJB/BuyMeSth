package edu.scau.buymesth.cash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.publish.FlowLayout;

/**
 * Created by Jammy on 2016/10/7.
 */
public class WithdrawActivity extends BaseActivity {

    ///TODO:提现成功后还要加入CashBook表中


    User user;
    @Bind(R.id.et_money)
    EditText etMoney;
    @Bind(R.id.btn_Ok)
    Button btnOk;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_withdraw;
    }

    @Override
    public void initView() {
        user = (User) getIntent().getSerializableExtra("order");
        btnOk.setOnClickListener(v -> {
            if(etMoney.getText().toString().equals("")){
                Toast.makeText(WithdrawActivity.this,"请填写提现金额",Toast.LENGTH_SHORT).show();
                return ;
            }

            BmobQuery<User> query = new BmobQuery<User>();
            query.getObject(user.getObjectId(), new QueryListener<User>() {
                @Override
                public void done(User u, BmobException e) {
                    if(e==null){
                        user = u;
                        if(user.getBalance()<Float.valueOf(etMoney.getText().toString())){
                            Toast.makeText(WithdrawActivity.this,"余额不足",Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        user.setBalance(user.getBalance()-Float.valueOf(etMoney.getText().toString()));
                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(WithdrawActivity.this,"提现成功",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(WithdrawActivity.this,"请重试",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(WithdrawActivity.this,"请重试",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    public static void navigate(Activity activity, User user) {
        Intent intent = new Intent(activity, WithdrawActivity.class);
        intent.putExtra("user", user);
        activity.startActivity(intent);
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }
}
