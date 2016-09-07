package edu.scau.buymesth.request.requestdetail;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import base.util.GlideCircleTransform;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.RequestCommentAdapter;
import edu.scau.buymesth.createorder.CreateOrderActivity;
import edu.scau.buymesth.data.bean.Comment;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.publish.FlowLayout;
import util.RecycleViewDivider;

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
    @Bind(R.id.tv_mark)
    TextView mark;
    @Bind(R.id.tv_like)
    TextView like;
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

    public static void navigate(Activity activity, Request request) {
        Intent intent = new Intent(activity, RequestDetailActivity.class);
        intent.putExtra(EXTRA_REQUEST, request);
        activity.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_requestdetail;
    }

    @Override
    public void initView() {
        rvComment.setLayoutManager(new LinearLayoutManager(mContext));
        rvComment.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_4)));
        rvComment.setHasFixedSize(true);
        rvComment.setNestedScrollingEnabled(false);
        rvComment.addItemDecoration(new RecycleViewDivider(
                mContext, LinearLayoutManager.VERTICAL, 1, getResources().getColor(R.color.lightgrey)));

        RequestDetailModel model = new RequestDetailModel();
        model.setRequest((Request) getIntent().getSerializableExtra(Constant.EXTRA_REQUEST));
        presenter = new RequestDetailPresenter();
        presenter.setVM(this, model);
        if(!model.getRequest().getAuthor().getObjectId().equals( BmobUser.getCurrentUser().getObjectId()))
        mCreateOrderBtn.setOnClickListener(v -> CreateOrderActivity.navigateTo(mContext,(Request) getIntent().getSerializableExtra(EXTRA_REQUEST)));
    }

    @Override
    protected void setListener() {
      //  comment.setOnClickListener(v->);
    }

    @Override
    protected void onDestroy() {

        presenter.onDestroy();
        presenter = null;
        super.onDestroy();
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
    }

    @Override
    public void setAuthorName(String nickname) {
        name.setText(nickname);
    }

    @Override
    public void setAuthorOnClicked() {
        userInfoBar.setOnClickListener((v) -> {
            Toast.makeText(mContext, "跳转用户页面", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void setOnAcceptClicked() {
        TextView accept = (TextView) findViewById(R.id.tv_accept);
        accept.setOnClickListener((v) -> {
            Toast.makeText(mContext, "关注", Toast.LENGTH_SHORT).show();
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
    public void setLikes(Integer likes) {
        String likeString = likes + "次赞";
        like.setText(likeString);
    }

    @Override
    public void setTime(String createdAt) {
        String timeString = createdAt + "发布";
        time.setText(timeString);
    }
    @Override
    public void setUpViewPager(List<String> picHeights, List<String> picWidths,List<String> urls) {
        if(picHeights.size()!=urls.size()||picWidths.size()!=urls.size()||urls.size()==0)return;
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
                    heights[i]= height;
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
                if(heights.length>1)
                mPageNumTv.setText("1/"+heights.length);
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
                mPageNumTv.setText(position+1+"/"+heights.length);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        requestCommentAdapter.setOnRecyclerViewItemClickListener((v, p) -> {
            Toast.makeText(mContext, "i was click", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void setTagList(List<String> tags) {
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
}
