package edu.scau.buymesth.notice;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.PicAdapter;
import edu.scau.buymesth.cash.PayActivity;
import edu.scau.buymesth.data.bean.Address;
import edu.scau.buymesth.data.bean.Evaluate;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.OrderMoment;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.notice.detail.OrderMomentAdapter;
import edu.scau.buymesth.notice.detail.PicPublishActivity;
import edu.scau.buymesth.request.requestdetail.RequestDetailActivity;
import edu.scau.buymesth.user.address.AddressActivity;

/**
 * Created by Jammy on 2016/10/4.
 */
public class OrderDetailActivity extends BaseActivity {
    public static void navigate(Activity activity, Order order) {
        Intent intent = new Intent(activity, OrderDetailActivity.class);
        intent.putExtra("order", order);
        activity.startActivity(intent);
    }

    public static void navigate(Activity activity, String id) {
        Intent intent = new Intent(activity, OrderDetailActivity.class);
        intent.putExtra("id", id);
        activity.startActivity(intent);
    }

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
    @Bind(R.id.t_tip)
    TextView tTip;
    @Bind(R.id.rl_get)
    RelativeLayout rlGet;
    @Bind(R.id.btn_comment)
    Button btnComment;
    @Bind(R.id.ratingBar)
    RatingBar ratingBar;
    @Bind(R.id.buyer_evaluate)
    TextView buyerEvaluate;
    @Bind(R.id.ll_evaluate)
    LinearLayout llEvaluate;
    @Bind(R.id.seller_evaluate)
    TextView sellerEvaluate;
    @Bind(R.id.rv_evaluate)
    RecyclerView rvEvaluate;

