package com.nj.baijiayun.downloader.adapter;

import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.inf.IEntity;
import com.nj.baijiayun.downloader.realmbean.DownloadItem;

/**
 * @project zywx_android
 * @class name：com.nj.baijiayun.downloader.adapter
 * @describe
 * @author houyi QQ:1007362137
 * @time 2019-06-04 17:21
 */
public class FileDownloadAdapter {

    public FileDownloadAdapter() {

    }

    public DownloadItem adapter(DownloadEntity downloadTask) {

        return null;
    }

    /**
     * 适配文件下载的状态
     */
    public int adapterDownloadStatus(int state) {
        switch (state) {
            case IEntity.STATE_COMPLETE:
                return DownloadItem.DOWNLOAD_STATUS_COMPLETE;
            case IEntity.STATE_FAIL:
            case IEntity.STATE_OTHER:
            case IEntity.STATE_CANCEL:
                return DownloadItem.DOWNLOAD_STATUS_ERROR;
            case IEntity.STATE_PRE:
            case IEntity.STATE_POST_PRE:
            case IEntity.STATE_WAIT:
                return DownloadItem.DOWNLOAD_STATUS_WAITING;
            case IEntity.STATE_STOP:
                return DownloadItem.DOWNLOAD_STATUS_STOP;
            case IEntity.STATE_RUNNING:
                return DownloadItem.DOWNLOAD_STATUS_DOWNLOADING;
            default:
                return DownloadItem.DOWNLOAD_STATUS_ERROR;
        }
    }
}
