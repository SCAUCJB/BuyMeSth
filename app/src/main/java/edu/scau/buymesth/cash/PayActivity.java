package edu.scau.buymesth.cash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.CashBook;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by Jammy on 2016/10/7.
 */
public class PayActivity extends BaseActivity {

    Order order;
    User user;
    Float sum;

    private static final int PAY_SUCCESS = 10000;
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
        tvSum.setText("商品价格：");

        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User u, BmobException e) {
                if(e==null){
                    user = u;
                    tvMoney.setText("账户余额："+user.getBalance());
                }else{

                }
            }
        });


        btnAdd.setOnClickListener(v -> {
            DepositActivity.navigate(PayActivity.this,user);
        });
        btnSubmit.setOnClickListener(v -> {
            BmobQuery<User> query1 = new BmobQuery<>();
            query1.getObject(user.getObjectId(), new QueryListener<User>() {
                @Override
                public void done(User u, BmobException e) {
                    if(e==null){
                        user = u;
                        tvMoney.setText("账户余额："+user.getBalance());
                        if(user.getBalance()<sum){
                            Toast.makeText(PayActivity.this,"账户余额不足，请充值",Toast.LENGTH_SHORT).show();
                            return ;
                        }

                        user.setBalance(user.getBalance()-sum);
                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    CashBook cashBook = new CashBook();
                                    cashBook.setUser(order.getBuyer());
                                    ////TODO:
//                                    cashBook.setType();
//                                    cashBook.setDescribe();
                                    cashBook.setToUser(order.getSeller());
                                    cashBook.setToOrder(order);
                                    cashBook.setCash(sum);
                                    cashBook.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if(e==null){
                                                Toast.makeText(PayActivity.this,"付款成功",Toast.LENGTH_SHORT).show();
                                                ////TODO：startActivityForResult
                                                finish();
                                            }
                                        }
                                    });
                                }else{

                                }
                            }
                        });

                    }else{

                    }
                }
            });
        });
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

