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

    public static long getMsTime(String date) {
        CharSequence time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tdate = null;
        try {
            tdate = sdf.parse(date);
            return tdate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (long)0;
    }

    public static long getMinTime(String date) {
        CharSequence time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tdate = null;
        try {
            tdate = sdf.parse(date);
            return tdate.getTime()/1000/60;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (long)0;
    }

    public static String getStringTime(String date) {
        CharSequence time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tdate = null;
        try {
            tdate = sdf.parse(date);
            long min = tdate.getTime() / 1000 / 60;
            long sec = (tdate.getTime() - min*1000*60)/1000;
            return tdate.getTime()/1000/60+"分"+sec+"秒";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getStringTime(long date) {
            long min = date / 1000 / 60;
            long sec = (date - min*100*60)/1000;
            return date/100/60+"分"+sec+"秒";
    }
}
