package edu.scau.buymesth.home.view;

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
import android.widget.Toast;

import adpater.BaseQuickAdapter;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.QuickAdapter;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * Created by Jammy on 2016/8/1.
 */
public class HomeFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private QuickAdapter mQuickAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        mRecyclerView= (RecyclerView) view.findViewById(R.id.rv_home_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_10)));
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
        frame.postDelayed(() -> frame.autoRefresh(false), 2100);
        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return  !mRecyclerView.canScrollVertically(-1);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(() -> frame.refreshComplete(), 2000);
            }
        });
    }
    private void initAdapter(){
        mQuickAdapter = new QuickAdapter(10);
        mQuickAdapter.openLoadAnimation();
        mRecyclerView.setAdapter(mQuickAdapter);
        mQuickAdapter.setOnRecyclerViewItemClickListener((view, position) -> Toast.makeText(getActivity(), "" + Integer.toString(position), Toast.LENGTH_LONG).show());
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            if(parent.getChildPosition(view) != 0)
                outRect.top = space;
        }
    }
}
