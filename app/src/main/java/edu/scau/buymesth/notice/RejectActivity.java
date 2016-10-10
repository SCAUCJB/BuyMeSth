package edu.scau.buymesth.notice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.CashBook;
import edu.scau.buymesth.data.bean.Evaluate;
import edu.scau.buymesth.data.bean.Notificate;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by Jammy on 2016/10/6.
 */
public class RejectActivity extends BaseActivity {

    public static final int REJECT_SUCCESS = 10087;
    Order order;
    @Bind(R.id.btn_go)
    Button btnGo;
    @Bind(R.id.et)
    EditText et;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reject;
    }

    @Override
    public void initView() {
        order = (Order) getIntent().getSerializableExtra("order");
        btnGo.setOnClickListener(v -> {
            if (et.getText().toString().equals("")) {
                Toast.makeText(RejectActivity.this, "请填写理由", Toast.LENGTH_SHORT).show();
                return;
            }


            showLoadingDialog();
            AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
            JSONObject params = new JSONObject();
            try {
                params.put("orderid", order.getObjectId());
                params.put("buyerid", order.getBuyer().getObjectId());
                params.put("reason", et.getText().toString());
                params.put("requestid",order.getRequest().getObjectId());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            ace.callEndpoint("sellerReject", params, new CloudCodeListener() {
                @Override
                public void done(Object o, BmobException e) {
                    if (o != null) {
                        if (((String) o).equals("success")) {
                            Toast.makeText(RejectActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                            order.setRejectReason(et.getText().toString());
                            Intent data = new Intent();
                            data.putExtra("order", order);
                            setResult(REJECT_SUCCESS, data);
                            finish();
                            closeLoadingDialog();
                        } else if (((String) o).equals("reject")) {
                            Toast.makeText(RejectActivity.this, "订单已取消", Toast.LENGTH_SHORT).show();
                            closeLoadingDialog();
                        } else {
                            Toast.makeText(RejectActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                            closeLoadingDialog();
                        }
                    }else{
                        Toast.makeText(RejectActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                        closeLoadingDialog();
                    }
                }
            });
        });
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    public static void navigateForResult(Activity activity, Order order) {
        Intent intent = new Intent(activity, RejectActivity.class);
        intent.putExtra("order", order);
        activity.startActivityForResult(intent, REJECT_SUCCESS);
    }

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }

}