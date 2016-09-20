package edu.scau.buymesth.main;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.TabAdapter;
import edu.scau.buymesth.chat.ChatFragment;
import edu.scau.buymesth.discover.list.DiscoverFragment;
import edu.scau.buymesth.discover.publish.MomentPublishActivity;
import edu.scau.buymesth.publish.PublishActivity;
import edu.scau.buymesth.request.HomeFragment;
import edu.scau.buymesth.request.HomePresenter;
import edu.scau.buymesth.request.requestdetail.RequestDetailActivity;
import edu.scau.buymesth.user.UserFragment;
import ui.widget.ChangeColorIconWithTextView;

/**
 * Created by Jammy on 2016/8/1.
 * Updated by John on 2016/8/9
 */
public class TabActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    List<Fragment> fragmentList = new ArrayList<>(4);
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
    @Bind(R.id.fab4)
    FloatingActionButton fab4;

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
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(homeFragment.getFilter()!=HomePresenter.FILTER_FOLLOW_ONLY)
                    homeFragment.setFilter(HomePresenter.FILTER_FOLLOW_ONLY,null);
                else homeFragment.setFilter(null,null);
                fab.close(true);
            }
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
    private boolean mIsExit;
    private Handler handler=new Handler();
    @Override
    public void onBackPressed() {
        if(fab.isOpened()){
            fab.close(true);
        }else {
            if (mIsExit) {
                this.finish();
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                handler.postDelayed(() -> mIsExit = false, 2000);
            }

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

    @Override
    protected void onDestroy() {
        fixInputMethodManagerLeak(mContext);
        super.onDestroy();
    }

    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String [] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0;i < arr.length;i ++) {
            String param = arr[i];
            try{
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            }catch(Throwable t){
                t.printStackTrace();
            }
        }
    }

}
