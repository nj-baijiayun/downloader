package com.nj.baijiayun.downloader.adapter;

import com.baijiayun.download.DownloadTask;
import com.baijiayun.download.constant.TaskStatus;
import com.nj.baijiayun.downloader.realmbean.DownloadItem;

/**
 * @project zywx_android
 * @class name：com.baijiayun.common_down.core
 * @describe
 * @anthor houyi QQ:1007362137
 * @time 2019-06-04 17:22
 * @change
 * @time
 * @describe
 */
public class VideoDownloadAdapter {

    public VideoDownloadAdapter() {

    }

    public DownloadItem adapter(DownloadTask downloadTask) {

        return null;
    }
    /**
     * 适配百家云视频下载的状态
     */
    public int adapterDownloadStatus(TaskStatus state) {
        switch (state) {
            case Finish:
                return DownloadItem.DOWNLOAD_STATUS_COMPLETE;
            case New:
                return DownloadItem.DOWNLOAD_STATUS_WAITING;
            case Error:
            case Cancel:
                return DownloadItem.DOWNLOAD_STATUS_ERROR;
            case Pause:
                return DownloadItem.DOWNLOAD_STATUS_STOP;
            case Downloading:
                return DownloadItem.DOWNLOAD_STATUS_DOWNLOADING;
            default:
                return DownloadItem.DOWNLOAD_STATUS_ERROR;
        }
    }
}
