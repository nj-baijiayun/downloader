package com.nj.baijiayun.downloader.helper;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;

/**
 * @project zywx_android
 * @class name：com.nj.baijiayun.module_common.helper
 * @describe
 * @anthor houyi QQ:1007362137
 * @time 2019-05-23 22:13
 * @change
 * @time
 * @describe
 */
public class FileStorageManager {
    /**
     * 视频下载目录
     */
    public static String getVideoDownLoadPath(Context context) {
        String videoFiles = "VideoFiles";
        return getExternalFilePath(context, videoFiles);
    }

    /**
     * 照片下载目录
     */
    public static String getPicturesDir(Context context) {
        return getExternalFilePath(context, Environment.DIRECTORY_PICTURES);
    }

    @Nullable
    private static String getExternalFilePath(Context context, String dirName) {
        String mCacheDirPath = null;
        File cacheDir = context.getExternalFilesDir(dirName);
        if (null != cacheDir) {
            mCacheDirPath = cacheDir.getAbsolutePath();
        }
        if (TextUtils.isEmpty(mCacheDirPath)) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                mCacheDirPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName() + "/files/" + dirName;
            }
        }
        return mCacheDirPath;
    }

    private static String getExternalCachePath(Context context) {
        String mCacheDirPath = null;
        File cacheDir = context.getExternalCacheDir();
        if (null != cacheDir) {
            mCacheDirPath = cacheDir.getAbsolutePath();
        }
        if (TextUtils.isEmpty(mCacheDirPath)) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                mCacheDirPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName() + "/cache";
            }
        }
        return mCacheDirPath;
    }

    private static String getInnerCachePath(Context context) {
        String mCacheDirPath = null;
        File cacheDir = context.getCacheDir();
        if (null != cacheDir) {
            mCacheDirPath = cacheDir.getAbsolutePath();
        }
        if (TextUtils.isEmpty(mCacheDirPath)) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                mCacheDirPath = Environment.getDataDirectory().getPath() + "/data/" + context.getPackageName() + "/cache";
            }
        }
        return mCacheDirPath;
    }


    /**
     * APK下载目录
     */
    public static String getApkDir(Context context) {
        return getExternalFilePath(context, "Apks");
    }

    /**
     * 文库下载目录
     */
    public static String getLibraryDir(Context context) {
        return getExternalFilePath(context, "Librarys");
    }

    /**
     * 课件下载目录
     */
    public static String getCourseFileDir(@NonNull Context context) {
        return getExternalFilePath(context, "CourseFile");
    }

    /**
     * 课件下载目录
     */
    public static String getCacheDir(Context context) {
        return getExternalCachePath(context);
    }

    /**
     * 获取内部缓存
     */
    public static String getInnerCacheDir(Context context) {
        return getInnerCachePath(context);
    }

}
