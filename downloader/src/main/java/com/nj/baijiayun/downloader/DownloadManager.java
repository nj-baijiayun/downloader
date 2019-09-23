package com.nj.baijiayun.downloader;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;

import com.arialyy.aria.core.Aria;
import com.baijiayun.BJYPlayerSDK;
import com.baijiayun.download.DownloadTask;
import com.nj.baijiayun.logger.log.Logger;
import com.nj.baijiayun.downloader.adapter.FileDownloadAdapter;
import com.nj.baijiayun.downloader.adapter.VideoDownloadAdapter;
import com.nj.baijiayun.downloader.config.DownConfig;
import com.nj.baijiayun.downloader.config.DownloadRealmWrapper;
import com.nj.baijiayun.downloader.core.FileDownloadManager;
import com.nj.baijiayun.downloader.core.UpdateDispatcher;
import com.nj.baijiayun.downloader.core.UpdateProcessor;
import com.nj.baijiayun.downloader.core.VideoDownloadManager;
import com.nj.baijiayun.downloader.realmbean.DownloadItem;
import com.nj.baijiayun.downloader.request.DownloadRequest;
import com.nj.baijiayun.downloader.request.FileDownloadRequest;
import com.nj.baijiayun.downloader.request.VideoDownloadRequest;

import java.util.List;


/**
 * @author houyi
 */
public class DownloadManager {

    private final VideoDownloadAdapter videoDownloadAdapter;

    /**
     * 下载类型
     */
    public enum DownloadType {
        /**
         * 1:课件文件，2:文库文件，3:普通回放视频,31:小班课回放，4:点播视频,41:点播音频,5:图文课程,6文件视频（阿里云或其他途径）
         * 7:音频文件
         */
        TYPE_COURSE_WAVE(1),
        TYPE_LIBRARY(2),
        TYPE_PLAY_BACK(3),
        TYPE_PLAY_BACK_SMALL(31),
        TYPE_VIDEO(4),
        TYPE_VIDEO_AUDIO(41),
        TYPE_FILE_GRAPHIC(5),
        TYPE_FILE_VIDEO(6),
        TYPE_FILE_AUDIO(7),
        TYPE_CUSTOM(8);

        private final int type;

        DownloadType(int type) {
            this.type = type;
        }

        public int value() {
            return type;
        }

        public static Integer[] toIntArray(DownloadType[] downloadTypes) {
            Integer[] integers = new Integer[downloadTypes.length];
            for (int i = 0; i < downloadTypes.length; i++) {
                integers[i] = downloadTypes[i].type;
            }
            return integers;
        }

        public static DownloadType getDownloadType(int type) {
            switch (type) {
                case 1:
                    return TYPE_COURSE_WAVE;
                case 2:
                    return TYPE_LIBRARY;
                case 3:
                    return TYPE_PLAY_BACK;
                case 31:
                    return TYPE_PLAY_BACK_SMALL;
                case 4:
                    return TYPE_VIDEO;
                case 41:
                    return TYPE_VIDEO_AUDIO;
                case 5:
                    return TYPE_FILE_GRAPHIC;
                case 6:
                    return TYPE_FILE_VIDEO;
                case 7:
                    return TYPE_FILE_AUDIO;
                case 8:
                    return TYPE_CUSTOM;
                default:
                    throw new IllegalArgumentException("no this type " + type);
            }
        }

    }


    /**
     * 下载状态
     */
    public enum DownloadStatus {
        /**
         * 1，完成；2，下载中；3，等待下载（等待线程或者其他情况）；
         * 4，停止/暂停；5，失败
         */
        STATUS_COMPLETE(1),
        STATUS_DOWNLOADING(2),
        STATUS_WAITING(3),
        STATUS_STOP(4),
        STATUS_ERROR(5);

        private final int type;

        DownloadStatus(int type) {
            this.type = type;
        }

        public int value() {
            return type;
        }

        public static Integer[] toIntArray(DownloadStatus[] downloadStatuses) {
            Integer[] integers = new Integer[downloadStatuses.length];
            for (int i = 0; i < downloadStatuses.length; i++) {
                integers[i] = downloadStatuses[i].type;
            }
            return integers;
        }

