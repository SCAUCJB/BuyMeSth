package edu.scau.buymesth.user.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.notice.OrderDetailActivity;
import edu.scau.buymesth.notice.detail.BuyerCreateActivity;
import edu.scau.buymesth.notice.detail.BuyerAcceptActivity;
import edu.scau.buymesth.notice.detail.BuyerDeliverActivity;
import edu.scau.buymesth.notice.detail.BuyerFinishActivity;
import edu.scau.buymesth.notice.detail.BuyerRejectActivity;
import edu.scau.buymesth.notice.detail.SellerAcceptActivity;
import edu.scau.buymesth.notice.detail.SellerDeliverActivity;
import edu.scau.buymesth.notice.detail.SellerFinishActivity;
import edu.scau.buymesth.notice.detail.SellerCreateActivity;
import edu.scau.buymesth.notice.detail.SellerRejectActivity;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.util.DividerItemDecoration;

/**
 * Created by John on 2016/9/25.
 */

public class OrderFragment extends Fragment{

    OrderListAdapter adapter;
    RecyclerView mRecyclerView;
    TextView mHintTv;
    private String mId;
    User user;

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
        BmobQuery<Order> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("seller",user);
        BmobQuery<Order> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("buyer",user);
        List<BmobQuery<Order>> queries = new ArrayList<BmobQuery<Order>>();
        queries.add(query1);
        queries.add(query2);
        BmobQuery<Order> query = new BmobQuery<Order>();
        query.or(queries);
        query.include("buyer,request,seller");
        query.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if(e==null){
                    adapter.setNewData(list);
                    mHintTv.setVisibility(View.GONE);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });

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


}
