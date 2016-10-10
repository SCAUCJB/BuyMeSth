package edu.scau.buymesth.cash;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import base.BaseActivity;
import butterknife.Bind;
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
import edu.scau.buymesth.util.InputMethodHelper;

/**
 * Created by Jammy on 2016/10/7.
 */
public class DepositActivity extends BaseActivity {

    User user;
    @Bind(R.id.et_money)
    EditText etMoney;
    @Bind(R.id.btn_Ok)
    Button btnOk;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_deposit;
    }

    public static void navigate(Activity activity, User user) {
        Intent intent = new Intent(activity, DepositActivity.class);
        intent.putExtra("user", user);
        activity.startActivity(intent);
    }

    @Override
    public void initView() {
        user = (User) getIntent().getSerializableExtra("user");
        btnOk.setOnClickListener(v -> {
            if (etMoney.getText().toString().equals("")) {
                Toast.makeText(DepositActivity.this, "请填写充值金额", Toast.LENGTH_SHORT).show();
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
            ace.callEndpoint("recharge", params, new CloudCodeListener() {
                @Override
                public void done(Object o, BmobException e) {
                    if (o != null) {
                        if (((String) o).equals("success")) {
                            Toast.makeText(DepositActivity.this, "充值成功", Toast.LENGTH_SHORT).show();
                            hideBroad();
                            finish();
                            closeLoadingDialog();
                        } else {
                            Toast.makeText(DepositActivity.this, "充值失败", Toast.LENGTH_SHORT).show();
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

    public static void navigateForResult(Activity activity, User user) {
        Intent intent = new Intent(activity, DepositActivity.class);
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
