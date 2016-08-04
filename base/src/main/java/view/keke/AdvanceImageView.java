package view.keke;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import edu.scau.base.R;

/**
 * Created by IamRabbit on 2016/8/1.
 */
public class AdvanceImageView extends ImageView{
    public AdvanceImageView(Context context) {
        super(context);
    }

    public AdvanceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AdvanceImageView);
        String stringUrl = ta.getString(R.styleable.AdvanceImageView_imageUrl);
        ta.recycle();
        System.out.println("adimageview getting atrr");
        if(stringUrl!=null){
            System.out.println("adimageview setting image");
            setImageUrl(stringUrl);
        }
    }

    public AdvanceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AdvanceImageView);
        String stringUrl = ta.getString(R.styleable.AdvanceImageView_imageUrl);
        ta.recycle();
        System.out.println("adimageview getting atrr");
        if(stringUrl!=null){
            System.out.println("adimageview setting image");
            setImageUrl(stringUrl);
        }
    }

    private URL imageUrl;
    private Bitmap bitmap;
    private ObjectAnimator objectAnimator;
    private OnImageDownloadLinstener imageDownloadLinstener;

    public void setImageUrl(String imageUrl){
        try {
            this.imageUrl = conversionUrl(new URL(imageUrl));
            getBitmapFromInternet();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setImageUrl(URL imageUrl){
        this.imageUrl = conversionUrl(imageUrl);
        getBitmapFromInternet();
    }

    public URL conversionUrl(URL sourceUrl){
        return sourceUrl;
    }

    public Bitmap getBitmapFromInternet(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                try {
                    //开启连接
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    //设置超时的时间，5000毫秒即5秒
                    conn.setConnectTimeout(5000);
                    //设置获取图片的方式为GET
                    conn.setRequestMethod("GET");
                    //响应码为200，则访问成功
                    if (conn.getResponseCode() == 200) {
                        //获取连接的输入流，这个输入流就是图片的输入流
                        is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        //图片已经下载成功
                        System.out.println("image download succeed");
                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setImageBitmap(bitmap);
                            }
                        });
                        if(imageDownloadLinstener!=null){
                            imageDownloadLinstener.onFinish();
                        }
                    }
                } catch (Exception e) {
                    //图片已经下载失败
                    System.out.println("image download fail");
                } finally {
                    //在最后，将各种流关闭
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
        return null;
    }

    public OnImageDownloadLinstener getOnImageDownloadLinstener() {
        return imageDownloadLinstener;
    }

    public void setOnImageDownloadLinstener(OnImageDownloadLinstener imageDownloadLinstener) {
        this.imageDownloadLinstener = imageDownloadLinstener;
    }

    public interface OnImageDownloadLinstener{
        public void onStart();
        public void onFinish();
    }
}
