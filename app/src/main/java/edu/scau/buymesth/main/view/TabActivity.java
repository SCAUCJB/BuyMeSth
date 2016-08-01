package edu.scau.buymesth.main.view;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.TabAdapter;
import edu.scau.buymesth.discover.view.DiscoverFragment;
import edu.scau.buymesth.home.view.HomeFragment;
import edu.scau.buymesth.user.view.UserFragment;

/**
 * Created by Jammy on 2016/8/1.
 */
public class TabActivity extends BaseActivity {

    List<Fragment> fragmentList = new ArrayList<Fragment>();
    List<String> list_title = new ArrayList<>();
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
        UserFragment userFragment = new UserFragment();
        DiscoverFragment discoverFragment = new DiscoverFragment();
        HomeFragment homeFragment = new HomeFragment();
        fragmentList.add(homeFragment);
        fragmentList.add(discoverFragment);
        fragmentList.add(userFragment);

        list_title.add("主页");
        list_title.add("发现");
        list_title.add("个人");

        tabLayout.addTab(tabLayout.newTab().setText(list_title.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(list_title.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(list_title.get(2)));

        tabAdapter = new TabAdapter(this.getSupportFragmentManager(), list_title, fragmentList);
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

}
