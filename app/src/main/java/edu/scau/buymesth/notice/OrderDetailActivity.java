package edu.scau.buymesth.notice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Address;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.OrderMoment;
import edu.scau.buymesth.notice.detail.OrderMomentAdapter;
import edu.scau.buymesth.notice.detail.PicPublishActivity;
import edu.scau.buymesth.request.requestdetail.RequestDetailActivity;
import edu.scau.buymesth.user.address.AddressActivity;

/**
 * Created by Jammy on 2016/10/4.
 */
public class OrderDetailActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_msg)
    TextView tvMsg;
    @Bind(R.id.request_icon)
    ImageView requestIcon;
    @Bind(R.id.tv_request)
    TextView tvRequest;
    @Bind(R.id.tv_want)
    TextView tvWant;
    @Bind(R.id.tv_seller_price)
    TextView tvSellerPrice;
    @Bind(R.id.tv_seller_tip)
    TextView tvSellerTip;
    @Bind(R.id.ll_address)
    LinearLayout llAddress;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.textView5)
    TextView textView5;
    @Bind(R.id.image_list)
    RecyclerView imageList;
    @Bind(R.id.btn_camera)
    FloatingActionButton btnCamera;
    @Bind(R.id.btn_get)
    Button btnGet;
    @Bind(R.id.btn_cancle)
    Button btnCancle;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.tv_address_msg)
    TextView tvAddressMsg;
    @Bind(R.id.btn_reject)
    Button btnReject;

    Order mOrder;
    @Bind(R.id.ll_request)
    LinearLayout llRequest;
    @Bind(R.id.rl_create_btn)
    RelativeLayout rlCreateBtn;
    @Bind(R.id.rl_moment)
    RelativeLayout rlMoment;
    OrderMomentAdapter orderMomentAdapter;
    @Bind(R.id.btn_go)
    Button btnGo;
    @Bind(R.id.btnGoback)
    Button btnGoback;
    @Bind(R.id.tv_inc)
    TextView tvInc;
    @Bind(R.id.tv_express_num)
    TextView tvExpressNum;
    @Bind(R.id.ll_express)
    LinearLayout llExpress;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    @Override
    public void initView() {
        showLoadingDialog();
        orderMomentAdapter = new OrderMomentAdapter(new ArrayList<>());
        mOrder = (Order) getIntent().getSerializableExtra("order");
        BmobQuery<Order> query = new BmobQuery<>();
        query.include("buyer,request,seller");
        query.getObject(mOrder.getObjectId(), new QueryListener<Order>() {
            @Override
            public void done(Order order, BmobException e) {
                if (e == null) {
                    LinearLayoutManager linearLayoutManager;
                    tvMsg.setVisibility(View.VISIBLE);
                    llRequest.setVisibility(View.VISIBLE);
                    tvRequest.setText(order.getRequest().getTitle());
                    if (order.getRequest().getUrls() != null) {
                        Glide.with(mContext).load(order.getRequest().getUrls().get(0)).placeholder(R.mipmap.def_head).into(requestIcon);
                    }
                    llRequest.setOnClickListener(v -> {
                        RequestDetailActivity.navigate(OrderDetailActivity.this, order.getRequest());
                    });
                    mOrder = order;

                    boolean i = Order.STATUS_ACCEPTED == order.getStatus();
                    switch (order.getItemType()) {
                        case Constant.BUYER_STATUS_CREATE:
                            tvSellerPrice.setVisibility(View.VISIBLE);
                            tvSellerTip.setVisibility(View.VISIBLE);
                            tvWant.setVisibility(View.VISIBLE);
                            rlCreateBtn.setVisibility(View.VISIBLE);
                            llAddress.setVisibility(View.VISIBLE);


                            tvMsg.setText("已有卖家提出报价");
                            tvSellerPrice.setText("卖家出价：" + order.getPrice() + order.getPriceType());
                            tvSellerTip.setText("卖家索要的小费：" + order.getTip() + order.getTipType());
                            if (order.getRequest().getMinPrice() == null)
                                tvWant.setText("你的期望价格：" + order.getRequest().getMaxPrice() + "￥");
                            else
                                tvWant.setText("你的期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
                            llAddress.setOnClickListener(v -> {
                                AddressActivity.navigateForResult(OrderDetailActivity.this);
                            });
                            btnOk.setOnClickListener(v -> {
                                if (order.getAddress() == null) {
                                    Toast.makeText(OrderDetailActivity.this, "请选择收货地址", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                order.setStatus(Order.STATUS_ACCEPTED);
                                order.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            ////TODO：应该显示已接收的界面

                                            tvMsg.setText("你已接收订单，等待卖家发货");
                                            rlCreateBtn.setVisibility(View.GONE);
                                            rlMoment.setVisibility(View.VISIBLE);
                                            tvAddressMsg.setText("买家地址是：收货人：" + order.getAddress().getRecipient() + "\n手机号码：" + order.getAddress().getPhone() + "\n地址：" + order.getAddress().getRegion() + order.getAddress().getSpecific());
                                        } else {
                                            Toast.makeText(OrderDetailActivity.this, "请重试", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            });

                            btnReject.setOnClickListener(v -> {
                                order.setStatus(Order.STATUS_ACCEPTED);
                                order.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            ////TODO：应该显示已拒绝的界面
                                            btnCancle.setText("已取消");
                                            btnCancle.setClickable(false);
                                            btnOk.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(OrderDetailActivity.this, "请重试", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            });

                            break;

                        case Constant.SELLER_STATUS_CREATE:
                            tvWant.setVisibility(View.VISIBLE);
                            tvSellerPrice.setVisibility(View.VISIBLE);
                            tvSellerTip.setVisibility(View.VISIBLE);
                            btnCancle.setVisibility(View.VISIBLE);

                            tvMsg.setText("你已向买家提出报价，等待买家确认");
                            if (order.getRequest().getMinPrice() == null)
                                tvWant.setText("买家期望价格：" + order.getRequest().getMaxPrice() + "￥");
                            else
                                tvWant.setText("买家期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
                            tvSellerPrice.setText("你的出价:" + order.getPrice() + order.getPriceType());
                            tvSellerTip.setText("你索要的小费" + order.getTip() + order.getTipType());
                            btnCancle.setOnClickListener(v -> {
                                order.setStatus(Order.STATUS_SELLER_REJECT);
                                order.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            /////todo：跳转到拒绝界面
                                            btnCancle.setText("订单已取消");
                                            btnCancle.setClickable(false);
                                        } else {
                                            Toast.makeText(OrderDetailActivity.this, "出错啦！请重试", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            });

                            break;
                        case Constant.BUYER_STATUS_ACCEPT:
                            rlMoment.setVisibility(View.VISIBLE);
                            btnCamera.setVisibility(View.GONE);
                            tvMsg.setText("你已接收订单，等待卖家发货");
                            tvAddressMsg.setText("收货地址是：收货人：" + order.getAddress().getRecipient() + "\n手机号码：" + order.getAddress().getPhone() + "\n地址：" + order.getAddress().getRegion() + order.getAddress().getSpecific());
                            linearLayoutManager = new LinearLayoutManager(OrderDetailActivity.this);
                            imageList.setLayoutManager(linearLayoutManager);
                            imageList.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
                            imageList.setNestedScrollingEnabled(false);
                            imageList.setAdapter(orderMomentAdapter);

                            BmobQuery<OrderMoment> query = new BmobQuery<>();
                            query.addWhereEqualTo("order", order);
                            showLoadingDialog();
                            query.findObjects(new FindListener<OrderMoment>() {
                                @Override
                                public void done(List<OrderMoment> list, BmobException e) {
                                    if (e == null) {
                                        orderMomentAdapter.setNewData(list);
                                        closeLoadingDialog();
                                    } else {
                                        Toast.makeText(OrderDetailActivity.this, "网络有问题，请重试", Toast.LENGTH_LONG).show();
                                        closeLoadingDialog();
                                    }
                                }
                            });

                            break;

                        case Constant.SELLER_STATUS_ACCEPT:
                            ////TODO:发货按钮和取消订单按钮
                            tvMsg.setText("买家已接收你的报价，现在可以开始发货了");
                            rlMoment.setVisibility(View.VISIBLE);
                            btnCamera.setVisibility(View.VISIBLE);
                            btnGoback.setVisibility(View.VISIBLE);
                            btnGo.setVisibility(View.VISIBLE);
                            llExpress.setVisibility(View.VISIBLE);

                            linearLayoutManager = new LinearLayoutManager(OrderDetailActivity.this);
                            imageList.setLayoutManager(linearLayoutManager);
                            imageList.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
                            imageList.setNestedScrollingEnabled(false);
                            imageList.setAdapter(orderMomentAdapter);
                            tvAddress.setText("买家地址是：收货人：" + order.getAddress().getRecipient() + "\n手机号码：" + order.getAddress().getPhone() + "\n地址：" + order.getAddress().getRegion() + order.getAddress().getSpecific());
                            btnCamera.setOnClickListener(v -> {
                                Intent intent = new Intent(OrderDetailActivity.this, PicPublishActivity.class);
                                intent.putExtra("order", order);
                                startActivity(intent);
                            });

                            llExpress.setOnClickListener(v -> {
                                View priceRangeView = getLayoutInflater().inflate(R.layout.dialog_express, null);
                                EditText etInc = (EditText) priceRangeView.findViewById(R.id.et_inc);
                                EditText etExpressNum = (EditText) priceRangeView.findViewById(R.id.et_express_num);
                                AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("请输入价格范围").setView(priceRangeView).setNegativeButton("取消", null).setPositiveButton("确定", (dialog1, which) -> {
                                    tvInc.setText(etInc.toString());
                                    tvExpressNum.setText(etExpressNum.toString());
                                }).create();
                            });

                            btnGo.setOnClickListener(v -> {
                                if(tvInc.getText().equals("")||tvExpressNum.equals("")){
                                    Toast.makeText(OrderDetailActivity.this,"请正确填写快递信息",Toast.LENGTH_LONG).show();
                                    return ;
                                }
                                order.setStatus(Order.STATUS_DELIVERING);
                                order.setExpressInc((String) tvInc.getText());
                                order.setExpressNumber((String) tvExpressNum.getText());
                                order.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Log.i("bmob", "更新成功");
                                        } else {
                                            Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                                        }
                                    }
                                });
                            });

                            btnGoback.setOnClickListener(v -> {
                                /////TODO:取消订单的情况
                            });

                            BmobQuery<OrderMoment> query_seller_accept = new BmobQuery<>();
                            query_seller_accept.addWhereEqualTo("order", order);
                            query_seller_accept.findObjects(new FindListener<OrderMoment>() {
                                @Override
                                public void done(List<OrderMoment> list, BmobException e) {
                                    if (e == null) {
                                        orderMomentAdapter.setNewData(list);
                                    } else {
                                        Toast.makeText(OrderDetailActivity.this, "网络有问题，请重试", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            break;

                        case Constant.BUYER_STATUS_REJECT:

                            break;

                        case Constant.SELLER_STATUS_REJECT:

                            break;

                        case Constant.BUYER_STATUS_DELIVERING:

                            break;

                        case Constant.SELLER_STATUS_DELIVERING:

                            break;

                        case Constant.BUYER_STATUS_FINISH:

                            break;

                        case Constant.SELLER_STATUS_FINISH:

                            break;
                    }

                    closeLoadingDialog();
                } else {

                }
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AddressActivity.REQUEST_PICK_ADDRESS) {
            Address address = (Address) data.getSerializableExtra(AddressActivity.RESULT_ADDRESS);
            mOrder.setAddress(address);
            tvAddress.setText("收货人：" + address.getRecipient() + "\n手机号码：" + address.getPhone() + "\n地址：" + address.getRegion() + address.getSpecific());
        }
    }

}
