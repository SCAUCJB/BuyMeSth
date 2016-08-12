package edu.scau.buymesth.main;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.TabAdapter;
import edu.scau.buymesth.discover.view.DiscoverFragment;
import edu.scau.buymesth.home.HomeFragment;
import edu.scau.buymesth.user.view.UserFragment;
import ui.widget.ChangeColorIconWithTextView;

/**
 * Created by Jammy on 2016/8/1.
 * Updated by John on 2016/8/9
 */
public class TabActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    List<Fragment> fragmentList = new ArrayList<Fragment>();
    TabAdapter tabAdapter;

    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_tab;
    }

    @Override
    public void initView() {
        tabLayout.setSelectedTabIndicatorHeight(0);
        UserFragment userFragment = new UserFragment();
        DiscoverFragment discoverFragment = new DiscoverFragment();
        HomeFragment homeFragment = new HomeFragment();
        fragmentList.add(homeFragment);
        fragmentList.add(discoverFragment);
        fragmentList.add(userFragment);

        for (int i = 0; i < fragmentList.size(); i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View view = this.getLayoutInflater().inflate(R.layout.tab, null);
            view.setTag(i);
            ChangeColorIconWithTextView cv = (ChangeColorIconWithTextView) view.findViewById(R.id.cv);
            switch (i) {
                case 0:
                    cv.setIcon(R.drawable.ic_home);
                    cv.setIconColor(getResources().getColor(R.color.colorAccent));
                    break;
                case 1:
                    cv.setIcon(R.drawable.ic_whatshot);
                    cv.setIconColor(getResources().getColor(R.color.colorAccent));
                    break;
                case 2:
                    cv.setIcon(R.drawable.ic_person);
                    cv.setIconColor(getResources().getColor(R.color.colorAccent));
                    break;
            }

            tab.setCustomView(view);
            tabLayout.addTab(tab);
        }

        ((ChangeColorIconWithTextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.cv)).setIconAlpha(1.0f);

        tabAdapter = new TabAdapter(this.getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(tabAdapter);
        viewPager.setOffscreenPageLimit(3);//缓存3个页面
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                ChangeColorIconWithTextView cv = (ChangeColorIconWithTextView) tab.getCustomView().findViewById(R.id.cv);
//                cv.setIconAlpha(1);
                if(!scrolling)
                    viewPager.setCurrentItem((Integer) tab.getCustomView().getTag(),false);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                if(!scrolling){
//                    ChangeColorIconWithTextView cv = (ChangeColorIconWithTextView) tab.getCustomView().findViewById(R.id.cv);
//                    cv.setIconAlpha(0);
//                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public boolean canSwipeBack() {
        return false;
    }

    @Override
    public boolean showColorStatusBar() {
        return true;
    }

    @Override
    public int getStatusColorResources() {
        return R.color.cardview_dark_background;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            ChangeColorIconWithTextView left = (ChangeColorIconWithTextView) tabLayout.getTabAt(position).getCustomView().findViewById(R.id.cv);
            ChangeColorIconWithTextView right = (ChangeColorIconWithTextView) tabLayout.getTabAt(position + 1).getCustomView().findViewById(R.id.cv);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    private boolean scrolling = false;

    @Override
    public void onPageSelected(int position) {
        if(!scrolling){
            for(int n = 0;n<=2;n++){
                ChangeColorIconWithTextView cv = (ChangeColorIconWithTextView) tabLayout.getTabAt(n).getCustomView().findViewById(R.id.cv);
                if(n == position){
                    cv.setIconAlpha(1);
                    System.out.println();
                }
                else{
                    cv.setIconAlpha(0);
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state==0)scrolling = false;
        else scrolling = true;
    }


}
