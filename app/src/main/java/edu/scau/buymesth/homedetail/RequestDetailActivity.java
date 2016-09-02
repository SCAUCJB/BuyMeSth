package edu.scau.buymesth.homedetail;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import base.BaseActivity;
import base.util.GlideCircleTransform;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import cache.lru.DiskLruCache;
import cn.bmob.v3.BmobUser;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.RequestCommentAdapter;
import edu.scau.buymesth.createorder.CreateOrderActivity;
import edu.scau.buymesth.data.bean.Comment;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.publish.FlowLayout;
import edu.scau.buymesth.util.DiskLruCacheHelper;

/**
 * Created by John on 2016/8/20.
 */

public class RequestDetailActivity extends BaseActivity implements RequestDetailContract.View {
    public static final String EXTRA_REQUEST = "edu.scau.buymesth.homedetail.request";
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
    private RequestDetailPresenter presenter;
    private int[] heights;
    private List<ImageView> imageViews;
    private PagerAdapter mAdapter;
    private ConcurrentLinkedQueue<HttpURLConnection> connections = new ConcurrentLinkedQueue<>();

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
        RequestDetailModel model = new RequestDetailModel();
        model.setRequest((Request) getIntent().getSerializableExtra(EXTRA_REQUEST));
        presenter = new RequestDetailPresenter();
        presenter.setVM(this, model);
        presenter.initUserInfo();
        presenter.initCommentBar();
        presenter.initContent();
        presenter.initComment();
        presenter.initTags();
        if(!model.getRequest().getAuthor().getObjectId().equals( BmobUser.getCurrentUser().getObjectId()))
        mCreateOrderBtn.setOnClickListener(v -> CreateOrderActivity.navigateTo(mContext,(Request) getIntent().getSerializableExtra(EXTRA_REQUEST)));
    }


    private volatile DiskLruCache mDiskLruCache = null;

    /**
     * 仅仅获取图片的高度，不加载图片
     *
     * @param urlString
     * @return
     */
    private int getImageHeight(String urlString) {

        String heightCache = DiskLruCacheHelper.getAsString(urlString + "height", mDiskLruCache);
        if (heightCache != null)
            return Integer.valueOf(heightCache);
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        int height = -1;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            connections.add(urlConnection);
            urlConnection.setReadTimeout(50000);
            inputStream = urlConnection.getInputStream();
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            WindowManager manager = getWindow().getWindowManager();
            DisplayMetrics outMetrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(outMetrics);
            height = (int) ((1.0f * outMetrics.widthPixels / options.outWidth) * options.outHeight);
            if(mDiskLruCache!=null)
            DiskLruCacheHelper.put(urlString + "height", String.valueOf(height), mDiskLruCache);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
                if (!connections.isEmpty())
                    connections.remove(urlConnection);
            }
        }
        return height;
    }

    @Override
    protected void onDestroy() {
        for (HttpURLConnection connection : connections) {
            if (connection != null)
                connection.disconnect();
        }
        if(!fixedThreadPool.isTerminated())
        {
           int unfinished= fixedThreadPool.shutdownNow().size();
            Log.d("zhx","unfinished thread="+unfinished);
        }
        presenter.onDestroy();
        presenter = null;
        if (mDiskLruCache != null) try {
            mDiskLruCache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
    @Override
    public void setUpViewPager(List<String> urls) {
        imageViews = new ArrayList<>(urls.size());
        heights = new int[urls.size()];
        mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mDiskLruCache == null) {
                    mDiskLruCache = DiskLruCacheHelper.create(mContext, "requestDetail");
                }
                for (int i = 0; i < urls.size(); ++i) {
                    ImageView imageView = new ImageView(RequestDetailActivity.this);
                    final int fi = i;
                    fixedThreadPool.execute(() -> {
                        heights[fi] = getImageHeight(urls.get(fi));
                        RequestDetailActivity.this.runOnUiThread(
                                () -> {
                                    if (fi == 0) {
                                        //ViewPager的初始高度设置为第一个页面的高度
                                        ViewGroup.LayoutParams layoutParams = mViewPager.getLayoutParams();
                                        layoutParams.height = heights[0];
                                        mViewPager.setLayoutParams(layoutParams);
                                    }
                                }
                        );
                    });
                   Glide.with(mContext).load(urls.get(i)).into(imageView);
                   imageViews.add(imageView);
                }
                fixedThreadPool.shutdown();
                mAdapter.notifyDataSetChanged();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

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
}
