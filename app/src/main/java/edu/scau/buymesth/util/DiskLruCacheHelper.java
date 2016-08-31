package edu.scau.buymesth.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
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
    public static String getAsString(String key, DiskLruCache diskLruCache){
        FileInputStream fileInputStream=null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(hashKeyFormUrl(key));
            if(snapshot!=null){
                fileInputStream = (FileInputStream) snapshot.getInputStream(Constant.DISK_CACHE_INDEX);
                StringBuilder sb = new StringBuilder();
                int len = -1;
                byte[] buf = new byte[128];
                while ((len = fileInputStream.read(buf)) != -1) {
                    sb.append(new String(buf, 0, len));
                }
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
//    public static File getAsFile(String key, DiskLruCache diskLruCache){
//        FileInputStream fis=null;
//        try {
//            DiskLruCache.Snapshot snapshot = diskLruCache.get(hashKeyFormUrl(key));
//            if(snapshot!=null){
//                fis = (FileInputStream) snapshot.getInputStream(Constant.DISK_CACHE_INDEX);
//                File file;
//                OutputStream os = new FileOutputStream(file);
//                int bytesRead = 0;
//                byte[] buffer = new byte[1024];
//                while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
//                    os.write(buffer, 0, bytesRead);
//                }
//                os.close();
//                fis.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if(fis!=null)
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//        }
//        return null;
//    }
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
        } catch (IOException e) {
            e.printStackTrace();
            try {
                //s
                editor.abort();//write REMOVE
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
    public static void put(String key, InputStream inputStream,DiskLruCache diskLruCache) {
        DiskLruCache.Editor editor = null;
        BufferedWriter bw = null;
        key=hashKeyFormUrl(key);
        try {
            editor = diskLruCache.edit(key);
            if (editor == null) return;
            OutputStream os =  editor.newOutputStream(0);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[1024];
            int size = -1;
            while ((size = bis.read(buffer)) != -1) {
                os.write(buffer, 0, size);
            }
            os.flush();
            os.close();
            bis.close();
            editor.commit();//write CLEAN
        } catch (IOException e) {
            e.printStackTrace();
            try {
                //s
                editor.abort();//write REMOVE
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
    public static void put(String key, Serializable value,DiskLruCache diskLruCache) {
        DiskLruCache.Editor editor = null;
        ObjectOutputStream oos = null;
        key=hashKeyFormUrl(key);
        try {
            if (editor == null) return;
            editor = diskLruCache.edit(key);
            OutputStream os = editor.newOutputStream(0);
            oos = new ObjectOutputStream(os);
            oos.writeObject(value);
            oos.flush();
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (oos != null)
                    oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> T getAsSerializable(String key,DiskLruCache diskLruCache) {
        T t = null;
        key=hashKeyFormUrl(key);

        ObjectInputStream ois = null;
        InputStream is=null;
        try {
            DiskLruCache.Snapshot snapshot =diskLruCache.get(key);
            if(snapshot==null)return null;
            is= snapshot.getInputStream(0);
            if (is == null) return null;
            ois = new ObjectInputStream(is);
            t = (T) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null)
                    ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return t;
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
