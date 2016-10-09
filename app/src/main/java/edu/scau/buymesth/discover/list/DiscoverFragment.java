package edu.scau.buymesth.discover.list;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import base.util.SpaceItemDecoration;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.DiscoverAdapter;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.discover.detail.MomentDetailActivity;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;


public class DiscoverFragment extends Fragment implements DiscoverContract.View{
    private RecyclerView mRecyclerView;
    private DiscoverAdapter mDiscoverAdapter;
    private DiscoverPresenter mPresenter;
    private PtrFrameLayout mPtrFrameLayout;
    private View notLoadingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover,container,false);
        mRecyclerView= (RecyclerView) view.findViewById(R.id.rv_discover_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(2));
//        mRecyclerView.addItemDecoration( new DividerItemDecoration(getContext(),
//                DividerItemDecoration.VERTICAL_LIST));
        //初始化代理人
        mPresenter=new DiscoverPresenter(getActivity().getBaseContext());
        mPresenter.setVM(this,new DiscoverModel());
        initAdapter();
        initStoreHouse(view);

        return view;
    }

    @Override
    public void onPause() {
        mPresenter.onPause();
        super.onPause();
    }

    private void initStoreHouse(View view) {
        mPtrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.store_house_ptr_frame);
        final StoreHouseHeader header = new StoreHouseHeader(getActivity());
        header.setPadding(0, 80, 0,50);
        header.initWithString("Buy Me Sth");
        header.setTextColor(Color.BLACK);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.post(() -> mPresenter.Refresh());
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return  !mRecyclerView.canScrollVertically(-1);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                mPresenter.Refresh();
            }
        });
    }
    private void initAdapter(){
        mDiscoverAdapter = new DiscoverAdapter(getActivity(),mPresenter.mModel.getDatas());
        mDiscoverAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            if(mPtrFrameLayout.isRefreshing())return;
            MomentDetailActivity.navigate(getActivity(),(Moment)mDiscoverAdapter.getItem(position));
        });
        mDiscoverAdapter.openLoadAnimation();
        mRecyclerView.setAdapter(mDiscoverAdapter);
        mDiscoverAdapter.setOnLoadMoreListener(() -> mPresenter.LoadMore());
        mDiscoverAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
    }

    @Override
    public void onLoadMoreSuccess(List<Moment> list) {
        if(list!=null&&list.size()>0)
            mDiscoverAdapter.notifyDataChangedAfterLoadMore(true);
        else{
            mDiscoverAdapter.notifyDataChangedAfterLoadMore(false);
            if (notLoadingView == null) {
                notLoadingView = getActivity().getWindow().getLayoutInflater().inflate(R.layout.not_loading, (ViewGroup) mRecyclerView.getParent(), false);
            }
            mDiscoverAdapter.addFooterView(notLoadingView);
        }
    }

    @Override
    public void onError(Throwable throwable, String msg) {
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(),throwable==null?msg:throwable.toString(),Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRefreshComplete(List<Moment> list) {
        mDiscoverAdapter.notifyDataSetChanged();
        mDiscoverAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
        mDiscoverAdapter.removeAllFooterView();
        if(mPtrFrameLayout!=null)
            mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void onInitializeLocalDataComplete() {

    }

    @Override
    public void onAddOneItem(Moment moment) {
        mDiscoverAdapter.notifyItemInserted(0);
    }

    @Override
    public void onItemChanged(){
        mDiscoverAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshInterrupt() {
        if(mPtrFrameLayout!=null)
            mPtrFrameLayout.refreshComplete();
        mDiscoverAdapter.notifyDataSetChanged();
    }
}
