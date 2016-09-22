package edu.scau.buymesth.user.moment;

import android.graphics.Rect;
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

import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.DiscoverAdapter;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.discover.detail.MomentDetailActivity;

/**
 * Created by John on 2016/9/21.
 */

public class MomentFragment extends Fragment implements MomentContract.View{
    private RecyclerView mRecyclerView;
    private DiscoverAdapter mDiscoverAdapter;
    private MomentPresenter mPresenter;
    private View notLoadingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moment,container,false);
        if(savedInstanceState==null){
        mRecyclerView= (RecyclerView) view.findViewById(R.id.rv_discover_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new  SpaceItemDecoration(2));
//        mRecyclerView.addItemDecoration( new DividerItemDecoration(getContext(),
//                DividerItemDecoration.VERTICAL_LIST));
        //初始化代理人
        mPresenter=new MomentPresenter(getActivity().getBaseContext());
        mPresenter.setVM(this,new MomentModel());
        initAdapter();
        mPresenter.Refresh();}
        return view;

    }


    private void initAdapter(){
        mDiscoverAdapter = new DiscoverAdapter(getActivity(),mPresenter.mModel.getDatas());
        mDiscoverAdapter.setOnRecyclerViewItemClickListener((view, position) -> {

            MomentDetailActivity.navigate(getActivity(),(Moment)mDiscoverAdapter.getItem(position));
        });
        mDiscoverAdapter.openLoadAnimation();
        mRecyclerView.setAdapter(mDiscoverAdapter);
        mDiscoverAdapter.setOnLoadMoreListener(() -> mPresenter.LoadMore());
        mDiscoverAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
    }

    @Override
    public void onLoadMoreSuccess(List<Moment> list) {
        if(list.size()>0)
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),throwable==null?msg:throwable.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefreshComplete(List<Moment> list) {
        mDiscoverAdapter.notifyDataSetChanged();
        mDiscoverAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
        mDiscoverAdapter.removeAllFooterView();

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

        mDiscoverAdapter.notifyDataSetChanged();
    }

    private final class SpaceItemDecoration extends RecyclerView.ItemDecoration{

        private int space;

        SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = space;
        }
    }
}
