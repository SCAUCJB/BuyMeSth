package edu.scau.buymesth.userinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import base.BaseActivity;
import base.util.GlideCircleTransform;
import base.util.ToastUtil;
import bitmap.BlurTransformation;
import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.ViewPagerAdapter;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.user.order.OrderFragment;
import edu.scau.buymesth.user.request.RequestFragment;
import edu.scau.buymesth.userinfo.evaluate.EvaluateListActivity;
import edu.scau.buymesth.util.ColorChangeHelper;
import edu.scau.buymesth.util.NetworkHelper;

/**
 * Created by John on 2016/9/24.
 */

public class UserInfoActivity extends BaseActivity implements Contract.View{
    UserInfoPresenter mPresenter;
    @Bind(R.id.iv_avatar)
    ImageView mAvatarIv;
    @Bind(R.id.tv_user)
    TextView mNameTv;
    @Bind(R.id.tv_level)
    TextView mLevelTv;
    @Bind(R.id.tv_location)
    TextView mLocationTv;
    @Bind(R.id.tv_signature)
    TextView mSignatureTv;
    @Bind(R.id.btn_follow)
    Button mFollow;
    @Bind(R.id.tv_score)
    TextView mScoreTv;
    @Bind(R.id.tv_population)
    TextView mPopulationTv;
    @Bind(R.id.ratingBar)
    RatingBar mRatingBar;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.rl_user_info)
    RelativeLayout mUserInfoRl;
    @Bind(R.id.tv_user_id)
    TextView mUserIdTv;
    @Bind(R.id.bg_user_info)
    ImageView mBgUser;
    private BottomSheetBehavior<NestedScrollView> behavior;
    private SparseArray<Drawable> mLevelDrawableCache=new SparseArray<>();

    public static void navigate(Activity activity,User user){
        Intent intent=new Intent(activity,UserInfoActivity.class);
        if(user==null)   return;
        intent.putExtra("user",user);
        activity.startActivity(intent);
    }

    public static void navigate(Activity activity,String userId){
        Intent intent=new Intent(activity,UserInfoActivity.class);
        if(userId==null)   return;
        intent.putExtra("userId",userId);
        activity.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    public void initView() {
        setTitle("用户信息");
        mUserInfoRl.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= 16) {
                            mUserInfoRl.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }
                        else {
                            mUserInfoRl.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                        behavior.setPeekHeight(wm.getDefaultDisplay().getHeight()-mUserInfoRl.getHeight()-getSupportActionBar().getHeight()-getStatusBarHeight());
                    }
                });

    }

    @Override
    protected void initPresenter() {
        UserInfoModel model=new UserInfoModel(this);
        if(getIntent().getSerializableExtra("user") !=null){
            model.setUser((User) getIntent().getSerializableExtra("user"));
            mPresenter=new UserInfoPresenter(this,model,true);
        }
        else{
            User user = new User();
            user.setObjectId(getIntent().getStringExtra("userId"));
            model.setUser(user);
            BmobQuery<User> query = new BmobQuery<>();
            showLoadingDialog();
            query.getObject(user.getObjectId(), new QueryListener<User>() {
                @Override
                public void done(User user0, BmobException e) {
                    closeLoadingDialog();
                    if(e==null) {
                        mPresenter.mModel.setUser(user0);
                        runOnUiThread(() -> mPresenter.showUserInfo());
                    }
                    else
                        runOnUiThread(() -> ToastUtil.show("加载用户资料失败 "+e.toString()));
                }
            });
            mPresenter=new UserInfoPresenter(this,model,false);
        }
        mPopulationTv.setOnClickListener(v-> EvaluateListActivity.navigate(mContext, model.getUser().getObjectId(),false));
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setUserName(String name) {
        mNameTv.setText(name);
    }

    @Override
    public void setAvatar(String url) {
        Glide.with(mContext).load(url).crossFade().placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into(mAvatarIv);
        if(url!=null)
        Glide.with(this).
                load(url).
                asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).
                transform(new BlurTransformation(mContext,40)).//高斯模糊处理
                into(mBgUser);
    }

    @Override
    public void setLevel(Integer exp) {
        Drawable levelBg = mLevelDrawableCache.get(exp/10*10);
        if(levelBg==null){
            levelBg = ColorChangeHelper.tintDrawable(mContext.getResources().getDrawable(R.drawable.rect_black),
                    ColorStateList.valueOf(ColorChangeHelper.IntToColorValue(exp/10*10)));
            mLevelDrawableCache.put(exp/10*10,levelBg);
        }
        mLevelTv.setBackground(levelBg);
        mLevelTv.setText("LV"+exp/10);
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
    public void showMsg(String msg) {
        toast(msg);
    }

    @Override
    public boolean hasNet() {
        return NetworkHelper.isOpenNetwork(mContext);
    }

    @Override
    public void setRatingBar(Float score) {
        if(score!=null)
        mRatingBar.setRating(score);
    }

    @Override
    public void initTab() {
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        RequestFragment requestFragment=new RequestFragment();
        adapter.addTab(requestFragment,"TA的请求");
        OrderFragment orderFragment = new OrderFragment();
        adapter.addTab(orderFragment, "TA的订单");
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl);
        NestedScrollView bottomSheet = (NestedScrollView) coordinatorLayout.findViewById(R.id.bottom_sheet);
         behavior=BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mUserInfoRl.setTranslationY(-mUserInfoRl.getHeight()*slideOffset/3);
            }
        });
        requestFragment.disallowIntercept(bottomSheet);
        orderFragment.disallowIntercept(bottomSheet);
    }
    @Override
    public void setEvaluateCount(Integer integer) {
        mPopulationTv.setText(integer+"人评价");
    }

    @Override
    public void setFollow(boolean b) {
        runOnUiThread(() -> {
            if (b) {
                mFollow.setText(getResources().getString(R.string.text_followed));
            } else {
                mFollow.setText(getResources().getString(R.string.text_follow));
            }
        });
    }

    @Override
    public void setUserId(String id) {
        mUserIdTv.setText(id);
    }

    @Override
    protected void setListener() {
        boolean isSelf = mPresenter.mModel.getUser().getObjectId().equals(BmobUser.getCurrentUser().getObjectId());
        mFollow.setOnClickListener((v) -> {
            if (!isSelf)
                mPresenter.follow();
            else
                toast("关注自己毫无意义");
        });
    }

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }
}
