package base.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;
import java.io.InputStream;

/**
 * Created by John on 2016/8/17.
 */

public class ImageResizer {
    private static final String TAG="ImageResizer";
    public ImageResizer(){

    }
    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,int reqWidth,int reqHeight){
        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeResource(res,resId,options);

        options.inSampleSize=calculateInSampleSize(options,reqWidth,reqHeight);

        options.inJustDecodeBounds=false;
       return  BitmapFactory.decodeResource(res,resId,options);
    }

    public Bitmap decodeSampledBitmapFromFIleDescriptor(FileDescriptor fd,int reqWidth,int reqHeight){
        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFileDescriptor(fd,null,options);

        options.inSampleSize=calculateInSampleSize(options,reqWidth,reqHeight);

        options.inJustDecodeBounds=false;
        return  BitmapFactory.decodeFileDescriptor(fd,null,options);
    }
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if(reqHeight==0||reqWidth==0)
            return 1;
        //拿到测量出来的图片宽高
        final int height= options.outHeight;
        final int width= options.outWidth;
        int inSampleSize=1;

        if(height>reqHeight||width>reqWidth){
            final int halfHeight=height/2;
            final int halfWidth=width/2;
            while((halfHeight/inSampleSize)>=reqHeight&&(halfWidth/inSampleSize)>=reqWidth){
                inSampleSize*=2;
            }
        }
        return inSampleSize;
    }
}
