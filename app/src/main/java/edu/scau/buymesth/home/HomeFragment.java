package edu.scau.buymesth.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import java.util.List;

import adpater.animation.ScaleInAnimation;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.HomeAdapter;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.homedetail.RequestDetailActivity;
import edu.scau.buymesth.publish.PublishActivity;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * Created by Jammy on 2016/8/1.
 */
public class HomeFragment extends Fragment implements HomeContract.View {
    private RecyclerView mRecyclerView;
    private HomeAdapter mHomeAdapter;
    private HomePresenter mPresenter;
    private PtrFrameLayout mPtrFrameLayout;
    private FloatingActionButton fbAdd;

    private View notLoadingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_home_fragment);
        fbAdd = (FloatingActionButton) view.findViewById(R.id.fb_add);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_10)));
        //初始化代理人
        mPresenter = new HomePresenter();
        mPresenter.setVM(this, new HomeModel());

        mRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                fbAdd.animate().translationY(fbAdd.getHeight() + fbAdd.getBottom()).setInterpolator(new AccelerateInterpolator(2)).start();
            }

            @Override
            public void onShow() {
                fbAdd.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        });

        fbAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PublishActivity.class);
            startActivity(intent);
        });


        initAdapter();
        initStoreHouse(view);
        return view;
    }

    private void initStoreHouse(View view) {
        final PtrFrameLayout frame = (PtrFrameLayout) view.findViewById(R.id.store_house_ptr_frame);
        final StoreHouseHeader header = new StoreHouseHeader(getActivity());
        header.setPadding(0, 80, 0, 50);
        header.initWithString("Buy Me Sth");
        header.setTextColor(Color.BLACK);
        frame.setDurationToCloseHeader(1500);
        frame.setHeaderView(header);
        frame.addPtrUIHandler(header);
        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return !mRecyclerView.canScrollVertically(-1);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                mPtrFrameLayout = frame;
                mPresenter.onRefresh();
            }
        });
    }

    private void initAdapter() {
        mPresenter.initAdapter();
        mHomeAdapter = new HomeAdapter();
        mHomeAdapter.openLoadAnimation(new ScaleInAnimation());
        mRecyclerView.setAdapter(mHomeAdapter);
        mHomeAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            RequestDetailActivity.navigate((AppCompatActivity)getActivity(),mHomeAdapter.getData().get(position));
        });
        mHomeAdapter.setOnLoadMoreListener(() -> mPresenter.onLoadMore());
        mHomeAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
    }

    /**
     * 通知局部刷新
     *
     * @param list
     */
    @Override
    public void onLoadMoreSuccess(List<Request> list) {
        if (list != null)
            mHomeAdapter.notifyDataChangedAfterLoadMore(list, true);
        else {
            mHomeAdapter.notifyDataChangedAfterLoadMore(false);
            if (notLoadingView == null) {
                notLoadingView = getActivity().getWindow().getLayoutInflater().inflate(R.layout.not_loading, (ViewGroup) mRecyclerView.getParent(), false);
            }
            mHomeAdapter.addFooterView(notLoadingView);
        }
    }

    /**
     * 这个到时候抽取到base view里面，base fragment也要实现
     *
     * @param msg
     */
    @Override
    public void showError(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 这里的下拉刷新处理得很丑陋，把之前加载更多的数据都去掉了，很伤,想办法利用缓存才行
     *
     * @param list
     */
    @Override
    public void onRefreshComplete(List<Request> list) {
        mHomeAdapter.setNewData(list);
        mHomeAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
        mHomeAdapter.removeAllFooterView();
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
        mHomeAdapter.setNewData(list);
    }

    private final class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//TODO  @止园哥 这里建议换成 getChildAdapterPosition(View) 或者 getChildLayoutPosition(View)
            if (parent.getChildPosition(view) != 0)
                outRect.top = space;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        mPresenter = null;
    }


}
