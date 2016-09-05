package edu.scau.buymesth.discover.list;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adpater.BaseQuickAdapter;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.DiscoverAdapter;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.discover.detail.MomentDetailActivity;
import edu.scau.buymesth.discover.publish.MomentPublishActivity;
import edu.scau.buymesth.homedetail.RequestDetailActivity;
import gallery.PhotoDialogFragment;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import ui.layout.NineGridLayout;

/**
 * Created by Jammy on 2016/8/1.
 */
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
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_10)));
        //初始化代理人
        mPresenter=new DiscoverPresenter(getContext());
        mPresenter.setVM(this,new DiscoverModel());
        initAdapter();
        initStoreHouse(view);

        return view;
    }


    private void initStoreHouse(View view) {
        final PtrFrameLayout frame = (PtrFrameLayout) view.findViewById(R.id.store_house_ptr_frame);
        final StoreHouseHeader header = new StoreHouseHeader(getActivity());
        header.setPadding(0, 80, 0,50);
        header.initWithString("Buy Me Sth");
        header.setTextColor(Color.BLACK);
        frame.setDurationToCloseHeader(1500);
        frame.setHeaderView(header);
        frame.addPtrUIHandler(header);
//        frame.postDelayed(() -> frame.autoRefresh(false), 0);
        frame.postDelayed(()-> mPresenter.Refresh(),0);
        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return  !mRecyclerView.canScrollVertically(-1);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                mPtrFrameLayout=frame;
                mPresenter.Refresh();
            }
        });
    }
    private void initAdapter(){
        mDiscoverAdapter = new DiscoverAdapter(mPresenter.mModel.getDatas());
        mDiscoverAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(getActivity(), MomentDetailActivity.class);
                i.putExtra(Moment.class.getName(),(Moment)mDiscoverAdapter.getItem(position));
                startActivity(i);
            }
        });
        mDiscoverAdapter.openLoadAnimation();
        mRecyclerView.setAdapter(mDiscoverAdapter);
//        mDiscoverAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
//            Intent intent =new Intent(getActivity(), HomeDetailActivity.class);
//            startActivity(intent);
//        });
//        mDiscoverAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//
//            }
//        });
        mDiscoverAdapter.setOnItemsContentClickListener(new DiscoverAdapter.OnItemsContentClickListener() {
            @Override
            public void onItemsContentClick(View v, Object item, int position) {
                switch (v.getId()){
                    case R.id.ly_delete:
                        mPresenter.DeleteOne((Moment) item,position);
                        break;
                    case R.id.ly_likes:
                        mPresenter.AddLike(v, (Moment) item, position);
                        break;
                    case R.id.ly_comments:
                        break;
                    case R.id.request_view:
                        RequestDetailActivity.navigate(getActivity(),((Moment)item).getRequest());
                        break;
                    case R.id.nine_grid_layout:
                        Toast.makeText(getContext(),"click on image position : "+position,Toast.LENGTH_SHORT).show();
                        List<View> views = new ArrayList<View>();
                        for(int i = ((NineGridLayout)v).getChildCount()-1;i>=0;i--){
                            views.add(0,((NineGridLayout)v).getChildAt(i));
                        }
                        PhotoDialogFragment.navigate(getActivity(),views,((List<String>)item),position);
                        break;
                    default:
                        break;
                }
            }
        });
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

    @Override
    public void onDeleteSuccess(String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),"Delete succeed",Toast.LENGTH_SHORT).show();
            }
        });
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
