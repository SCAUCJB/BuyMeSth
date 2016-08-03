package cache;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by John on 2016/8/3.
 * 磁盘缓存管理器
 */

public class DiskCacheManager implements IDiskCacheManage {
    private static volatile DiskCacheManager instance;
    private static final int EXTERNAL_STORAGE_REQ_CODE = 10;
    private File cacheDir;

    private DiskCacheManager() {

    }

    public static DiskCacheManager getInstance() {
        if (instance == null) {
            synchronized (DiskCacheManager.class) {
                if (instance == null)
                    instance = new DiskCacheManager();
            }
        }
        return instance;
    }

    /**
     * android 6.0的运行时请求权限
     */
    private static void requestWritePermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(activity)
                        .setMessage("没有写权限则无法读缓存，这样无法为您节省流量")
                        .setPositiveButton("OK", (dialog, which) -> {
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    EXTERNAL_STORAGE_REQ_CODE);
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
                return;
            }
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQ_CODE);

        }
    }

    /**
     * android 6.0的运行时请求权限
     */
    private static void requestReadPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(activity)
                        .setMessage("没有读权限则无法读缓存，这样无法为您节省流量")
                        .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(activity,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                EXTERNAL_STORAGE_REQ_CODE))
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
                return;
            }

            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQ_CODE);

        }
    }

    /**
     * 初始化缓存目录，如果是安卓6.0 则需要请求写文件权限。
     *
     * @param context context
     */
    public void initCache(Context context) {
        cacheDir = context.getCacheDir();
    }

    /**
     * 写文件到缓存目录下，可以自己建立目录，这样方便查找
     *
     * @param fileName 文件名
     * @param folder   文件夹
     * @param data     数据
     * @param <T>      类型
     */
    @Override
    public <T extends Serializable> void put(String fileName, String folder, T data) {
        if (cacheDir != null) {
            File file;
            if (!folder.isEmpty()) {
                File fileDir = new File(cacheDir, folder);
                if (!fileDir.exists() || !fileDir.isDirectory()) {
                    if (!fileDir.mkdir()) return;
                }
                file = new File(fileDir, fileName);
            } else {
                file = new File(cacheDir, fileName);
            }
            if (file.exists()) {
                if (!file.delete()) return;
            }

            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                oos.writeObject(data);
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public <T> T get(String fileName, String folder) {
        T data = null;
        if (cacheDir != null) {
            File file;
            if (!folder.isEmpty()) {
                File fileDir = new File(cacheDir, folder);
                if (!fileDir.exists() || !fileDir.isDirectory()) {
                    if (!fileDir.mkdir()) return null;
                }
                file = new File(fileDir, fileName);
            } else {
                file = new File(cacheDir, fileName);
            }
            if (file.exists()) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                    data = (T) ois.readObject();
                    ois.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }


    @Override
    public void clear() {

    }
}