    PicAdapter picAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    @Override
    public void initView() {
        showLoadingDialog();
        orderMomentAdapter = new OrderMomentAdapter(new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetailActivity.this);
        imageList.setLayoutManager(linearLayoutManager);
        imageList.setNestedScrollingEnabled(false);
        imageList.setAdapter(orderMomentAdapter);
        imageList.addItemDecoration(new SpaceItemDecoration(45));

        picAdapter = new PicAdapter(new ArrayList<>());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(OrderDetailActivity.this, 3);
        rvEvaluate.setLayoutManager(gridLayoutManager);
        rvEvaluate.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
        rvEvaluate.setNestedScrollingEnabled(false);
        rvEvaluate.setAdapter(picAdapter);


        mOrder = (Order) getIntent().getSerializableExtra("order");
        String id;
        if (mOrder == null) {
            id = getIntent().getStringExtra("id");
        } else {
            id = mOrder.getObjectId();
        }
        BmobQuery<Order> query = new BmobQuery<>();
        query.include("buyer,request,seller,address,evaluate");
        query.getObject(id, new QueryListener<Order>() {
            @Override
            public void done(Order order, BmobException e) {
                if (e == null) {
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
                                if (order.getRequest().getAccecpted()) {
                                    Toast.makeText(OrderDetailActivity.this, "此订单已被接收", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                PayActivity.navigateForResult(OrderDetailActivity.this, order);
                            });

                            btnReject.setOnClickListener(v -> {
                                AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                                JSONObject params = new JSONObject();
                                try {
                                    params.put("orderid", order.getObjectId());
                                    params.put("sellerid", order.getSeller().getObjectId());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                showLoadingDialog();
                                ace.callEndpoint("rejectOrder", params, new CloudCodeListener() {
                                    @Override
                                    public void done(Object o, BmobException e) {
                                        if (o != null) {
                                            if (((String) o).equals("success")) {
                                                Toast.makeText(OrderDetailActivity.this, "拒绝成功", Toast.LENGTH_SHORT).show();
                                                tvMsg.setText("你已拒绝了这位卖家的报价");
                                                rlCreateBtn.setVisibility(View.GONE);
                                                closeLoadingDialog();
                                            } else {
                                                Toast.makeText(OrderDetailActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                                                closeLoadingDialog();
                                            }
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
                                RejectActivity.navigateForResult(OrderDetailActivity.this, order);
                            });

                            break;
                        case Constant.BUYER_STATUS_ACCEPT:
                            rlMoment.setVisibility(View.VISIBLE);
                            tvWant.setVisibility(View.VISIBLE);
                            tvSellerPrice.setVisibility(View.VISIBLE);
                            tvSellerTip.setVisibility(View.VISIBLE);
                            btnCamera.setVisibility(View.GONE);
                            tvMsg.setText("你已接收订单，等待卖家发货");
                            tvAddressMsg.setText("收货人：" + order.getAddress().getRecipient() + "\n手机号码：" + order.getAddress().getPhone() + "\n地址：" + order.getAddress().getRegion() + order.getAddress().getSpecific());
                            if (order.getRequest().getMinPrice() == null)
                                tvWant.setText("买家期望价格：" + order.getRequest().getMaxPrice() + "￥");
                            else
                                tvWant.setText("买家期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
                            tvSellerPrice.setText("你的出价:" + order.getPrice() + order.getPriceType());
                            tvSellerTip.setText("你索要的小费" + order.getTip() + order.getTipType());
                            BmobQuery<OrderMoment> query = new BmobQuery<>();
                            query.addWhereEqualTo("order", order);
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
                            closeLoadingDialog();
                            tvMsg.setText("买家已接收你的报价，现在可以开始发货了");
                            rlMoment.setVisibility(View.VISIBLE);
                            btnCamera.setVisibility(View.VISIBLE);
                            btnGoback.setVisibility(View.VISIBLE);
                            btnGo.setVisibility(View.VISIBLE);
                            llExpress.setVisibility(View.VISIBLE);
                            tvWant.setVisibility(View.VISIBLE);
                            tvSellerPrice.setVisibility(View.VISIBLE);
                            tvSellerTip.setVisibility(View.VISIBLE);
                            if (order.getRequest().getMinPrice() == null)
                                tvWant.setText("买家期望价格：" + order.getRequest().getMaxPrice() + "￥");
                            else
                                tvWant.setText("买家期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
                            tvSellerPrice.setText("你的出价:" + order.getPrice() + order.getPriceType());
                            tvSellerTip.setText("你索要的小费" + order.getTip() + order.getTipType());
                            tvAddressMsg.setText("买家地址是：收货人：" + order.getAddress().getRecipient() + "\n手机号码：" + order.getAddress().getPhone() + "\n地址：" + order.getAddress().getRegion() + order.getAddress().getSpecific());
                            btnCamera.setOnClickListener(v -> {
                                Intent intent = new Intent(OrderDetailActivity.this, PicPublishActivity.class);
                                intent.putExtra("order", order);
                                startActivity(intent);
                            });

                            llExpress.setOnClickListener(v -> {
                                View priceRangeView = getLayoutInflater().inflate(R.layout.dialog_express, null);
                                EditText etInc = (EditText) priceRangeView.findViewById(R.id.et_inc);
                                EditText etExpressNum = (EditText) priceRangeView.findViewById(R.id.et_express_num);
                                AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("请输入快递信息").setView(priceRangeView).setNegativeButton("取消", null).setPositiveButton("确定", (dialog1, which) -> {
                                    tvInc.setText("快递公司：" + etInc.getText().toString());
                                    tvExpressNum.setText("快递单号：" + etExpressNum.getText().toString());
                                }).show();
                            });

                            btnGo.setOnClickListener(v -> {
                                        if (tvInc.getText().equals("") || tvExpressNum.equals("")) {
                                            llExpress.performClick();
                                            return;
                                        }
                                        showLoadingDialog();
                                                AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                                                JSONObject params = new JSONObject();
                                                try {
                                                    params.put("orderid", order.getObjectId());
                                                    params.put("buyerid", order.getBuyer().getObjectId());
                                                    params.put("expressnum", tvExpressNum.getText().toString());
                                                    params.put("incnum", tvInc.getText().toString());
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                                ace.callEndpoint("sendgood", params, new CloudCodeListener() {
                                                    @Override
                                                    public void done(Object o, BmobException e) {
                                                        if (o != null) {
                                                            if (((String) o).equals("success")) {
                                                                Toast.makeText(OrderDetailActivity.this, "发货", Toast.LENGTH_SHORT).show();
                                                                tvMsg.setText("你已发货，等待买家收货");
                                                                btnCamera.setVisibility(View.GONE);
                                                                btnGo.setVisibility(View.GONE);
                                                                btnGoback.setVisibility(View.GONE);
                                                                llAddress.setClickable(false);
                                                                closeLoadingDialog();
                                                            } else {
                                                                Toast.makeText(OrderDetailActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                                                                closeLoadingDialog();
                                                            }
                                                        }
                                            }
                                        });
                                    }
                            );

                            btnGoback.setOnClickListener(v -> {
                                RejectActivity.navigateForResult(OrderDetailActivity.this, order);
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
                            tvWant.setVisibility(View.VISIBLE);
                            tvSellerPrice.setVisibility(View.VISIBLE);
                            tvSellerTip.setVisibility(View.VISIBLE);
                            tvMsg.setText("你已拒绝别人的订单");
                            if (order.getRequest().getMinPrice() == null)
                                tvWant.setText("买家期望价格：" + order.getRequest().getMaxPrice() + "￥");
                            else
                                tvWant.setText("买家期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
                            tvSellerPrice.setText("你的出价:" + order.getPrice() + order.getPriceType());
                            tvSellerTip.setText("你索要的小费" + order.getTip() + order.getTipType());
                            break;

                        case Constant.SELLER_STATUS_REJECT:
                            tvWant.setVisibility(View.VISIBLE);
                            tvSellerPrice.setVisibility(View.VISIBLE);
                            tvSellerTip.setVisibility(View.VISIBLE);
                            tvMsg.setText("你的订单已被拒绝");
                            if (order.getRequest().getMinPrice() == null)
                                tvWant.setText("买家期望价格：" + order.getRequest().getMaxPrice() + "￥");
                            else
                                tvWant.setText("买家期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
                            tvSellerPrice.setText("你的出价:" + order.getPrice() + order.getPriceType());
                            tvSellerTip.setText("你索要的小费" + order.getTip() + order.getTipType());
                            break;

                        case Constant.BUYER_STATUS_DELIVERING:
                            tvWant.setVisibility(View.VISIBLE);
                            tvSellerPrice.setVisibility(View.VISIBLE);
                            tvSellerTip.setVisibility(View.VISIBLE);
                            llExpress.setVisibility(View.VISIBLE);
                            rlMoment.setVisibility(View.VISIBLE);
                            rlGet.setVisibility(View.VISIBLE);

                            tvMsg.setText("卖家已发货，请确认收货");
                            if (order.getRequest().getMinPrice() == null)
                                tvWant.setText("买家期望价格：" + order.getRequest().getMaxPrice() + "￥");
                            else
                                tvWant.setText("买家期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
                            tvSellerPrice.setText("你的出价:" + order.getPrice() + order.getPriceType());
                            tvSellerTip.setText("你索要的小费" + order.getTip() + order.getTipType());
                            tvInc.setText("快递公司：" + order.getExpressInc());
                            tvExpressNum.setText("快递单号：" + order.getExpressNum());
                            tvAddressMsg.setText("你的收货地址是：\n收货人：" + order.getAddress().getRecipient() + "\n手机号码：" + order.getAddress().getPhone() + "\n地址：" + order.getAddress().getRegion() + order.getAddress().getSpecific());

                            BmobQuery<OrderMoment> query2 = new BmobQuery<>();
                            query2.addWhereEqualTo("order", order);
                            showLoadingDialog();
                            query2.findObjects(new FindListener<OrderMoment>() {
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

                            btnGet.setOnClickListener(v -> {
                                showLoadingDialog();
                                AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                                JSONObject params = new JSONObject();
                                try {
                                    params.put("order", order.getObjectId());
                                    params.put("seller", order.getSeller().getObjectId());
                                    params.put("sum", getSum(order));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                ace.callEndpoint("takegoods", params, new CloudCodeListener() {
                                    @Override
                                    public void done(Object o, BmobException e) {
                                        if (o != null) {
                                            if (((String) o).equals("success")) {
                                                Toast.makeText(OrderDetailActivity.this, "已收货,付款成功", Toast.LENGTH_SHORT).show();

                                                User buyer = order.getBuyer();
                                                int level = (int) (buyer.getExp()+OrderDetailActivity.getSum(order));
                                                        if((int)(level/100)==0){
                                                            level=100;
                                                        }

                                                buyer.setExp(buyer.getExp()+level);
                                                buyer.update(new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {

                                                    }
                                                });

                                                User seller = order.getSeller();
                                                seller.setExp(seller.getExp()+level);
                                                seller.update(new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {

                                                    }
                                                });


                                                tvMsg.setText("交易已完成，请对卖家做出评价");
                                                rlGet.setVisibility(View.GONE);
                                                btnComment.setVisibility(View.VISIBLE);
                                                closeLoadingDialog();
                                            } else {
                                                Toast.makeText(OrderDetailActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                                                closeLoadingDialog();
                                            }
                                        }else{
                                            Toast.makeText(OrderDetailActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                                            closeLoadingDialog();
                                        }
                                    }
                                });
                            });

                            break;

                        case Constant.SELLER_STATUS_DELIVERING:
                            tvWant.setVisibility(View.VISIBLE);
                            tvSellerPrice.setVisibility(View.VISIBLE);
                            tvSellerTip.setVisibility(View.VISIBLE);
                            llExpress.setVisibility(View.VISIBLE);
                            rlMoment.setVisibility(View.VISIBLE);

                            tvMsg.setText("你已发货，等待买家确认收货");
                            if (order.getRequest().getMinPrice() == null)
                                tvWant.setText("买家期望价格：" + order.getRequest().getMaxPrice() + "￥");
                            else
                                tvWant.setText("买家期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
                            tvSellerPrice.setText("你的出价:" + order.getPrice() + order.getPriceType());
                            tvSellerTip.setText("你索要的小费" + order.getTip() + order.getTipType());
                            tvInc.setText("快递公司：" + order.getExpressInc());
                            tvExpressNum.setText("快递单号：" + order.getExpressNum());
                            tvAddressMsg.setText("买家地址是：收货人：" + order.getAddress().getRecipient() + "\n手机号码：" + order.getAddress().getPhone() + "\n地址：" + order.getAddress().getRegion() + order.getAddress().getSpecific());

                            BmobQuery<OrderMoment> query3 = new BmobQuery<>();
                            query3.addWhereEqualTo("order", order);
                            showLoadingDialog();
                            query3.findObjects(new FindListener<OrderMoment>() {
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

                        case Constant.BUYER_STATUS_FINISH:
                            tvWant.setVisibility(View.VISIBLE);
                            tvSellerPrice.setVisibility(View.VISIBLE);
                            tvSellerTip.setVisibility(View.VISIBLE);
                            llExpress.setVisibility(View.VISIBLE);
                            rlMoment.setVisibility(View.VISIBLE);
                            btnComment.setVisibility(View.VISIBLE);

                            tvMsg.setText("交易已完成");
                            if (order.getRequest().getMinPrice() == null)
                                tvWant.setText("你的期望价格：" + order.getRequest().getMaxPrice() + "￥");
                            else
                                tvWant.setText("你的期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
                            tvSellerPrice.setText("卖家出价:" + order.getPrice() + order.getPriceType());
                            tvSellerTip.setText("卖家索要的小费" + order.getTip() + order.getTipType());
                            tvInc.setText("快递公司：" + order.getExpressInc());
                            tvExpressNum.setText("快递单号：" + order.getExpressNum());
                            tvAddressMsg.setText("买家地址是：收货人：" + order.getAddress().getRecipient() + "\n手机号码：" + order.getAddress().getPhone() + "\n地址：" + order.getAddress().getRegion() + order.getAddress().getSpecific());

                            BmobQuery<OrderMoment> query4 = new BmobQuery<>();
                            query4.addWhereEqualTo("order", order);
                            showLoadingDialog();
                            query4.findObjects(new FindListener<OrderMoment>() {
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

                            btnComment.setOnClickListener(v -> {
                                EvaluateActivity.navigateForResult(OrderDetailActivity.this, order);
                            });
                            if (order.getEvaluate() != null) {
                                btnComment.setVisibility(View.GONE);
                                llEvaluate.setVisibility(View.VISIBLE);
                                ratingBar.setRating(order.getEvaluate().getScore());
                                if (order.getEvaluate().getContent() != null)
                                    buyerEvaluate.setText(order.getEvaluate().getContent());
                                if (order.getEvaluate().getUrlList() != null)
                                    picAdapter.setNewData(order.getEvaluate().getUrlList());
                            }
                            if (order.getEvaluate().getReply() != null) {
                                sellerEvaluate.setText("[买手回复]："+order.getEvaluate().getReply());
                                sellerEvaluate.setVisibility(View.VISIBLE);
                            }

                            break;

                        case Constant.SELLER_STATUS_FINISH:
                            tvWant.setVisibility(View.VISIBLE);
                            tvSellerPrice.setVisibility(View.VISIBLE);
                            tvSellerTip.setVisibility(View.VISIBLE);
                            llExpress.setVisibility(View.VISIBLE);
                            rlMoment.setVisibility(View.VISIBLE);
                            if (order.getEvaluate() != null) {
                                llEvaluate.setVisibility(View.VISIBLE);
                                ratingBar.setRating(order.getEvaluate().getScore());
                                if (order.getEvaluate().getContent() != null)
                                    buyerEvaluate.setText(order.getEvaluate().getContent());
                                if (order.getEvaluate().getUrlList() != null)
                                    picAdapter.setNewData(order.getEvaluate().getUrlList());
                            }
                            if (order.getEvaluate().getReply() != null) {
                                sellerEvaluate.setVisibility(View.VISIBLE);
                                sellerEvaluate.setText(order.getEvaluate().getReply());
                            } else {
                                btnComment.setVisibility(View.VISIBLE);
                            }

                            tvMsg.setText("交易已完成，请对买家做出评价");
                            if (order.getRequest().getMinPrice() == null)
                                tvWant.setText("买家期望价格：" + order.getRequest().getMaxPrice() + "￥");
                            else
                                tvWant.setText("买家期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
                            tvSellerPrice.setText("你的出价:" + order.getPrice() + order.getPriceType());
                            tvSellerTip.setText("你索要的小费:" + order.getTip() + order.getTipType());
                            tvInc.setText("快递公司：" + order.getExpressInc());
                            tvExpressNum.setText("快递单号：" + order.getExpressNum());
                            tvAddressMsg.setText("买家地址是：收货人：" + order.getAddress().getRecipient() + "\n手机号码：" + order.getAddress().getPhone() + "\n地址：" + order.getAddress().getRegion() + order.getAddress().getSpecific());

                            BmobQuery<OrderMoment> query5 = new BmobQuery<>();
                            query5.addWhereEqualTo("order", order);
                            showLoadingDialog();
                            query5.findObjects(new FindListener<OrderMoment>() {
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
                            btnComment.setOnClickListener(v -> {
                                ReplyActivity.navigateForResult(OrderDetailActivity.this, order.getEvaluate());
                            });

                            break;
                        case Constant.BUYER_STATUS_SELLER_REJECT:
                            tvMsg.setText("卖家已取消订单,拒绝理由是：" + order.getRejectReason());
                            tvWant.setVisibility(View.VISIBLE);
                            tvSellerPrice.setVisibility(View.VISIBLE);
                            tvSellerTip.setVisibility(View.VISIBLE);

                            if (order.getRequest().getMinPrice() == null)
                                tvWant.setText("你的期望价格：" + order.getRequest().getMaxPrice() + "￥");
                            else
                                tvWant.setText("你的期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
                            tvSellerPrice.setText("卖家出价:" + order.getPrice() + order.getPriceType());
                            tvSellerTip.setText("卖家索要的小费" + order.getTip() + order.getTipType());

                            break;

                        case Constant.SELLER_STATUS_SELLER_REJECT:
                            tvMsg.setText("你已取消了订单，并扣去相应的经验值,拒绝理由是：" + order.getRejectReason());
                            tvWant.setVisibility(View.VISIBLE);
                            tvSellerPrice.setVisibility(View.VISIBLE);
                            tvSellerTip.setVisibility(View.VISIBLE);
                            if (order.getRequest().getMinPrice() == null)
                                tvWant.setText("买家期望价格：" + order.getRequest().getMaxPrice() + "￥");
                            else
                                tvWant.setText("买家期望价格：" + order.getRequest().getMinPrice() + "~" + order.getRequest().getMaxPrice());
                            tvSellerPrice.setText("你的出价:" + order.getPrice() + order.getPriceType());
                            tvSellerTip.setText("你索要的小费" + order.getTip() + order.getTipType());
                            break;
                    }
                    closeLoadingDialog();
                } else {

                }
            }
        });

    }

    @Override
    protected void onResume() {
        BmobQuery<OrderMoment> query4 = new BmobQuery<>();
        query4.addWhereEqualTo("order", mOrder);
        showLoadingDialog();
        query4.findObjects(new FindListener<OrderMoment>() {
            @Override
            public void done(List<OrderMoment> list, BmobException e) {
                if (e == null) {
                    if(list!=null&&list.size()>0)
                    {
                        orderMomentAdapter.setNewData(list);
                    }
                    closeLoadingDialog();
                } else {
                    Toast.makeText(OrderDetailActivity.this, "网络有问题，请重试", Toast.LENGTH_LONG).show();
                    closeLoadingDialog();
                }
            }
        });
        super.onResume();
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
        if (resultCode == EvaluateActivity.EVALUATE_SUCCESS) {
            Evaluate evaluate = (Evaluate) data.getSerializableExtra("evaluate");
            if(evaluate.getUrlList()!=null&&evaluate.getUrlList().size()>0)
            picAdapter.setNewData(evaluate.getUrlList());
            btnComment.setVisibility(View.GONE);
            llEvaluate.setVisibility(View.VISIBLE);
            ratingBar.setRating(evaluate.getScore());
            buyerEvaluate.setText("买家评价是：" + evaluate.getContent());
        }
        if (resultCode == ReplyActivity.REPLY_SUCCESS) {
            Evaluate evaluate = (Evaluate) data.getSerializableExtra("evaluate");
            btnComment.setVisibility(View.GONE);
            sellerEvaluate.setVisibility(View.VISIBLE);
            sellerEvaluate.setText("卖家追评：" + evaluate.getReply());
        }
        if (resultCode == RejectActivity.REJECT_SUCCESS) {
            Order order = (Order) data.getSerializableExtra("order");
            tvMsg.setText("你已拒绝了订单，拒绝理由是：" + order.getRejectReason());
            btnGoback.setVisibility(View.GONE);
            btnGo.setVisibility(View.GONE);
            btnCamera.setVisibility(View.GONE);
            llAddress.setClickable(false);
            llExpress.setVisibility(View.GONE);
            rlMoment.setVisibility(View.GONE);
            btnCancle.setVisibility(View.GONE);
            User user = order.getSeller();
            user.setExp(user.getExp()-300);
            user.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {

                }
            });
        }

        if (resultCode == PayActivity.PAY_SUCCESS) {
            Order order = (Order) data.getSerializableExtra("order");
            tvMsg.setText("你已接收订单，等待卖家发货");
            rlCreateBtn.setVisibility(View.GONE);
            llAddress.setVisibility(View.GONE);
            rlMoment.setVisibility(View.VISIBLE);
            tvAddressMsg.setText("买家地址是：收货人：" + order.getAddress().getRecipient() + "\n手机号码：" + order.getAddress().getPhone() + "\n地址：" + order.getAddress().getRegion() + order.getAddress().getSpecific());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public static float getSum(Order order) {
        float sum = 0f;
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
        return sum;
    }


}




