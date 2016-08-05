package edu.scau.buymesth.home.view;

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
        initAdapter();
        return view;
    }
    private void initAdapter(){
        mQuickAdapter = new QuickAdapter(10);
        mQuickAdapter.openLoadAnimation();
        mRecyclerView.setAdapter(mQuickAdapter);
        mQuickAdapter.setOnRecyclerViewItemClickListener((view, position) -> Toast.makeText(getActivity(), "" + Integer.toString(position), Toast.LENGTH_LONG).show());
    }
}
