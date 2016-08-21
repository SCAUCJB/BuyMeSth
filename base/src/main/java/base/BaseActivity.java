package base;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.ButterKnife;
import edu.scau.base.R;
import ui.layout.SwipeBackLayout;

/**
 * Created by John on 2016/8/1.
 *
 */
public abstract class BaseActivity extends AppCompatActivity {
    private boolean mCanSwipeBack=true;
    public Context mContext;
    private SwipeBackLayout swipeBackLayout;
    private ImageView ivShadow;
    private ImageView colorStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        //设置透明状态栏
        setTranslucentStatus();

        initView();
    }


    private void setTranslucentStatus(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT&&Build.VERSION.SDK_INT<=Build.VERSION_CODES.LOLLIPOP) {
            //透明化状态栏
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    localLayoutParams.flags);
            //设置状态栏颜色
            if(showColorStatusBar()&&colorStatus!=null)
                colorStatus.setBackgroundColor(getResources().getColor(getStatusColorResources()));
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (!canSwipeBack()) {
            FrameLayout frameLayout = new FrameLayout(this);
            //添加状态栏色块
            if(showColorStatusBar()&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                colorStatus = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight());
                frameLayout.addView(colorStatus,params);
            }
            frameLayout.setBackgroundColor(getResources().getColor(R.color.window_background));
            View view = LayoutInflater.from(this).inflate(layoutResID, null);
            frameLayout.addView(view);
            //如果设置的布局fitsSystemWindows==true 将多出的状态栏空间去掉
            if(!showColorStatusBar()&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT&&view.getFitsSystemWindows()){
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
                lp.setMargins(0,-getStatusBarHeight(),0,0);
                view.setLayoutParams(lp);
            }
            super.setContentView(frameLayout);
        } else {
            super.setContentView(getContainer());
            FrameLayout frameLayout = new FrameLayout(this);
            //添加状态栏色块
            if(showColorStatusBar()&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                colorStatus = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight());
                frameLayout.addView(colorStatus,params);
            }
            frameLayout.setBackgroundColor(getResources().getColor(R.color.window_background));

            View view = LayoutInflater.from(this).inflate(layoutResID, null);
            frameLayout.addView(view);
            //如果设置的布局fitsSystemWindows==true 将多出的状态栏空间去掉
            if(!showColorStatusBar()&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT&&view.getFitsSystemWindows()){
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
                lp.setMargins(0,-getStatusBarHeight(),0,0);
                view.setLayoutParams(lp);
            }
            swipeBackLayout.addView(frameLayout);
        }
    }

    private View getContainer() {
        RelativeLayout container = new RelativeLayout(this);
        swipeBackLayout = new SwipeBackLayout(this);
        swipeBackLayout.setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        ivShadow = new ImageView(this);
        ivShadow.setBackgroundColor(getResources().getColor(R.color.theme_black_7f));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        container.addView(ivShadow, params);
        container.addView(swipeBackLayout);
        swipeBackLayout.setOnSwipeBackListener((fa, fs) -> ivShadow.setAlpha(1 - fs));
        return container;
    }
    protected abstract int getLayoutId();
    public abstract void initView();
    public abstract boolean canSwipeBack();//返回值决定是否可滑动返回

    /**
     * 重写以选择是否显示有色状态栏
     * 布局文件的根必须fitsSystemWindows=true
     * @return
     */
    public boolean showColorStatusBar(){
        return true;
    }

    /**
     * 重写修改状态栏颜色
     * @return
     */
    public int getStatusColorResources() {
        return R.color.window_background;
    }

    public void toast(String text){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }
}
