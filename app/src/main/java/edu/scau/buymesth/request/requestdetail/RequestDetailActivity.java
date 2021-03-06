package edu.scau.buymesth.request.requestdetail;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import base.util.GlideCircleTransform;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.RequestCommentAdapter;
import edu.scau.buymesth.createorder.CreateOrderActivity;
import edu.scau.buymesth.data.bean.Comment;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.publish.FlowLayout;
import edu.scau.buymesth.request.comment.CommentActivity;
import edu.scau.buymesth.util.ColorChangeHelper;
import edu.scau.buymesth.util.DividerItemDecoration;
import edu.scau.buymesth.util.NetworkHelper;

import static edu.scau.Constant.EXTRA_NEEDQUERY;
import static edu.scau.Constant.EXTRA_REQUEST;

/**
 * Created by John on 2016/8/20.
 */

public class RequestDetailActivity extends BaseActivity implements RequestDetailContract.View {
    @Bind(R.id.tv_content)
    TextView content;
    @Bind(R.id.vp_images)
    ViewPager mViewPager;
    @Bind(R.id.tv_title)
    TextView title;
    @Bind(R.id.tv_time)
    TextView time;
    @Bind(R.id.tv_remark)
    TextView mRemarkTv;
    @Bind(R.id.iv_avatar_user)
    ImageView userAvatar;
    @Bind(R.id.tv_comment)
    TextView comment;
    @Bind(R.id.iv_avatar_author)
    ImageView authorAvatar;
    @Bind(R.id.tv_level)
    TextView level;
    @Bind(R.id.tv_name)
    TextView name;
    @Bind(R.id.rl_user_info)
    RelativeLayout userInfoBar;
    @Bind(R.id.rv_comment)
    RecyclerView rvComment;
    @Bind(R.id.tv_create_order)
    TextView mCreateOrderBtn;
    @Bind(R.id.fl_tags)
    FlowLayout flTags;
    @Bind(R.id.tv_price)
    TextView mPriceTv;
    @Bind(R.id.tv_page_num)
    TextView mPageNumTv;
    private RequestDetailPresenter presenter;
    private int[] heights;
    private List<ImageView> imageViews;
    private PagerAdapter mAdapter;
    private boolean mIsSelf = false;
    @Bind(R.id.btn_follow)
    Button mFollowBtn;
    @Bind(R.id.tv_tag_hint)
    TextView mTagHintTv;


    public static void navigate(Activity activity, Request request) {
        Intent intent = new Intent(activity, RequestDetailActivity.class);
        intent.putExtra(EXTRA_REQUEST, request);
        activity.startActivity(intent);
    }

    public static void navigate(Activity activity, Request request, boolean needQueryRequest) {
        Intent intent = new Intent(activity, RequestDetailActivity.class);
        intent.putExtra(EXTRA_REQUEST, request);
        intent.putExtra(EXTRA_NEEDQUERY, needQueryRequest);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_requestdetail;
    }


    @Override
    public void initView() {

        rvComment.setLayoutManager(new LinearLayoutManager(mContext));

        rvComment.setHasFixedSize(true);
        rvComment.setNestedScrollingEnabled(false);
        rvComment.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        RequestDetailModel model = new RequestDetailModel();
        model.setRequest((Request) getIntent().getSerializableExtra(Constant.EXTRA_REQUEST));
        mPresenter = presenter = new RequestDetailPresenter();
        //需要联网查询
        presenter.mNeedQueryRequest = getIntent().getBooleanExtra(EXTRA_NEEDQUERY, false);
        presenter.setVM(this, model);
        if (!presenter.mNeedQueryRequest)
            mIsSelf = model.getRequest().getUser().getObjectId().equals(BmobUser.getCurrentUser().getObjectId());
        mCreateOrderBtn.setOnClickListener(v -> {
            if (!mIsSelf)
                CreateOrderActivity.navigateTo(mContext, (Request) getIntent().getSerializableExtra(EXTRA_REQUEST));
            else toast("不能接自己的单");
        });

    }

