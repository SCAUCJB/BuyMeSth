package ui.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;

import java.nio.channels.Selector;

import edu.scau.base.R;


/**
 * 根据宽高比例自动计算高度ImageView
 * Created by HMY on 2016/4/21.
 */
public class RatioImageView extends ImageView {

    /**
     * 宽高比例
     */
    private float mRatio = 0f;

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        this.setBackgroundColor(Color.GRAY);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);

        mRatio = typedArray.getFloat(R.styleable.RatioImageView_ratio, 0f);
        typedArray.recycle();
//        this.setBackgroundColor(Color.GRAY);
    }

    public RatioImageView(Context context) {
        super(context);
//        this.setBackgroundColor(Color.GRAY);
    }

    /**
     * 设置ImageView的宽高比
     *
     * @param ratio
     */
    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mRatio != 0) {
            float height = width / mRatio;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Toast.makeText(getContext(),"touch",Toast.LENGTH_SHORT).show();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Drawable drawable = getDrawable();
//                if (drawable != null) {
//                    drawable.mutate().setColorFilter(Color.GRAY,
//                            PorterDuff.Mode.MULTIPLY);
//                }
                this.setImageAlpha(170);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Drawable drawableUp = getDrawable();
//                if (drawableUp != null) {
//                    drawableUp.mutate().clearColorFilter();
//                }
                this.setImageAlpha(255);
                break;
        }

        return super.onTouchEvent(event);
    }
}