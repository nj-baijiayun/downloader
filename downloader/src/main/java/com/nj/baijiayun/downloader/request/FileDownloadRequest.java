package com.nj.baijiayun.downloader.request;

import android.arch.lifecycle.LifecycleOwner;
import android.text.TextUtils;

import com.arialyy.aria.core.download.DownloadTask;
import com.nj.baijiayun.downloader.DownloadManager;
import com.nj.baijiayun.downloader.ListenerTracker;
import com.nj.baijiayun.downloader.adapter.FileDownloadAdapter;
import com.nj.baijiayun.downloader.config.SingleRealmTracker;
import com.nj.baijiayun.downloader.core.FileDownloadManager;
import com.nj.baijiayun.downloader.core.UpdateProcessor;
import com.nj.baijiayun.downloader.listener.DownloadListener;
import com.nj.baijiayun.downloader.realmbean.DownloadItem;


/**
 * @project zywx_android
 * @class name：com.baijiayun.common_down.request
 * @describe 文件下载Request对象
 * @anthor houyi QQ:1007362137
 * @time 2019-06-05 19:55
 * @change
 * @time
 * @describe
 */
public class FileDownloadRequest extends DownloadRequest {
    private final FileDownloadManager fileDownloadManager;
    private final UpdateProcessor updateProcessor;
    private final FileDownloadAdapter fileDownloadAdapter;


    public FileDownloadRequest(DownloadManager.DownloadType type, String uid, UpdateProcessor updateProcessor,
                               FileDownloadManager fileDownloadManager, FileDownloadAdapter fileDownloadAdapter) {
        super(type, uid);
        this.fileDownloadManager = fileDownloadManager;
        this.updateProcessor = updateProcessor;
        this.fileDownloadAdapter = fileDownloadAdapter;
        if (type == DownloadManager.DownloadType.TYPE_FILE_VIDEO
        ) {
            fileGenre = "mp4";
        } else if (type == DownloadManager.DownloadType.TYPE_FILE_AUDIO) {
            fileGenre = "mp3";
        }
    }

    @Override
    public void start() {
        start(null, null);
    }

    @Override
    public ListenerTracker start(LifecycleOwner owner, DownloadListener downloadListener) {
        return start(owner, downloadListener, true);
    }

    @Override
    public ListenerTracker start(final LifecycleOwner owner, final DownloadListener downloadListener, boolean autoFinish) {
        if (TextUtils.isEmpty(url)) {
            throw new MissingArgumentException("missing argument url or url is null");
        }
        if (TextUtils.isEmpty(fileName)) {
            throw new MissingArgumentException("missing argument fileName or fileName is null");
        }
        if (TextUtils.isEmpty(fileGenre)) {
            throw new MissingArgumentException("missing argument fileGenre or fileGenre is null");
        }
        if (TextUtils.isEmpty(uid)) {
            throw new MissingArgumentException("missing argument uid or uid is null");
        }
        if (type == DownloadManager.DownloadType.TYPE_FILE_GRAPHIC) {
            if (TextUtils.isEmpty(itemId)) {
                throw new MissingArgumentException("missing argument itemId or itemId is null");
            }
        }
        final DownloadItem downloadItem = saveToRealm();
        SingleRealmTracker tracker = null;
        if (owner != null && downloadListener != null) {
            tracker = new SingleRealmTracker(downloadItem.key, downloadListener, autoFinish);
            updateProcessor.beginTracker(tracker, owner);
        }
        fileDownloadManager.downloadFile(parentId, url, fileName, fileGenre, new FileDownloadManager.FileOpenCallBack() {
            @Override
            public void onTaskStart(DownloadTask task) {
                updateProcessor.newTask(downloadItem, task.getEntity());
            }

        });
        return tracker;
    }

    @Override
    protected void setCustomParams(DownloadItem item) {
        item.setFilePath(fileDownloadManager.getFilePath(parentId, url, fileName, fileGenre));
    }
}
