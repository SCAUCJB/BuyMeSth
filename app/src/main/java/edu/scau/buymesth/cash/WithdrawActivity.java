package edu.scau.buymesth.cash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.CashBook;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.publish.FlowLayout;
import edu.scau.buymesth.util.InputMethodHelper;

/**
 * Created by Jammy on 2016/10/7.
 */
public class WithdrawActivity extends BaseActivity {

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
            if (etMoney.getText().toString().equals("")) {
                Toast.makeText(WithdrawActivity.this, "请填写提现金额", Toast.LENGTH_SHORT).show();
                return;
            }
            showLoadingDialog();
            AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
            JSONObject params = new JSONObject();
            try {
                params.put("userid", BmobUser.getCurrentUser().getObjectId());
                params.put("num", etMoney.getText().toString());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            ace.callEndpoint("withdraw", params, new CloudCodeListener() {
                @Override
                public void done(Object o, BmobException e) {
                    if (o != null) {
                        if (((String) o).equals("success")) {
                            Toast.makeText(WithdrawActivity.this, "提现成功", Toast.LENGTH_SHORT).show();
                            hideBroad();
                            finish();
                            closeLoadingDialog();
                        } else if (((String) o).equals("big")) {
                            Toast.makeText(WithdrawActivity.this, "余额不足！！", Toast.LENGTH_SHORT).show();
                            closeLoadingDialog();
                        } else {
                            Toast.makeText(WithdrawActivity.this, "提现失败", Toast.LENGTH_SHORT).show();
                            closeLoadingDialog();
                        }
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

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }

    public void hideBroad() {
        InputMethodHelper.toggle(this);
        new Handler().postDelayed(() -> InputMethodHelper.closeFromView(mContext, etMoney), 100);
    }

}