    @Override
    protected void setListener() {
        comment.setOnClickListener(v -> CommentActivity.navigateTo(mContext, presenter.getRequest()));
        mRemarkTv.setOnClickListener(v -> CommentActivity.navigateTo(mContext, presenter.getRequest()));
        userAvatar.setOnClickListener(v -> presenter.onAuthorOnClicked());
    }


    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public boolean showColorStatusBar() {
        return true;
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    public void setAuthorAvatar(String avatar) {
        Glide.with(mContext).load(avatar).placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into(authorAvatar);
    }

    @Override
    public void setAuthorExp(Integer exp) {
        String levelString = "LV" + exp / 10;
        level.setText(levelString);
        Drawable levelBg = ColorChangeHelper.tintDrawable(mContext.getResources().getDrawable(R.drawable.rect_black),
                    ColorStateList.valueOf(ColorChangeHelper.IntToColorValue(exp/10*10)));
        level.setBackground(levelBg);
    }

    @Override
    public void setAuthorName(String nickname) {
        name.setText(nickname);
    }

    @Override
    public void setAuthorOnClicked() {
        userInfoBar.setOnClickListener((v) -> {
            presenter.onAuthorOnClicked();
        });
    }

    @Override
    public void setOnFollowClicked() {
        mIsSelf = presenter.mModel.getRequest().getUser().getObjectId().equals(BmobUser.getCurrentUser().getObjectId());
        mFollowBtn.setOnClickListener((v) -> {
            if (!mIsSelf)
                presenter.follow();
            else
                toast("关注自己毫无意义");
        });

    }

    @Override
    public void setOnCollectClicked() {
        TextView accept = (TextView) findViewById(R.id.tv_collect);
        accept.setOnClickListener((v) -> {
            if (!mIsSelf)
                presenter.collect();
            else
                toast("收藏自己毫无意义");
        });
    }

    @Override
    public void setCommentBtn(String commentBtnStr) {
        comment.setText(commentBtnStr);
    }

    @Override
    public void setUserAvatar() {
        SharedPreferences globalUser = getSharedPreferences(Constant.SHARE_PREFERENCE_USER_INFO, MODE_PRIVATE);
        String avatar = globalUser.getString(Constant.KEY_AVATAR, "");
        Glide.with(mContext).load(avatar).placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into(userAvatar);
    }

    @Override
    public void setTitle(String title) {
        this.title.setText(title);

    }

    @Override
    public void setContent(String content) {
        this.content.setText(content);

    }


    @Override
    public void setTime(String createdAt) {
        String timeString = createdAt + "发布";
        time.setText(timeString);
    }

    @Override
    public void setUpViewPager(List<String> picHeights, List<String> picWidths, List<String> urls) {
        if (picHeights.size() != urls.size() || picWidths.size() != urls.size() || urls.size() == 0)
            return;
        imageViews = new ArrayList<>(urls.size());
        heights = new int[urls.size()];
        mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                WindowManager manager = getWindow().getWindowManager();
                DisplayMetrics outMetrics = new DisplayMetrics();
                manager.getDefaultDisplay().getMetrics(outMetrics);
                for (int i = 0; i < urls.size(); ++i) {
                    ImageView imageView = new ImageView(RequestDetailActivity.this);
                    int height = (int) ((1.0f * outMetrics.widthPixels / Integer.parseInt(picWidths.get(i))) * Integer.parseInt(picHeights.get(i)));
                    heights[i] = height;
                    Glide.with(mContext).load(urls.get(i)).into(imageView);
                    imageViews.add(imageView);
                }
                mAdapter.notifyDataSetChanged();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                //为ViewPager设置高度
                ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
                params.height = heights[0];
                mViewPager.setLayoutParams(params);
                if (heights.length > 1)
                    mPageNumTv.setText("1/" + heights.length);
            }
        });
        mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return imageViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = imageViews.get(position);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                container.addView(imageView);
                return imageView;
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //最后一页就不用动态改变高度了
                if (position == heights.length - 1) {
                    return;
                }
                //计算ViewPager现在应该的高度,heights[]表示页面高度的数组。
                int height = (int) (heights[position] * (1 - positionOffset) + heights[position + 1] * positionOffset);

                //为ViewPager设置高度
                ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
                params.height = height;
                mViewPager.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                mPageNumTv.setText(position + 1 + "/" + heights.length);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void setFollow(boolean b) {
        runOnUiThread(() -> {
            if (b) {
                mFollowBtn.setText(getResources().getString(R.string.text_followed));
            } else {
                mFollowBtn.setText(getResources().getString(R.string.text_follow));
            }
        });
    }

    @Override
    public void setCollect(boolean b) {
        runOnUiThread(() -> {
            if (b) {
                ((TextView) findViewById(R.id.tv_collect)).setText(getResources().getString(R.string.text_collected));
            } else {
                ((TextView) findViewById(R.id.tv_collect)).setText(getResources().getString(R.string.text_collect));
            }
        });
    }

    @Override
    public void showDialog() {
        showLoadingDialog();
    }

    @Override
    public void closeDialog() {
        closeLoadingDialog();
    }

    @Override
    public Activity getContext() {
        return (Activity) mContext;
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void hideViewPager() {
        mViewPager.setVisibility(View.GONE);
    }

    @Override
    public void setComment(List<Comment> commentList) {
        if (commentList.size() == 0) {
            return;
        }
        RequestCommentAdapter requestCommentAdapter = new RequestCommentAdapter(commentList);
        rvComment.setAdapter(requestCommentAdapter);
    }


    @Override
    public void setTagList(List<String> tags) {
        if (tags.size() == 0) {
            mTagHintTv.setVisibility(View.GONE);
            flTags.setVisibility(View.GONE);
            return;
        }
        for (String tag : tags) {
            TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tv_tag, null);
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginLayoutParams.setMargins(4, 4, 4, 4);
            tv.setLayoutParams(marginLayoutParams);
            tv.setText(tag);
            tv.setClickable(true);
            flTags.addView(tv);
        }
    }

    @Override
    public void setPrice(String price) {
        mPriceTv.setText(price);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkHelper.isOpenNetwork(mContext))
            presenter.refreshComment();
    }
}