        public static DownloadStatus getStatusType(int type) {
            switch (type) {
                case 1:
                    return STATUS_COMPLETE;
                case 2:
                    return STATUS_DOWNLOADING;
                case 3:
                    return STATUS_WAITING;
                case 4:
                    return STATUS_STOP;
                case 5:
                    return STATUS_ERROR;
                default:
                    throw new IllegalArgumentException("no this type " + type);
            }
        }

    }

    private static DownConfig config;
    private final VideoDownloadManager videoDownloadManager;
    private static boolean init = false;
    private static DownloadManager instance;
    private FileDownloadManager fileDownloadManager;
    private FileDownloadAdapter fileDownloadAdapter;
    private UpdateProcessor updateProcessor;
    private UpdateDispatcher updateDispatcher;

    private DownloadManager() {
        fileDownloadManager = new FileDownloadManager(config.getFilePath());
        videoDownloadManager = new VideoDownloadManager(config.getContext(), config.getVideoPath());
        fileDownloadAdapter = new FileDownloadAdapter();
        videoDownloadAdapter = new VideoDownloadAdapter();
        updateDispatcher = new UpdateDispatcher();
        updateProcessor = new UpdateProcessor(fileDownloadManager, videoDownloadManager, updateDispatcher, config.getUid());
    }

    /**
     * 初始化
     *
     * @param downConfig 初始化配置
     */
    public static void init(DownConfig downConfig) {
        downConfig.getContext().registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
                    if (instance != null) {
                        instance.onTrimMemory();
                    }
                }
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {

            }

