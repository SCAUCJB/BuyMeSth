package edu.scau.buymesth.conversation.userlist;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import base.util.SpaceItemDecoration;
import base.util.ToastUtil;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.userinfo.UserInfoActivity;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * Created by ！ on 2016/9/18.
 */
public class UserListFragment extends Fragment implements UserListContract.View{
    private RecyclerView mRecyclerView;
    private UserListAdapter mUserListAdapter;
    private UserListPresenter mPresenter;
    private PtrFrameLayout mPtrFrameLayout;
    private View notLoadingView;
    private View mHeaderSearchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list,container,false);
        mRecyclerView= (RecyclerView) view.findViewById(R.id.rv_discover_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(2));
        //初始化代理人
        mPresenter=new UserListPresenter(getActivity().getBaseContext());
        mPresenter.setVM(this,new UserListModel());
        initAdapter();
        initStoreHouse(view);

        return view;
    }

    private void initAdapter(){
        mUserListAdapter = new UserListAdapter(getActivity(),mPresenter.mModel.getDatas());
        mUserListAdapter.openLoadAnimation();
        mUserListAdapter.setOnRecyclerViewItemClickListener((view, position) -> UserInfoActivity.navigate(getActivity(),mUserListAdapter.getItem(position)));
        mRecyclerView.setAdapter(mUserListAdapter);
        mHeaderSearchView = LayoutInflater.from(getContext()).inflate(R.layout.item_search_bar,(ViewGroup) mRecyclerView.getParent(), false);
        mHeaderSearchView.findViewById(R.id.bt_search).setOnClickListener(v -> {
            mPresenter.setSearchKey(((EditText)mHeaderSearchView.findViewById(R.id.et_user_info)).getText().toString());
            mPtrFrameLayout.post(() -> mPtrFrameLayout.autoRefresh());
        });
        mUserListAdapter.addHeaderView(mHeaderSearchView);
        mUserListAdapter.setOnLoadMoreListener(() -> mPresenter.loadMore());
        mUserListAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
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
        mPtrFrameLayout.post(() -> mPresenter.refresh());
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return  !mRecyclerView.canScrollVertically(-1);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                mPresenter.refresh();
            }
        });
    }

    @Override
    public void onLoadMoreSuccess(List<User> list) {
        if(list!=null&&list.size()>0)
            mUserListAdapter.notifyDataChangedAfterLoadMore(true);
        else{
            mUserListAdapter.notifyDataChangedAfterLoadMore(false);
            if (notLoadingView == null) {
                notLoadingView = getActivity().getWindow().getLayoutInflater().inflate(R.layout.not_loading, (ViewGroup) mRecyclerView.getParent(), false);
            }
            mUserListAdapter.addFooterView(notLoadingView);
        }
    }

    @Override
    public void onError(Throwable throwable, String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void onRefreshComplete(List<User> list) {
        mUserListAdapter.notifyDataSetChanged();
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void onRefreshInterrupt() {
        mPtrFrameLayout.refreshComplete();
    }
}
