package edu.scau.buymesth.user.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import adpater.animation.SlideInBottomAnimation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.notice.OrderDetailActivity;
import edu.scau.buymesth.util.DividerItemDecoration;
import edu.scau.buymesth.util.NetworkHelper;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static cn.bmob.v3.BmobQuery.CachePolicy.CACHE_ELSE_NETWORK;
import static cn.bmob.v3.BmobQuery.CachePolicy.CACHE_ONLY;
import static cn.bmob.v3.BmobQuery.CachePolicy.NETWORK_ELSE_CACHE;
import static cn.bmob.v3.BmobQuery.CachePolicy.NETWORK_ONLY;

/**
 * Created by John on 2016/9/25.
 */

public class OrderFragment extends Fragment{
    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    OrderListAdapter adapter;
    RecyclerView mRecyclerView;
    TextView mHintTv;
    private String mId;
    User user;
    private View notLoadingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);
        mHintTv = (TextView) view.findViewById(R.id.tv_hint);
        user=(User) getActivity().getIntent().getSerializableExtra("user");
        mId= user!=null?user.getObjectId(): BmobUser.getCurrentUser().getObjectId();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(mParent==null) return;
                if(mRecyclerView.canScrollVertically(-1))
                {
                    mParent.setNestedScrollingEnabled(false);
                }
                else
                    mParent.setNestedScrollingEnabled(true);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));

        initAdapter();

        return view;
    }

    private void initAdapter() {
        adapter = new OrderListAdapter(new ArrayList<>());
        adapter.openLoadAnimation(new SlideInBottomAnimation());
        BmobQuery<Order> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("seller",mId);
        BmobQuery<Order> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("buyer",mId);
        List<BmobQuery<Order>> queries = new ArrayList<BmobQuery<Order>>();
        queries.add(query1);
        queries.add(query2);
        BmobQuery<Order> query = new BmobQuery<>();
        query.or(queries);
        query.include("buyer,request,seller");
        query.order("-updatedAt");

        Subscription subscription = getOrderObservable(CACHE_ELSE_NETWORK, mId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Order>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(List<Order> orders) {

                        if (orders == null || orders.size() == 0) {
                            mHintTv.setVisibility(View.VISIBLE);
                        }else if(orders.size()>0&&mHintTv.getVisibility()==View.VISIBLE)
                            mHintTv.setVisibility(View.GONE);
                        adapter.setNewData(orders);
                    }
                });
        mSubscriptions.add(subscription);
        adapter.setOnLoadMoreListener(() -> onLoadMore(mId));
        adapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener((view, position) -> {
            Order order = adapter.getItem(position);
            Intent intent = new Intent(getContext(), OrderDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("order",order);
            intent.putExtra("order",order);
            startActivity(intent);
        });
    }


    NestedScrollView mParent;
    public void disallowIntercept(NestedScrollView parent){
        mParent=parent;
    }

    private int pageNum = 0;

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
    @Override
    public void onPause() {
        super.onPause();
        mSubscriptions.clear();
    }
    public void refresh() {
        pageNum=0;
        Subscription subscription = getOrderObservable(NETWORK_ONLY, mId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Order>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showMsg("获取订单列表出现了问题");
                    }

                    @Override
                    public void onNext(List<Order> orders) {

                        if (orders == null || orders.size() == 0) {
                            mHintTv.setVisibility(View.VISIBLE);
                        }else if(orders.size()>0&&mHintTv.getVisibility()==View.VISIBLE)
                            mHintTv.setVisibility(View.GONE);
                        adapter.setNewData(orders);
                    }
                });
        mSubscriptions.add(subscription);
    }

    public Observable<List<Order>> getOrderObservable(BmobQuery.CachePolicy policy, String userId) {
        BmobQuery<Order> query = new BmobQuery<>();
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));//此表示缓存一天，可以用来优化下拉刷新而清空了的加载更多
        BmobQuery<Order> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("seller",mId);
        BmobQuery<Order> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("buyer",mId);
        List<BmobQuery<Order>> queries = new ArrayList<BmobQuery<Order>>();
        queries.add(query1);
        queries.add(query2);
        query.or(queries);
        query.include("buyer,request,seller");
        query.order("-updatedAt");
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        if (policy == CACHE_ONLY && query.hasCachedResult(Order.class))
            query.setCachePolicy(CACHE_ONLY);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        else
            query.setCachePolicy(NETWORK_ELSE_CACHE);// 从网络

        return query.findObjectsObservable(Order.class);
    }

    void onLoadMore(String userId) {
        BmobQuery.CachePolicy policy = NetworkHelper.isOpenNetwork(getContext()) ? BmobQuery.CachePolicy.NETWORK_ONLY : BmobQuery.CachePolicy.CACHE_ONLY;
        mSubscriptions.add(getOrderObservable(policy, userId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Order>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showMsg("获取订单列表出现了问题");
                        adapter.notifyDataChangedAfterLoadMore(false);
                    }

                    @Override
                    public void onNext(List<Order> orders) {
                        if(orders==null)
                        adapter.notifyDataChangedAfterLoadMore(orders, true);
                        else{
                            adapter.notifyDataChangedAfterLoadMore(false);
                            if (notLoadingView == null) {
                                notLoadingView = getActivity().getWindow().getLayoutInflater().inflate(R.layout.not_loading, (ViewGroup) mRecyclerView.getParent(), false);
                            }
                            adapter.addFooterView(notLoadingView);
                        }
                    }
                }));
    }

    public void showMsg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


}
