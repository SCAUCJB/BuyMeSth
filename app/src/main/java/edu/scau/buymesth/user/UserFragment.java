package edu.scau.buymesth.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.scau.buymesth.R;
import edu.scau.buymesth.user.moment.MomentFragment;
import edu.scau.buymesth.user.request.RequsetFragment;
import edu.scau.buymesth.user.setting.UserSettingActivity;

/**
 * Created by Jammy on 2016/8/1.
 */
public class UserFragment extends Fragment implements UserContract.View {
    ViewPager mContentVp;
    TabLayout mContentTb;
    UserPresenter mPresenter;
    private boolean mHasExpand=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        mContentTb = (TabLayout) view.findViewById(R.id.tl_content);
        mContentVp = (ViewPager) view.findViewById(R.id.vp_content);
        HomePagerAdapter adapter=new HomePagerAdapter(getFragmentManager());
        adapter.addTab(new RequsetFragment(),"我的请求");
        adapter.addTab(new MomentFragment(),"我的动态");

        mContentVp.setAdapter(adapter);
        mContentTb.setupWithViewPager(mContentVp);
        view.findViewById(R.id.btn_setting).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserSettingActivity.class);
            getActivity().startActivity(intent);
        });


        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl);
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //这里是拖拽中的回调，根据slideOffset可以做一些动画
                Log.d("zhx","onSlide  ");
                Log.d("zhx","slideOffset="+slideOffset);
            }
        });
        //   mPresenter = new UserPresenter();
        return view;
    }
      class HomePagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> titles = new ArrayList<>();

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addTab(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
