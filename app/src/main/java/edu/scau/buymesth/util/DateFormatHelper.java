package edu.scau.buymesth.util;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IamRabbit on 2016/8/26.
 */
public class DateFormatHelper {
    public static String dateFormat(String date){
        CharSequence time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date tdate = sdf.parse(date);
            time = DateUtils.getRelativeTimeSpanString(tdate.getTime());
            return time.toString();
        } catch (ParseException e) {
            return date;
        } catch (NullPointerException e){
            return date;
        }
    }

    public static String dateFormat(long date){
        CharSequence time;
        time = DateUtils.getRelativeTimeSpanString(date);
        return time.toString();
    }
}
