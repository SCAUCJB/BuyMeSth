package edu.scau.buymesth.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by IamRabbit on 2016/8/18.
 */
public class ColorChangeHelper {

    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    public static int[] IntToRGB(int i){
        int[] RGB = new int[3];
        RGB[0]=1;
        RGB[1]=255;
        RGB[2]=1;
        int position=0;
        while(i>0){
            int sign;
            if(position%2==0)sign = 1;
            else sign = -1;
            RGB[position%3] = RGB[position%3] + i * sign;
            if(i>254){
                i=i-254;
            }else {
                break;
            }
            position++;
        }
        return RGB;
    }

    public static int IntToColorValue(int i){
        int[] RGB = new int[3];
        RGB[0]=1;
        RGB[1]=255;
        RGB[2]=1;
        int position=0;
        while(i>0){
            int sign;
            if(position%2==0)sign = 1;
            else sign = -1;
            RGB[position%3] = RGB[position%3] + i * sign;
            if(i>254){
                i=i-254;
            }else {
                break;
            }
            position++;
        }
        return Color.rgb(RGB[0],RGB[1],RGB[2]);
    }
}
