package edu.scau.buymesth.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.TabAdapter;
import edu.scau.buymesth.chat.ChatFragment;
import edu.scau.buymesth.discover.list.DiscoverFragment;
import edu.scau.buymesth.discover.publish.MomentPublishActivity;
import edu.scau.buymesth.request.HomeFragment;
import edu.scau.buymesth.publish.PublishActivity;
import edu.scau.buymesth.request.HomePresenter;
import edu.scau.buymesth.user.UserFragment;
import ui.widget.ChangeColorIconWithTextView;

/**
 * Created by Jammy on 2016/8/1.
 * Updated by John on 2016/8/9
 */
public class TabActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    List<Fragment> fragmentList = new ArrayList<>();
    TabAdapter tabAdapter;

    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.fab_menu)
    FloatingActionMenu fab;
    @Bind(R.id.fab1)
    FloatingActionButton fab1;
    @Bind(R.id.fab2)
    FloatingActionButton fab2;
    @Bind(R.id.fab3)
    FloatingActionButton fab3;

    private DiscoverFragment discoverFragment;
    private HomeFragment homeFragment;
    private ChatFragment chatFragment;
    private AlertDialog searchDialog;
    private EditText et;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tab;
    }

    @Override
    public void initView() {
        tabLayout.setSelectedTabIndicatorHeight(0);
        UserFragment userFragment = new UserFragment();
        discoverFragment = new DiscoverFragment();
        homeFragment = new HomeFragment();
        chatFragment = new ChatFragment();

        homeFragment.setRelatedFab(fab);
        fab.setClosedOnTouchOutside(true);
        fab1.setOnClickListener(v -> {
            Intent i = new Intent(TabActivity.this,PublishActivity.class);
            startActivity(i);
        });
        fab2.setOnClickListener(v -> {
            Intent i = new Intent(TabActivity.this, MomentPublishActivity.class);
            startActivity(i);
        });
        fab3.setOnClickListener(v -> {
            if(et==null)et = new EditText(TabActivity.this);
            if(searchDialog==null)
                searchDialog = new AlertDialog.Builder(TabActivity.this).setTitle("搜索")
                        .setView(et)
                        .setPositiveButton("确定",
                        (dialog, which) -> {
                            homeFragment.setFilter(HomePresenter.FILTER_FUZZY_SEARCH,et.getText().toString());
                        })
                        .setNegativeButton("取消", null)
                        .setNeutralButton("清除", (dialog, which) -> {
                            et.setText("");
                            homeFragment.setFilter(null,null);
                        })
                        .show();
            else
                searchDialog.show();
            fab.close(true);
        });

        fragmentList.add(homeFragment);
        fragmentList.add(discoverFragment);
        fragmentList.add(chatFragment);
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
                    cv.setIcon(R.drawable.ic_whatshot);
                    cv.setIconColor(getResources().getColor(R.color.colorAccent));
                    break;
                case 3:
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
        viewPager.setOffscreenPageLimit(4);//缓存4个页面
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (!scrolling)
                    viewPager.setCurrentItem((Integer) tab.getCustomView().getTag(), false);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        if(fab.isOpened()){
            fab.close(true);
        }else {
            super.onBackPressed();
        }
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
        return R.color.colorPrimaryDark;
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
        if (!scrolling) {
            for (int n = 0; n <= 3; n++) {
                ChangeColorIconWithTextView cv = (ChangeColorIconWithTextView) tabLayout.getTabAt(n).getCustomView().findViewById(R.id.cv);
                if (n == position) {
                    cv.setIconAlpha(1);
                } else {
                    cv.setIconAlpha(0);
                }
            }
        }
        if(position==0||position==1){
            fab.showMenu(true);
        }else {
            fab.hideMenu(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 0) scrolling = false;
        else scrolling = true;
    }


}
