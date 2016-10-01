package edu.scau.buymesth.request;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

import adpater.animation.ScaleInAnimation;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.RequestListAdapter;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.request.requestdetail.RequestDetailActivity;
import edu.scau.buymesth.util.NetworkHelper;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * Created by Jammy on 2016/8/1.
 */
public class HomeFragment extends Fragment implements HomeContract.View {
    private RecyclerView mRecyclerView;
    private RequestListAdapter mRequestListAdapter;
    private HomePresenter mPresenter;
    private PtrFrameLayout mPtrFrameLayout;

    private FloatingActionMenu relatedFab;

    private View notLoadingView;
    private String filter;
    private Object filterKey;
    private PtrHandler ptrHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
Log.d("zhx","onCreateView");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_home_fragment);
//        fbAdd = (FloatingActionButton) view.findViewById(R.id.fb_add);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_10)));
        //初始化代理人
        mPresenter = new HomePresenter();
        mPresenter.setVM(this, new HomeModel());

        if (relatedFab != null)
            mRecyclerView.addOnScrollListener(new HidingScrollListener() {
                @Override
                public void onHide() {
                    relatedFab.hideMenu(true);
                }

                @Override
                public void onShow() {
                    relatedFab.showMenu(true);
                }
            });

        initAdapter();
        initStoreHouse(view, savedInstanceState);
        return view;
    }
      StoreHouseHeader mHeader;
    private void initStoreHouse(View view, Bundle savedInstanceState) {
        mPtrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.store_house_ptr_frame);
        mHeader = new StoreHouseHeader(getActivity());
        mHeader.setPadding(0, 80, 0, 50);
        mHeader.initWithString("Buy Me Sth");
        mHeader.setTextColor(Color.BLACK);
        mPtrFrameLayout.setHeaderView(mHeader);
        mPtrFrameLayout.addPtrUIHandler(mHeader);
     //   if(savedInstanceState==null)//屏幕旋转以后就不用再自动刷新了
     //   mPtrFrameLayout.post(() -> mPtrFrameLayout.autoRefresh(false));
        ptrHandler = new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return !mRecyclerView.canScrollVertically(-1) && mPresenter != null;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                //屏幕旋转的话presenter被销毁，上面的自动刷新会调用到这里，会引起空指针
                if (mPresenter == null) {
                    Log.d("zhx","mPresenter == null");
                    return;
                }
                mPtrFrameLayout = frame;
                Log.d("zhx","onRefreshBegin");
                mPresenter.onRefresh(filter, filterKey);
            }
        };
        mPtrFrameLayout.setPtrHandler(ptrHandler);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("zhx","onPause");
        mPresenter.onPause();
        mPtrFrameLayout.removePtrUIHandler(mHeader);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("zhx","onResume");
        mPtrFrameLayout.addPtrUIHandler(mHeader);
        mPresenter.onResume();
    }

    private void initAdapter() {
        mRequestListAdapter = new RequestListAdapter();
        mRequestListAdapter.openLoadAnimation(new ScaleInAnimation());
        if (getActivity().getIntent().getBooleanExtra("selectRequest", false)) {
            mPresenter.onRefresh(filter, filterKey);
            mRequestListAdapter.setOnRecyclerViewItemClickListener(
                    (view, position) -> {
                        Intent i = new Intent();
                        i.putExtra("requestId", mRequestListAdapter.getData().get(position).getObjectId());
                        getActivity().setResult(Activity.RESULT_OK, i);
                        getActivity().finish();
                    });
        } else {
            mPresenter.initAdapter();
            mRequestListAdapter.setOnRecyclerViewItemClickListener(
                    (view, position) -> RequestDetailActivity.navigate(getActivity(), mRequestListAdapter.getData().get(position))
            );
        }
        mRecyclerView.setAdapter(mRequestListAdapter);
        mRequestListAdapter.setOnLoadMoreListener(() -> mPresenter.onLoadMore(filter, filterKey));
        mRequestListAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
    }

    /**
     * 通知局部刷新
     *
     * @param list
     */
    @Override
    public void onLoadMoreSuccess(List<Request> list) {
        if (list != null)
            mRequestListAdapter.notifyDataChangedAfterLoadMore(list, true);
        else {
            mRequestListAdapter.notifyDataChangedAfterLoadMore(false);
            if (notLoadingView == null) {
                notLoadingView = getActivity().getWindow().getLayoutInflater().inflate(R.layout.not_loading, (ViewGroup) mRecyclerView.getParent(), false);
            }
            mRequestListAdapter.addFooterView(notLoadingView);
        }
    }

    /**
     * 这个到时候抽取到base view里面，base fragment也要实现
     *
     * @param msg
     */
    @Override
    public void showError(String msg) {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 这里的下拉刷新处理得很丑陋，把之前加载更多的数据都去掉了，很伤,想办法利用缓存才行
     *
     * @param list
     */
    @Override
    public void onRefreshComplete(List<Request> list) {
        mRequestListAdapter.setNewData(list);
        mRequestListAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
        mRequestListAdapter.removeAllFooterView();
        if (mPtrFrameLayout != null)
            mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void onRefreshFail() {
        if (mPtrFrameLayout != null)
            mPtrFrameLayout.refreshComplete();
    }

    /**
     * 第一次进入的时候从缓存拿数据，然后设置
     *
     * @param list
     */
    @Override
    public void setAdapter(List<Request> list) {
        mRequestListAdapter.setNewData(list);
    }

    @Override
    public boolean hasNetwork() {
        return   (NetworkHelper.isOpenNetwork(getContext()));
    }

    public void setFilter(String filter, Object filterKey) {
        this.filter = filter;
        this.filterKey = filterKey;
        mPresenter.onRefresh(filter, filterKey);
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return this.filter;
    }

    public void setFilterKey(Object filterKey) {
        this.filterKey = filterKey;
    }

    public FloatingActionMenu getRelatedFab() {
        return relatedFab;
    }

    public void setRelatedFab(FloatingActionMenu relatedFab) {
        this.relatedFab = relatedFab;
    }

    private final class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = space;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        mPresenter = null;
        Log.d("zhx","onDestroy");
    }
}
