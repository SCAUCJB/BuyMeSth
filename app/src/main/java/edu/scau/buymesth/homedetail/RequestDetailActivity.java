package edu.scau.buymesth.homedetail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import base.BaseActivity;
import base.util.GlideCircleTransform;
import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by John on 2016/8/20.
 */

public class RequestDetailActivity extends BaseActivity {
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
    ImageView userAavatar;
    @Bind(R.id.tv_comment)
    TextView comment;
    private int[] heights;
    private List<ImageView> imageViews;
    private PagerAdapter mAdapter;
    private Request mRequest;
    private LinkedList<HttpURLConnection> connections = new LinkedList<>();

    public static void navigate(AppCompatActivity activity, Request request) {
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
        initToolBar();
        mRequest = (Request) getIntent().getSerializableExtra(EXTRA_REQUEST);
        initUserInfo();
        initContent();

    }

    private void initContent() {
        title.setText(mRequest.getTitle());
        content.setText(mRequest.getContent());
        String timeString = mRequest.getCreatedAt() + "发布";
        time.setText(timeString);
        String likeString = mRequest.getLikes() + "次赞";
        like.setText(likeString);
        if (mRequest.getUrls() != null) {
            imageViews = new ArrayList<>(mRequest.getUrls().size());
            heights = new int[mRequest.getUrls().size()];
            initViewPager();
        } else
            mViewPager.setVisibility(View.GONE);
    }

    private void initUserInfo() {
        ImageView authorAvatar = (ImageView) findViewById(R.id.iv_avatar_author);
        Glide.with(mContext).load(mRequest.getAuthor().getAvatar()).placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into(authorAvatar);
        TextView level = (TextView) findViewById(R.id.tv_level);
        String levelString = "LV" + mRequest.getAuthor().getExp() / 10;
        level.setText(levelString);
        String nameString=mRequest.getAuthor().getNickname();
        TextView name= (TextView) findViewById(R.id.tv_name);
        name.setText(nameString);
        RelativeLayout userInfo = (RelativeLayout) findViewById(R.id.rl_user_info);
        userInfo.setOnClickListener((v) -> {
            Toast.makeText(mContext, "跳转用户页面", Toast.LENGTH_SHORT).show();
        });
        TextView follow = (TextView) findViewById(R.id.tv_follow);
        follow.setOnClickListener((v) -> {
            Toast.makeText(mContext, "关注", Toast.LENGTH_SHORT).show();
        });
        SharedPreferences globalUser=getSharedPreferences(Constant.SHARE_PREFRENCE_USER_INFO, MODE_PRIVATE);
        String avatar=globalUser.getString(Constant.KEY_AVATAR,"");
        Glide.with(mContext).load(avatar).placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into(userAavatar);

        comment.setText("要勾搭，先评论");

    }

    private void initViewPager() {
        if (mRequest.getUrls() != null)
            mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    for (int i = mRequest.getUrls().size() - 1; i >= 0; --i) {
                        ImageView imageView = new ImageView(RequestDetailActivity.this);
                        if (mRequest.getUrls() != null) {
                            final int fi = i;
                            new Thread() {
                                @Override
                                public void run() {
                                    heights[fi] = getImageHeight(mRequest.getUrls().get(fi));
                                    RequestDetailActivity.this.runOnUiThread(
                                            () -> mAdapter.notifyDataSetChanged()
                                    );
                                }
                            }.start();
                            Glide.with(mContext).load(mRequest.getUrls().get(i)).into(imageView);
                        }
                        imageViews.add(imageView);
                    }
                    mAdapter.notifyDataSetChanged();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    //ViewPager的初始高度设置为第一个页面的高度
                    ViewGroup.LayoutParams layoutParams = mViewPager.getLayoutParams();
                    layoutParams.height = heights[0];
                    mViewPager.setLayoutParams(layoutParams);
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

    /**
     * 仅仅获取图片的高度，不加载图片
     *
     * @param urlString
     * @return
     */
    private int getImageHeight(String urlString) {
        HttpURLConnection urlConnection = null;
        int height = -1;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            connections.addFirst(urlConnection);
            urlConnection.setReadTimeout(50000);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(urlConnection.getInputStream(), null, options);
            WindowManager manager = getWindow().getWindowManager();
            DisplayMetrics outMetrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(outMetrics);
            height = (int) ((1.0f * outMetrics.widthPixels / options.outWidth) * options.outHeight);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
                if(!connections.isEmpty())
                connections.pollFirst();
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
        super.onDestroy();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
