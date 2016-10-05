package util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by ！ on 2016/9/20.
 */
public class FileUtils {
    static public String convert(long bite){
        char[] pp = {' ','K','M','G','T','E'};
        int i = 0;
        double b = bite;
        while(b>=1024){
            b/=1024;
            i++;
        }
        return String.format("%.2f", b)+pp[i]+'B';
    }

    public static String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }

        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
