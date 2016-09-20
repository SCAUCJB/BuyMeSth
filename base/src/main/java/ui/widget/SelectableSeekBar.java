package ui.widget;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by John on 2016/8/27.
 */

public class SelectableSeekBar extends View {
    private TextPaint mTextPaint;
    private Paint mSlipperPaint;
    private float mTextWidth;

    private int mSize = 2;
    private int mCircleRadius = 20;
    private float mLineWidth = 8;
    private int mCurrentPos = 0;
    private float mCurrentX=-1;
    private float mVelocity = 1;
    private float mAcceleration = 2;
    private boolean mIsUp;
    private float cy;
    private float[] cx=new float[3];
    private int perWidth;
    private OnStateSelectedListener mListener=null;
    private int mLineColor=Color.GRAY;
    private int mSlipperColor=Color.RED;
    private String[] texts;
    private Paint mLinePaint;
    private float mSlipperRadius=35;

    private ScrollView parent;
    public void setParent(ScrollView parent){
        this.parent=parent;
    }
    public  interface OnStateSelectedListener{
        void onStateSelected(int pos);
    }
    public void setOnStateSelectedListener(OnStateSelectedListener listener){
        mListener=listener;
    }
    public SelectableSeekBar(Context context) {
        super(context);
        init( null, 0);
    }

    public SelectableSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init( attrs, 0);
    }

    public SelectableSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init( attrs, defStyle);
    }



    private void init( AttributeSet attrs, int defStyle) {

        texts=new String[]{"一口价","范围内"};
        mSlipperPaint = new Paint();
        mSlipperPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mSlipperPaint.setColor(mSlipperColor);
        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(mLineColor);
        mLinePaint=new Paint();
        mLinePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        mTextPaint.setTextSize(contentHeight/(mSize+1));
        //draw background
        for (int i = 0; i < mSize; ++i) {
            perWidth = (int) (1.0f * contentWidth / mSize);
            cx[i] = paddingLeft + 1.0f * perWidth / 2 + i * perWidth;
            if(mCurrentX==-1)mCurrentX=cx[i];
            cy = paddingTop + 1.0f * contentHeight / 3;
            canvas.drawCircle(cx[i], cy, mCircleRadius, mLinePaint);
            if (i != mSize - 1) {
                float startX = cx[i] + mCircleRadius;
                float startY = cy;
                float stopX = cx[i] + perWidth - mCircleRadius;
                float stopY = cy;
                mLinePaint.setStrokeWidth(mLineWidth);
                canvas.drawLine(startX, startY, stopX, stopY, mLinePaint);
            }
            if(mCurrentPos==i)mTextPaint.setColor(mSlipperColor);
            else mTextPaint.setColor(Color.BLACK);
            mTextWidth = mTextPaint.measureText(texts[i]);
            canvas.drawText(texts[i],cx[i]-mTextWidth/2,mCircleRadius+15+paddingTop + 2.0f * contentHeight / 3,mTextPaint);
        }
        canvas.drawCircle(mCurrentX, paddingTop + 1.0f * contentHeight / 3, mSlipperRadius, mSlipperPaint);
        int state=(int)(mCurrentX-cx[0])/(perWidth/2);
        mCurrentPos=(state+1)/2;
        if (mIsUp) {
            slipTo(mCurrentPos);
        }
    }
    public int getSelectedPosition(){
        return mCurrentPos;
    }
    private void slipTo(int i) {
        if (mCurrentX > cx[i]) {
            mCurrentX -= mVelocity;
            mVelocity += mAcceleration;
            if (mCurrentX <= cx[i]) {
                mSlipperPaint.setColor(mSlipperColor); mCurrentX = cx[i];
            }
            invalidate();
        } else if(mCurrentX<cx[i]){
            mCurrentX += mVelocity;
            mVelocity += mAcceleration;
            if (mCurrentX >= cx[i]) {
                mSlipperPaint.setColor(mSlipperColor);
                mCurrentX = cx[i];
            }
            invalidate();
        }
        else {
            mVelocity = 1;
            mAcceleration = 2;
            mCurrentPos=i;
            if(mListener!=null){
                mListener.onStateSelected(i);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsUp = false;
                mSlipperPaint.setColor(Color.YELLOW);
                mSlipperRadius+=8;
                parent.requestDisallowInterceptTouchEvent(true);
                mCurrentX = event.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                mIsUp=false;
                mSlipperPaint.setColor(Color.YELLOW);
                mCurrentX = event.getX();
                parent.requestDisallowInterceptTouchEvent(true);
                invalidate();
                return  true;
            case MotionEvent.ACTION_UP:
                mIsUp = true;
                mSlipperRadius-=8;
                parent.requestDisallowInterceptTouchEvent(false);
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }


}
