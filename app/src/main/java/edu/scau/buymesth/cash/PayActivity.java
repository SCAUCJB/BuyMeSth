package edu.scau.buymesth.cash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import base.BaseActivity;
import butterknife.Bind;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.CashBook;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.data.bean.Wallet;

/**
 * Created by Jammy on 2016/10/7.
 */
public class PayActivity extends BaseActivity {

    Order order;
    User user;
    Float sum;

    public static final int PAY_SUCCESS = 10000;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.tv_sum)
    TextView tvSum;
    @Bind(R.id.btn_add)
    Button btnAdd;
    @Bind(R.id.btn_submit)
    Button btnSubmit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay;
    }

    @Override
    public void initView() {
        order = (Order) getIntent().getSerializableExtra("order");
        user = order.getBuyer();
        if (order.getPriceType().equals("人民币")) {
            sum = order.getPrice();
        } else if (order.getPriceType().equals("美元")) {
            sum = order.getPrice() * 6;
        } else if (order.getPriceType().equals("港币")) {
            sum = order.getPrice() * 0.8f;
        }

        if (order.getTipType().equals("人民币")) {
            sum += order.getTip();
        } else if (order.getTipType().equals("美元")) {
            sum += (order.getTip() * 6);
        } else if (order.getTipType().equals("港币")) {
            sum += (order.getTip() * 0.8f);
        } else {
            sum = sum + sum * order.getTip();
        }

        tvSum.setText("商品价格：" + sum);
        
        BmobQuery<Wallet> query = new BmobQuery<>();
        query.addWhereEqualTo("user",BmobUser.getCurrentUser().getObjectId());
        query.findObjects( new FindListener<Wallet>() {
            @Override
            public void done(List<Wallet> list, BmobException e) {
                if(e==null){
                    tvMoney.setText("当前账户余额为：" + list.get(0).getCash()+"￥");
                }else{
                    Toast.makeText(PayActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }}});




        btnAdd.setOnClickListener(v -> {
            DepositActivity.navigate(PayActivity.this, user);
        });
        btnSubmit.setOnClickListener(v -> {

            showLoadingDialog();

            AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
            JSONObject params = new JSONObject();
            try {
                params.put("buyerid",order.getBuyer().getObjectId());
                params.put("sellerid",order.getSeller().getObjectId());
                params.put("orderid",order.getObjectId());
                params.put("addressid",order.getAddress().getObjectId());
                params.put("num",sum);
                params.put("requestid",order.getRequest().getObjectId());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            ace.callEndpoint("acceptOrder", params, new CloudCodeListener() {
                @Override
                public void done(Object o, BmobException e) {
                    if (o != null) {
                        if (((String) o).equals("success")) {
                            Toast.makeText(PayActivity.this, "付款成功", Toast.LENGTH_SHORT).show();
                            Intent data = new Intent();
                            data.putExtra("order", order);
                            setResult(PAY_SUCCESS, data);
                            finish();
                            closeLoadingDialog();
                        }else if(((String) o).equals("out")){
                            Toast.makeText(PayActivity.this, "余额不足，请充值", Toast.LENGTH_SHORT).show();
                            closeLoadingDialog();
                        }else
                        {
                            Toast.makeText(PayActivity.this, "付款失败，请重试", Toast.LENGTH_SHORT).show();
                            closeLoadingDialog();
                        }
                    }
                }
            });
    });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BmobQuery<Wallet> query = new BmobQuery<>();
        query.addWhereEqualTo("user",BmobUser.getCurrentUser().getObjectId());
        query.findObjects( new FindListener<Wallet>() {
            @Override
            public void done(List<Wallet> list, BmobException e) {
                if(e==null){
                    tvMoney.setText("当前账户余额为：" + list.get(0).getCash()+"￥");
                }else{
                    Toast.makeText(PayActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }}});
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    public static void navigateForResult(Activity activity, Order order) {
        Intent intent = new Intent(activity, PayActivity.class);
        intent.putExtra("order", order);
        activity.startActivityForResult(intent, PAY_SUCCESS);
    }

}

