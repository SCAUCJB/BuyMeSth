package ui.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import edu.scau.base.R;

/**
 * Created by ！ on 2016/9/12.
 */
public class NineGridLayout extends ViewGroup {

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_TEXT = 1;

    private static final int DEFUALT_COLUMN = 3;
    private static final float DEFUALT_SPACING = 3f;
    private float mSpacing = DEFUALT_SPACING;
    private List<String> mUrlList = new ArrayList<>();
    private int mLayoutHeight;
    private int mLayoutWidth;//useless
    private float mSingleWidth = 90;//单个imageview的宽度
    private float mSingleHeight = 90;//单个imageview的高度
    private int mMaxColumn = DEFUALT_COLUMN;//3
    private int mMaxRow = DEFUALT_COLUMN;//3
    private int mColumn = DEFUALT_COLUMN;//3
    private int mRow = 0;

    private boolean mSetOneImageLayout = false;
    private int mOneImageHeight = -1;
    private int mOneImageWidth = -1;

    private boolean mChildrenAdded = false;
    private boolean mShowAll = false;

    private OnItemClickListener onItemClickListener;

    public NineGridLayout(Context context) {
        super(context);
    }

    public NineGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NineGridLayout);

        mSpacing = typedArray.getDimension(R.styleable.NineGridLayout_sapcing, DEFUALT_SPACING);
        typedArray.recycle();
    }

    private int getListSize(List<String> list) {
        if (list == null || list.size() == 0)
            return 0;
        else
            return list.size();
    }

    private int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        System.out.println("!!!!!"+"onMeasure");
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        if(widthMode != MeasureSpec.AT_MOST){
            mSingleWidth = (sizeWidth-mSpacing*(getmMaxColumn() -1))/(float) getmMaxColumn();
            mSingleHeight = mSingleWidth;
        }

        //根据子url数量确定高度
        int size = getListSize(mUrlList);
        if(!mShowAll&&size> getmMaxColumn() * getmMaxRow())size = getmMaxColumn() * getmMaxRow();
        if(size==1){
            if(!mSetOneImageLayout){
                mOneImageHeight = (int) mSingleHeight;
                mOneImageWidth = (int) mSingleWidth;
            }
            mLayoutHeight = mOneImageHeight;
        }
        else {
            while(size>mRow* getmMaxColumn()){
                mRow++;
            }
            mLayoutHeight = (int) (mRow * mSingleHeight + (mRow-1)*mSpacing);
        }

        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : sizeWidth, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : mLayoutHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        System.out.println("!!!!!"+"onLayout");
        if(mChildrenAdded)return;
        if(getListSize(mUrlList)==0){
            removeAllViews();
            return;
        }
        mChildrenAdded = true;
        removeAllViews();
        int childLeft = 0, childTop = 0, childRight = 0, childBottom = 0;
        if(getListSize(mUrlList) == 1){
            ImageView imageView = createImageView(0,mUrlList.get(0));
            imageView.layout(0, 0,mOneImageWidth , mOneImageHeight);
            addView(imageView);
            displayImage(imageView,mUrlList.get(0));
            return;
        }
        for(int i = 0;i<getListSize(mUrlList);i++){
            if(!ismShowAll() &&i>= getmMaxColumn() * getmMaxRow())break;
            ImageView imageView = createImageView(i,mUrlList.get(i));
            //计算位置
            childLeft = (int) ((mSingleWidth+mSpacing)*(i% getmMaxColumn()));
            childRight = (int) (childLeft + mSingleWidth);
            int row = 0;
            while(row* getmMaxColumn() <i+1)row++;
            childTop = (int) ((mSingleHeight+mSpacing)*(row-1));
            childBottom = (int) (childTop+mSingleHeight);
            //设置
            imageView.layout(childLeft, childTop, childRight, childBottom);
            addView(imageView);
            displayImage(imageView,mUrlList.get(i));
        }
        if(!ismShowAll() && getmMaxColumn() * getmMaxRow() <getListSize(mUrlList)){//超出可显示的数量
            int overCount = getListSize(mUrlList) - getmMaxColumn() * getmMaxRow();
            float textSize = 30;
            TextView textView = new TextView(getContext());
            textView.setOnClickListener(v -> {
                if(getOnItemClickListener() !=null)
                    getOnItemClickListener().onClick(v,getmMaxColumn() * getmMaxRow(),null,TYPE_IMAGE);
                showAll();
            });
            textView.setText("+" + String.valueOf(overCount));
            textView.setTextColor(Color.WHITE);
            textView.setPadding(0, (int) (mSingleHeight / 2 - getFontHeight(textSize)), 0, 0);
            textView.setTextSize(textSize);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.BLACK);
            textView.getBackground().setAlpha(120);
            //设置
            textView.layout(childLeft, childTop, childRight, childBottom);
            addView(textView);
        }
    }

    public void showAll(){
        setmShowAll(true);
        mChildrenAdded = false;
        requestLayout();
    }

    protected ImageView createImageView(final int i, final String url) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(v -> {
            if(getOnItemClickListener() !=null)
                getOnItemClickListener().onClick(v,i,mUrlList,TYPE_IMAGE);
        });
        return imageView;
    }

    protected void displayImage(ImageView imageView, String url){
        Glide.with(getContext()).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public void setUrlList(List<String> urls){
        this.mUrlList.clear();
        this.mUrlList.addAll(urls);
        if(getListSize(this.mUrlList)>0){
            setVisibility(VISIBLE);
            System.out.println("!!!!!"+"setting URL");
        }
        else setVisibility(GONE);
        initData();
        requestLayout();
    }

    private void initData(){
        mChildrenAdded = false;
        mRow = 0;
        mColumn = 0;
        mOneImageHeight = -1;
        mOneImageWidth = -1;
    }

    public int getmMaxColumn() {
        return mMaxColumn;
    }

    public void setmMaxColumn(int mMaxColumn) {
        this.mMaxColumn = mMaxColumn;
    }

    public int getmMaxRow() {
        return mMaxRow;
    }

    public void setmMaxRow(int mMaxRow) {
        this.mMaxRow = mMaxRow;
    }

    public boolean ismShowAll() {
        return mShowAll;
    }

    public void setmShowAll(boolean mShowAll) {
        this.mShowAll = mShowAll;
    }

    public void setOneImageLayout(int width,int height){
        System.out.println("!!!!!"+"set layout");
        mOneImageWidth = width;
        mOneImageHeight = height;
        mSetOneImageLayout = true;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onClick(View view,int position,List<String> urls,int itemType);
    }
}