package ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import edu.scau.base.R;

public class ChangeColorIconWithTextView extends View
{

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mPaint;
	/**
	 * 颜色
	 */
	private int mColor = 0xFF45C01A;
	/**
	 * 透明度 0.0-1.0
	 */
	private float mAlpha = 0f;
	/**
	 * 图标
	 */
	private Bitmap mIconBitmap;
	/**
	 * 限制绘制icon的范围,实例化放在生成对象的时候而不是在measure时候
	 */
	private Rect mIconRect=new Rect();


	private Paint mTextPaint;
	private Rect mTextBound = new Rect();

	public ChangeColorIconWithTextView(Context context)
	{
		super(context);

	}



	/**
	 * 初始化自定义属性值
	 * 
	 * @param context
	 * @param attrs
	 */
	public ChangeColorIconWithTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		// 获取设置的图标
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ChangeColorIconView);

		int n = a.getIndexCount();
		for (int i = 0; i < n; i++)
		{

			int attr = a.getIndex(i);
			if (attr == R.styleable.ChangeColorIconView_icon1) {
				BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
				mIconBitmap = drawable.getBitmap();

			} else if (attr == R.styleable.ChangeColorIconView_color1) {
				mColor = a.getColor(attr, 0x45C01A);

			}
		}

		a.recycle();



	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 得到绘制icon的宽
		int bitmapWidth;
            bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
                    - getPaddingRight(), getMeasuredHeight() - getPaddingTop()
                    - getPaddingBottom());

		int left = getMeasuredWidth() / 2 - bitmapWidth / 2;
		int top;
            top = getMeasuredHeight() / 2 - bitmapWidth/ 2;

		// 设置icon的绘制范围
		mIconRect.set(left, top, left + bitmapWidth, top + bitmapWidth);

	}

	@Override
	protected void onDraw(Canvas canvas)
	{

		int alpha = (int) Math.ceil((255 * mAlpha));
		canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
		setupTargetBitmap(alpha);
        canvas.drawBitmap(mBitmap, 0, 0, null);

	}
	
	private void setupTargetBitmap(int alpha)
	{
		mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
				Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPaint = new Paint();
		mPaint.setColor(mColor);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setAlpha(alpha);
		mCanvas.drawRect(mIconRect, mPaint);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mPaint.setAlpha(255);
		mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
	}

	public void setIconAlpha(float alpha)
	{
		this.mAlpha = alpha;
		invalidateView();
	}

    private void invalidateView()
	{
		if (Looper.getMainLooper() == Looper.myLooper())
		{
			invalidate();
		} else
		{
			postInvalidate();
		}
	}

	public void setIconColor(int color)
	{
		mColor = color;
	}

	public void setIcon(int resId)
	{
		this.mIconBitmap = BitmapFactory.decodeResource(getResources(), resId);
		if (mIconRect != null)
			invalidateView();
	}

	public void setIcon(Bitmap iconBitmap)
	{
		this.mIconBitmap = iconBitmap;
		if (mIconRect != null)
			invalidateView();
	}


	private static final String INSTANCE_STATE = "instance_state";
	private static final String STATE_ALPHA = "state_alpha";

	@Override
	protected Parcelable onSaveInstanceState()
	{
		Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
		bundle.putFloat(STATE_ALPHA, mAlpha);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		if (state instanceof Bundle)
		{
			Bundle bundle = (Bundle) state;
			mAlpha = bundle.getFloat(STATE_ALPHA);
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
		} else
		{
			super.onRestoreInstanceState(state);
		}

	}

}
