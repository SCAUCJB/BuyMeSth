package edu.scau.buymesth.home.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.scau.buymesth.R;

/**
 * Created by Jammy on 2016/8/1.
 */
public class HomeFragment extends Fragment{
    private RecyclerView mRecyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        mRecyclerView= (RecyclerView) view.findViewById(R.id.rv_home_fragment);

        return view;
    }
}