            @Override
            public void onLowMemory() {

            }
        });
        init = true;
        new BJYPlayerSDK.Builder((Application) downConfig.getContext())
                .setDevelopMode(false)
                .setEncrypt(true)
                .build();
        config = downConfig;
        Aria.init(downConfig.getContext());
        RealmManager.init(downConfig.getContext());
    }

    /**
     * 设置当前用户的uid
     *
     * @param uid uid指定唯一用户，不同用户的下载互不干扰
     */
    public static void updateUid(String uid) {
        config.setUid(uid);
        if (init) {
            getInstance().notifyUidChanged();
        }
    }

    private void notifyUidChanged() {
        videoDownloadManager.updateDownloadPath(config.getUid(), config.getVideoPath());
        fileDownloadManager.updateFilePath(config.getFilePath());
        updateProcessor.updateUid(config.getUid());
    }

    /**
     * 获取当前的用户uid
     */
    public static String getCurrentUid() {
        return config.getUid();
    }

    private void onTrimMemory() {
        if (fileDownloadManager != null) {
            fileDownloadManager.unregisterCallback();
        }
    }

    private DownloadRealmWrapper getDownloadInfo(LifecycleOwner lifecycleOwner, String parentId, DownloadType[] downloadTypes, Integer[] downloadStatus) {
        return updateDispatcher.registerDispatcher(lifecycleOwner, parentId, downloadTypes, downloadStatus);
    }

    /**
     * 下载
     *
     * @param type 下载类型
     * @return 下载的请求
     */
    public static DownloadRequest downloadFile(DownloadType type) {
        checkInit();
        return getInstance().makeRequest(type);

    }

    private DownloadRequest makeRequest(DownloadType type) {
        switch (type) {
            case TYPE_VIDEO:
            case TYPE_PLAY_BACK:
                return new VideoDownloadRequest(type, config.getUid(), updateProcessor, videoDownloadManager, videoDownloadAdapter);
            default:
                return new FileDownloadRequest(type, config.getUid(), updateProcessor, fileDownloadManager, fileDownloadAdapter);
        }
    }

    private static DownloadManager getInstance() {
        if (instance == null) {
            synchronized (DownloadManager.class) {
                if (instance == null) {
                    instance = new DownloadManager();
                    init = true;
                }
            }
        }
        return instance;
    }

    /**
     * @see #getAllDownloadInfo(LifecycleOwner, String, DownloadType[], DownloadStatus[])
     */
    public static DownloadRealmWrapper getAllDownloadInfo(LifecycleOwner lifecycleOwner) {
        return getAllDownloadInfo(lifecycleOwner, null, null, ((Integer[]) null));
    }

    /**
     * @see #getAllDownloadInfo(LifecycleOwner, String, DownloadType[], DownloadStatus[])
     */
    public static DownloadRealmWrapper getAllDownloadInfo(LifecycleOwner lifecycleOwner, String courseId) {
        return getAllDownloadInfo(lifecycleOwner, courseId, null, ((Integer[]) null));
    }

    /**
     * @see #getAllDownloadInfo(LifecycleOwner, String, DownloadType[], DownloadStatus[])
     */
    public static DownloadRealmWrapper getAllDownloadInfo(LifecycleOwner lifecycleOwner, DownloadType[] downloadTypes) {
        return getAllDownloadInfo(lifecycleOwner, null, downloadTypes, ((Integer[]) null));
    }


    /**
     * @see #getAllDownloadInfo(LifecycleOwner, String, DownloadType[], Integer[])
     * @deprecated Use {@link #getAllDownloadInfo(LifecycleOwner, DownloadStatus[])}instead.
     * this method will be removed next version
     */
    @Deprecated
    public static DownloadRealmWrapper getAllDownloadInfo(LifecycleOwner lifecycleOwner, Integer[] downloadStatus) {
        return getAllDownloadInfo(lifecycleOwner, null, null, downloadStatus);
    }

    /**
     * @see #getAllDownloadInfo(LifecycleOwner, String, DownloadType[], DownloadStatus[])
     */
    public static DownloadRealmWrapper getAllDownloadInfo(LifecycleOwner lifecycleOwner, DownloadStatus[] downloadStatus) {
        return getAllDownloadInfo(lifecycleOwner, null, null, downloadStatus);
    }


    /**
     * 获取数据库下载列表
     *
     * @param lifecycleOwner 用于监听生命周期回调
     * @param parentId       用户下载时设置的parentId，一般为课程或文库Id
     * @param downloadTypes  下载的类型、可多选，null为全选
     * @param downloadStatus 下载的状态、可多选，null为全选
     * @return DownloadRealmWrapper 结果列表封装类
     * @deprecated Use {@link #getAllDownloadInfo(LifecycleOwner, String, DownloadType[], DownloadStatus[])}instead.
     * this method will be removed next version
     */
    @Deprecated
    public static DownloadRealmWrapper getAllDownloadInfo(LifecycleOwner lifecycleOwner, String parentId, DownloadType[] downloadTypes, Integer[] downloadStatus) {
        return getInstance().getDownloadInfo(lifecycleOwner, parentId, downloadTypes, downloadStatus);
    }

    /**
     * 获取数据库下载列表
     *
     * @param lifecycleOwner 用于监听生命周期回调
     * @param parentId       用户下载时设置的parentId，一般为课程或文库Id
     * @param downloadTypes  下载的类型、可多选，null为全选
     * @param downloadStatus 下载的状态、可多选，null为全选
     * @return DownloadRealmWrapper 结果列表封装类
     */
    public static DownloadRealmWrapper getAllDownloadInfo(LifecycleOwner lifecycleOwner, String parentId, DownloadType[] downloadTypes, DownloadStatus[] downloadStatus) {
        return getAllDownloadInfo(lifecycleOwner, parentId, downloadTypes, DownloadStatus.toIntArray(downloadStatus));
    }

    /**
     * 暂停下载
     *
     * @param item
     */
    public static void pauseDownload(DownloadItem item) {
        getInstance().pauseDownloadInner(item);
    }

    /**
     * 恢复下载
     *
     * @param item
     */
    public static void resumeDownload(DownloadItem item) {
        getInstance().resumeDownloadInner(item);
    }

    private void resumeDownloadInner(DownloadItem item) {
        updateProcessor.resumeDownload(item);
    }


    public static void delete(List<DownloadItem> deleteItems) {
        for (DownloadItem deleteItem : deleteItems) {
            try {
                getInstance().deleteItem(deleteItem);
            } catch (Throwable throwable) {
                Logger.e(throwable);
            }
        }
    }

    private void pauseDownloadInner(DownloadItem item) {
        updateProcessor.pauseDownload(item);
    }

    private void deleteItem(DownloadItem deleteItem) {
        updateProcessor.delete(deleteItem);
    }


    private static void checkInit() {
        if (!init) {
            throw new IllegalStateException("must use after init");
        }
    }

    /**
     * 通过downloadItem获取原始的DownloadTask，目前只针对回放
     */
    public static DownloadTask getPlayBackDownloadTask(DownloadItem item) {
        return getInstance().getDownloadTaskWithItem(item);
    }

    private DownloadTask getDownloadTaskWithItem(DownloadItem item) {
        return videoDownloadManager.getDownloadTask(item);
    }

}
