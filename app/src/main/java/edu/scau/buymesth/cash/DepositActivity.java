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
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.CashBook;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by Jammy on 2016/10/7.
 */
public class DepositActivity extends BaseActivity {

    ///TODO:充值成功后还要加入CashBook表中

    User user;
    @Bind(R.id.et_money)
    EditText etMoney;
    @Bind(R.id.btn_Ok)
    Button btnOk;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_deposit;
    }

    @Override
    public void initView() {
        user = (User) getIntent().getSerializableExtra("order");
        btnOk.setOnClickListener(v -> {
            if(etMoney.getText().toString().equals("")){
                Toast.makeText(DepositActivity.this,"请填写充值金额",Toast.LENGTH_SHORT).show();
                return ;
            }
            BmobQuery<User> query = new BmobQuery<User>();
            query.getObject(user.getObjectId(), new QueryListener<User>() {
                @Override
                public void done(User u, BmobException e) {
                    if(e==null){
                        user = u;
                        user.setBalance(user.getBalance()+Float.valueOf(etMoney.getText().toString()));
                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    CashBook cashBook = new CashBook();
                                    cashBook.setCash(Float.valueOf(etMoney.getText().toString()));
                                    cashBook.setToUser(user);
                                    cashBook.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {

                                        }
                                    });
                                    Toast.makeText(DepositActivity.this,"充值成功",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(DepositActivity.this,"请重试",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{

                    }
                }
            });
        });
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    public static void navigateForResult(Activity activity, User user) {
        Intent intent = new Intent(activity, DepositActivity.class);
        intent.putExtra("user", user);
        activity.startActivity(intent);
    }

    public static void navigate(Activity activity, User user) {
        Intent intent = new Intent(activity, DepositActivity.class);
        intent.putExtra("user", user);
        activity.startActivity(intent);
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }
}
