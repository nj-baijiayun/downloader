package com.nj.baijiayun.downloader.listener;

import com.nj.baijiayun.downloader.realmbean.DownloadItem;

/**
 * @author houyi QQ:1007362137
 * @project zywx_android
 * @class name：com.baijiayun.common_down
 * @describe
 * @time 2019-06-15 18:29
 * @change
 * @time
 * @describe
 */
public interface DownloadListener {
    /**
     * 下载开始
     *
     * @param downloadItem item
     */
    void onStart(DownloadItem downloadItem);

    /**
     * 进度更新
     *
     * @param downloadItem item
     */
    void onProgressUpdate(DownloadItem downloadItem);

    /**
     * 下载完成
     *
     * @param downloadItem item
     */
    void onComplete(DownloadItem downloadItem);

    /**
     * 暂停
     *
     * @param downloadItem item
     */
    void onPause(DownloadItem downloadItem);

    /**
     * 下载出错
     *
     * @param downloadItem item
     */
    void onError(DownloadItem downloadItem);
}
