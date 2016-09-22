package edu.scau.buymesth.user.moment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.scau.buymesth.R;

/**
 * Created by John on 2016/9/21.
 */

public class MomentFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moment, container, false);

        //   mPresenter = new UserPresenter();
        return view;
    }
}
