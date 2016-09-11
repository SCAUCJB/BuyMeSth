package ui.layout;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import photoview.PhotoView;

/**
 * 2d平滑变化的显示图片的ImageView
 * 仅限于用于:从一个ScaleType==CENTER_CROP的ImageView，切换到另一个ScaleType=
 * FIT_CENTER的ImageView，或者反之 (当然，得使用同样的图片最好)
 *
 * @author Dean Tao
 *
 */
public class SmoothImageView extends PhotoView {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_TRANSFORM_IN = 1;
    private static final int STATE_TRANSFORM_OUT = 2;
    private int mOriginalWidth;
    private int mOriginalHeight;
    private int mOriginalLocationX;
    private int mOriginalLocationY;
    private int mState = STATE_NORMAL;
    private Matrix mSmoothMatrix;
    private Bitmap mBitmap;
    private boolean mTransformStart = false;
    private Transform mTransform;
    private final int mBgColor = 0xFF000000;
    private int mBgAlpha = 0;
    private Paint mPaint;
    private boolean mInitOrigin = false;
    private TransformListener mTransformListener;

    public SmoothImageView(Context context) {
        super(context);
        initThis();
    }

    public SmoothImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initThis();
    }

    public SmoothImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initThis();
    }

    private void initThis() {
        mSmoothMatrix = new Matrix();
        mPaint=new Paint();
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Style.FILL);
//		setBackgroundColor(mBgColor);
    }

    public void setOriginalInfo(int width, int height, int locationX, int locationY) {
        mOriginalWidth = width;
        mOriginalHeight = height;
        mOriginalLocationX = locationX;
        mOriginalLocationY = locationY;
    }

    /**
     * 用于开始进入的方法。 调用此方前，需已经调用过setOriginalInfo
     */
    public void transformIn() {
        mState = STATE_TRANSFORM_IN;
        mTransformStart = true;
        mBgAlpha = 0;
        invalidate();
    }

    /**
     * 用于开始退出的方法。 调用此方前，需已经调用过setOriginalInfo
     */
    public void transformOut() {
        mState = STATE_TRANSFORM_OUT;
        mTransformStart = true;
        mBgAlpha = 255;
        invalidate();
    }

    private class Transform {
        float startScale;// 图片开始的缩放值
        float endScale;// 图片结束的缩放值
        float scale;// 属性ValueAnimator计算出来的值
        LocationSizeF startRect;// 开始的区域
        LocationSizeF endRect;// 结束的区域
        LocationSizeF rect;// 属性ValueAnimator计算出来的值

        void initStartIn() {
            scale = startScale;
            try {
                rect = (LocationSizeF) startRect.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        void initStartOut() {
            scale = endScale;
            try {
                rect = (LocationSizeF) endRect.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 初始化进入的变量信息
     */
    private void initTransform() {
        if (getDrawable() == null) {
            return;
        }
        if (mBitmap == null || mBitmap.isRecycled()) {
            if(getDrawable() instanceof GlideBitmapDrawable)
                mBitmap = ((GlideBitmapDrawable) getDrawable()).getBitmap();
            else
                mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        }
        //防止mTransfrom重复的做同样的初始化
        if (mTransform != null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        mTransform = new Transform();

        /** 下面为缩放的计算 */
		/* 计算初始的缩放值，初始值因为是CENTR_CROP效果，所以要保证图片的宽和高至少1个能匹配原始的宽和高，另1个大于 */
        float xSScale = mOriginalWidth / ((float) mBitmap.getWidth());
        float ySScale = mOriginalHeight / ((float) mBitmap.getHeight());
        float startScale = xSScale > ySScale ? xSScale : ySScale;
        mTransform.startScale = startScale;
		/* 计算结束时候的缩放值，结束值因为要达到FIT_CENTER效果，所以要保证图片的宽和高至少1个能匹配原始的宽和高，另1个小于 */
        float xEScale = getWidth() / ((float) mBitmap.getWidth());
        float yEScale = getHeight() / ((float) mBitmap.getHeight());
        float endScale = xEScale < yEScale ? xEScale : yEScale;
        mTransform.endScale = endScale;

        /**
         * 下面计算Canvas Clip的范围，也就是图片的显示的范围，因为图片是慢慢变大，并且是等比例的，所以这个效果还需要裁减图片显示的区域
         * ，而显示区域的变化范围是在原始CENTER_CROP效果的范围区域
         * ，到最终的FIT_CENTER的范围之间的，区域我用LocationSizeF更好计算
         * ，他就包括左上顶点坐标，和宽高，最后转为Canvas裁减的Rect.
         */
		/* 开始区域 */
        mTransform.startRect = new LocationSizeF();
        mTransform.startRect.left = mOriginalLocationX;
        mTransform.startRect.top = mOriginalLocationY;
        mTransform.startRect.width = mOriginalWidth;
        mTransform.startRect.height = mOriginalHeight;
		/* 结束区域 */
        mTransform.endRect = new LocationSizeF();
        float bitmapEndWidth = mBitmap.getWidth() * mTransform.endScale;// 图片最终的宽度
        float bitmapEndHeight = mBitmap.getHeight() * mTransform.endScale;// 图片最终的宽度
        mTransform.endRect.left = (getWidth() - bitmapEndWidth) / 2;
        mTransform.endRect.top = (getHeight() - bitmapEndHeight) / 2;
        mTransform.endRect.width = bitmapEndWidth;
        mTransform.endRect.height = bitmapEndHeight;

        mTransform.rect = new LocationSizeF();
    }

    private class LocationSizeF implements Cloneable{
        float left;
        float top;
        float width;
        float height;
        @Override
        public String toString() {
            return "[left:"+left+" top:"+top+" width:"+width+" height:"+height+"]";
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            // TODO Auto-generated method stub
            return super.clone();
        }

    }

    /* 下面实现了CENTER_CROP的功能 的Matrix，在优化的过程中，已经不用了 */
    private void getCenterCropMatrix() {
        if (getDrawable() == null) {
            return;
        }
        if (mBitmap == null || mBitmap.isRecycled()) {
            mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        }
		/* 下面实现了CENTER_CROP的功能 */
        float xScale = mOriginalWidth / ((float) mBitmap.getWidth());
        float yScale = mOriginalHeight / ((float) mBitmap.getHeight());
        float scale = xScale > yScale ? xScale : yScale;
        mSmoothMatrix.reset();
        mSmoothMatrix.setScale(scale, scale);
        mSmoothMatrix.postTranslate(-(scale * mBitmap.getWidth() / 2 - mOriginalWidth / 2), -(scale * mBitmap.getHeight() / 2 - mOriginalHeight / 2));
    }

    private void getBmpMatrix() {
        if (getDrawable() == null) {
            return;
        }
        if (mTransform == null) {
            return;
        }
        if (mBitmap == null || mBitmap.isRecycled()) {
            mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        }
		/* 下面实现了CENTER_CROP的功能 */
        mSmoothMatrix.setScale(mTransform.scale, mTransform.scale);
        mSmoothMatrix.postTranslate(-(mTransform.scale * mBitmap.getWidth() / 2 - mTransform.rect.width / 2),
                -(mTransform.scale * mBitmap.getHeight() / 2 - mTransform.rect.height / 2));
    }

    /**
     * 测量view的位置 将获得的图片屏幕位置转为view内相对位置 为什么作者不这样写呢
     */
    private void initOrigin(){
        mInitOrigin = true;
        int[] location = new int[2];
        getLocationOnScreen(location);
        mOriginalLocationY = mOriginalLocationY - location[1];
        mOriginalLocationX = mOriginalLocationX - location[0];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return; // couldn't resolve the URI
        }
        if (mState == STATE_TRANSFORM_IN || mState == STATE_TRANSFORM_OUT) {
            if (mTransformStart) {
                if(!mInitOrigin)
                    initOrigin();
                initTransform();
            }
            if (mTransform == null) {
                super.onDraw(canvas);
                return;
            }

            if (mTransformStart) {
                if (mState == STATE_TRANSFORM_IN) {
                    mTransform.initStartIn();
                } else {
                    mTransform.initStartOut();
                }
            }

            if(mTransformStart){
                Log.d("Dean", "mTransfrom.startScale:"+ mTransform.startScale);
                Log.d("Dean", "mTransfrom.startScale:"+ mTransform.endScale);
                Log.d("Dean", "mTransfrom.scale:"+ mTransform.scale);
                Log.d("Dean", "mTransfrom.startRect:"+ mTransform.startRect.toString());
                Log.d("Dean", "mTransfrom.endRect:"+ mTransform.endRect.toString());
                Log.d("Dean", "mTransfrom.rect:"+ mTransform.rect.toString());
            }

            mPaint.setAlpha(mBgAlpha);
            canvas.drawPaint(mPaint);

            int saveCount = canvas.getSaveCount();
            canvas.save();
            // 先得到图片在此刻的图像Matrix矩阵
            getBmpMatrix();
            canvas.translate(mTransform.rect.left, mTransform.rect.top);
            canvas.clipRect(0, 0, mTransform.rect.width, mTransform.rect.height);
            canvas.concat(mSmoothMatrix);
            getDrawable().draw(canvas);
            canvas.restoreToCount(saveCount);
            if (mTransformStart) {
                mTransformStart=false;
                startTransform(mState);
            }
        } else {
            //当Transform In变化完成后，把背景改为黑色，使得Activity不透明
            mPaint.setAlpha(255);
            canvas.drawPaint(mPaint);
            super.onDraw(canvas);
        }
    }

    private void startTransform(final int state) {
        if (mTransform == null) {
            return;
        }
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (state == STATE_TRANSFORM_IN) {
            PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("scale", mTransform.startScale, mTransform.endScale);
            PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("left", mTransform.startRect.left, mTransform.endRect.left);
            PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("top", mTransform.startRect.top, mTransform.endRect.top);
            PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("width", mTransform.startRect.width, mTransform.endRect.width);
            PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("height", mTransform.startRect.height, mTransform.endRect.height);
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("alpha", 0, 255);
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder, alphaHolder);
        } else {
            PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("scale", mTransform.endScale, mTransform.startScale);
            PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("left", mTransform.endRect.left, mTransform.startRect.left);
            PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("top", mTransform.endRect.top, mTransform.startRect.top);
            PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("width", mTransform.endRect.width, mTransform.startRect.width);
            PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("height", mTransform.endRect.height, mTransform.startRect.height);
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("alpha", 255, 0);
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder, alphaHolder);
        }

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public synchronized void onAnimationUpdate(ValueAnimator animation) {
                mTransform.scale = (Float) animation.getAnimatedValue("scale");
                mTransform.rect.left = (Float) animation.getAnimatedValue("left");
                mTransform.rect.top = (Float) animation.getAnimatedValue("top");
                mTransform.rect.width = (Float) animation.getAnimatedValue("width");
                mTransform.rect.height = (Float) animation.getAnimatedValue("height");
                mBgAlpha = (Integer) animation.getAnimatedValue("alpha");
                invalidate();
                ((Activity)getContext()).getWindow().getDecorView().invalidate();
            }
        });
        valueAnimator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
				/*
				 * 如果是进入的话，当然是希望最后停留在center_crop的区域。但是如果是out的话，就不应该是center_crop的位置了
				 * ， 而应该是最后变化的位置，因为当out的时候结束时，不回复视图是Normal，要不然会有一个突然闪动回去的bug
				 */
                // TODO 这个可以根据实际需求来修改
                if (state == STATE_TRANSFORM_IN) {
                    mState = STATE_NORMAL;
                }
                if (mTransformListener != null) {
                    mTransformListener.onTransformComplete(state);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    public void setOnTransformListener(TransformListener listener) {
        mTransformListener = listener;
    }

    public static interface TransformListener {
        /**
         *
         * @param mode
         *            STATE_TRANSFORM_IN 1 ,STATE_TRANSFORM_OUT 2
         */
        void onTransformComplete(int mode);// mode 1

        void onTransformStart();
    }

}
