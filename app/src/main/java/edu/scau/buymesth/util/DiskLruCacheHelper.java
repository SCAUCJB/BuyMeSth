package edu.scau.buymesth.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import cache.lru.DiskLruCache;
import edu.scau.Constant;

/**
 * Created by John on 2016/8/24.
 */

public class DiskLruCacheHelper {

    public static DiskLruCache create(Context context,String directorName){
        File diskCacheDir = getDiskCacheDir(context, directorName!=null?directorName:"default");
        if (!diskCacheDir.exists())
            diskCacheDir.mkdirs();
        try {
            return DiskLruCache.open(diskCacheDir, Constant.APP_VERSION, 1, Constant. DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static File getDiskCacheDir(Context context, String name) {
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath = externalStorageAvailable ? context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();
        return new File(cachePath + File.separator + name);

    }
    public static String get(String key,DiskLruCache diskLruCache){
        FileInputStream fileInputStream=null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(hashKeyFormUrl(key));
            if(snapshot!=null){
                 fileInputStream = (FileInputStream) snapshot.getInputStream(Constant.DISK_CACHE_INDEX);
                StringBuilder sb = new StringBuilder();
                int len = 0;
                byte[] buf = new byte[128];
                while ((len = fileInputStream.read(buf)) != -1) {
                    sb.append(new String(buf, 0, len));
                }
                Log.d("zhx","from cache");
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fileInputStream!=null)
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
    public static void put(String key, String value,DiskLruCache diskLruCache) {
        DiskLruCache.Editor editor = null;
        BufferedWriter bw = null;
        key=hashKeyFormUrl(key);
        try {
            editor = diskLruCache.edit(key);
            if (editor == null) return;
            OutputStream os = editor.newOutputStream(0);
            bw = new BufferedWriter(new OutputStreamWriter(os));
            bw.write(value);
            editor.commit();//write CLEAN
            Log.d("zhx","put to cache success,value="+value);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                //s
                editor.abort();//write REMOVE
                Log.d("zhx","put to cache success,fail");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (bw != null)
                    bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static  String hashKeyFormUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(url.getBytes());
            cacheKey = bytesToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
