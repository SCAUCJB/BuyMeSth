package edu.scau.buymesth.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.scau.buymesth.R;
import edu.scau.buymesth.user.moment.MomentFragment;
import edu.scau.buymesth.user.request.RequestFragment;
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
  //      MomentFragment momentFragment=new MomentFragment();
        RequestFragment requestFragment=new RequestFragment();

        adapter.addTab(requestFragment,"我的请求");
   //     adapter.addTab(momentFragment,"我的动态");

        mContentVp.setAdapter(adapter);
        mContentTb.setupWithViewPager(mContentVp);
        view.findViewById(R.id.btn_setting).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserSettingActivity.class);
            getActivity().startActivity(intent);
        });


        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl);

        NestedScrollView bottomSheet = (NestedScrollView) coordinatorLayout.findViewById(R.id.bottom_sheet);

    //     momentFragment.disallowIntercept(bottomSheet);
         requestFragment.disallowIntercept(bottomSheet);
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
