package edu.scau.buymesth.user;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import base.BaseActivity;
import base.util.GlideCircleTransform;
import base.util.ToastUtil;
import cn.bmob.v3.BmobUser;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.ViewPagerAdapter;
import edu.scau.buymesth.ui.LoginActivity;
import edu.scau.buymesth.ui.RegisterActivity;
import edu.scau.buymesth.user.address.AddressActivity;
import edu.scau.buymesth.user.mark.MarkActivity;
import edu.scau.buymesth.user.order.OrderFragment;
import edu.scau.buymesth.user.request.RequestFragment;
import edu.scau.buymesth.user.setting.UserSettingActivity;
import edu.scau.buymesth.util.ColorChangeHelper;
import util.DensityUtil;

/**
 * Created by Jammy on 2016/8/1.
 */
public class UserFragment extends Fragment implements UserContract.View {
    UserPresenter mPresenter;
    ImageView mAvatarIv;
    TextView mNameTv;
    TextView mLevelTv;
    TextView mLocationTv;
    TextView mSignatureTv;
    Button mSettingBtn;
    TextView mScoreTv;
    TextView mPopulationTv;
    RatingBar mRatingBar;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    CoordinatorLayout mCoordinatorLayout;
    LinearLayout userInfoLl;

    private BottomSheetBehavior<NestedScrollView> behavior;
    private SparseArray<Drawable> mLevelDrawableCache = new SparseArray<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        mAvatarIv = (ImageView) view.findViewById(R.id.iv_avatar);
        mNameTv = (TextView) view.findViewById(R.id.tv_user);
        mLevelTv = (TextView) view.findViewById(R.id.tv_level);
        mLocationTv = (TextView) view.findViewById(R.id.tv_location);
        mSignatureTv = (TextView) view.findViewById(R.id.tv_signature);
        mSettingBtn = (Button) view.findViewById(R.id.btn_setting);
        mScoreTv = (TextView) view.findViewById(R.id.tv_score);
        mPopulationTv = (TextView) view.findViewById(R.id.tv_population);
        mRatingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl);
        userInfoLl = (LinearLayout) view.findViewById(R.id.ll_user_info);
        mSettingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserSettingActivity.class);
            getActivity().startActivity(intent);
        });
        view.findViewById(R.id.address_manage).setOnClickListener(v->{AddressActivity.navigate(getActivity());});
        view.findViewById(R.id.mark_list).setOnClickListener(v->{
            MarkActivity.navigate(getContext());});
        UserModel model = new UserModel(getContext());
        mPresenter = new UserPresenter(this, model);
        initTab();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setUserName(String name) {
        mNameTv.setText(name);
    }

    @Override
    public void setAvatar(String url) {
        Glide.with(getContext()).load(url).crossFade().placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(getContext())).into(mAvatarIv);
        mAvatarIv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BmobUser.logOut();
                ToastUtil.show("log out");
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                return false;
            }
        });
    }

    @Override
    public void setLevel(Integer exp) {
        Drawable levelBg = mLevelDrawableCache.get(exp / 10 * 10);
        if (levelBg == null) {
            levelBg = ColorChangeHelper.tintDrawable(getContext().getResources().getDrawable(R.drawable.rect_black),
                    ColorStateList.valueOf(ColorChangeHelper.IntToColorValue(exp / 10 * 10)));
            mLevelDrawableCache.put(exp / 10 * 10, levelBg);
        }
        mLevelTv.setBackground(levelBg);
        mLevelTv.setText("LV" + exp / 10);
    }

    @Override
    public void setlocation(String location) {
        mLocationTv.setText(location);
    }

    @Override
    public void setSignature(String signature) {
        mSignatureTv.setText(signature);
    }

    @Override
    public void setScore(String score) {
        mScoreTv.setText(score);
    }

    @Override
    public void setPopulation(String population) {
        mPopulationTv.setText(population);
    }


    @Override
    public void setRatingBar(Float score) {
        if (score != null)
            mRatingBar.setRating(score);
    }

    @Override
    public void initTab() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        RequestFragment requestFragment = new RequestFragment();
        OrderFragment orderFragment = new OrderFragment();
        adapter.addTab(requestFragment, "我的请求");
        adapter.addTab(orderFragment,"我的订单");
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        NestedScrollView bottomSheet = (NestedScrollView) mCoordinatorLayout.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        requestFragment.disallowIntercept(bottomSheet);
        orderFragment.disallowIntercept(bottomSheet);
        userInfoLl.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= 16) {
                            userInfoLl.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        } else {
                            userInfoLl.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                        behavior.setPeekHeight(wm.getDefaultDisplay().getHeight() - userInfoLl.getHeight() - DensityUtil.dip2px(getContext(), 56f) - ((BaseActivity) getActivity()).getStatusBarHeight());
                    }
                });
    }


}
