package com.nj.baijiayun.downloader.request;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.baijiayun.constant.VideoDefinition;
import com.nj.baijiayun.downloader.DownloadManager;
import com.nj.baijiayun.downloader.ListenerTracker;
import com.nj.baijiayun.downloader.adapter.VideoDownloadAdapter;
import com.nj.baijiayun.downloader.config.SingleRealmTracker;
import com.nj.baijiayun.downloader.core.UpdateProcessor;
import com.nj.baijiayun.downloader.core.VideoDownloadManager;
import com.nj.baijiayun.downloader.helper.ExtraInfoHelper;
import com.nj.baijiayun.downloader.listener.DownloadListener;
import com.nj.baijiayun.downloader.realmbean.DownloadItem;

/**
 * @project zywx_android
 * @class name：com.baijiayun.common_down.request
 * @describe
 * @anthor houyi QQ:1007362137
 * @time 2019-06-05 19:55
 * @change
 * @time
 * @describe
 */
public class VideoDownloadRequest extends DownloadRequest {
    private final VideoDownloadManager videoDownloadManager;
    private final UpdateProcessor updateProcessor;
    private final VideoDownloadAdapter videoDownloadAdapter;

    public VideoDownloadRequest(DownloadManager.DownloadType type, String uid, UpdateProcessor updateProcessor,
                                VideoDownloadManager videoDownloadManager, VideoDownloadAdapter videoDownloadAdapter) {
        super(type, uid);
        this.videoDownloadManager = videoDownloadManager;
        this.updateProcessor = updateProcessor;
        this.videoDownloadAdapter = videoDownloadAdapter;
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
    public ListenerTracker start(LifecycleOwner owner, DownloadListener downloadListener, boolean autoFinish) {
        if (videoId <= 0) {
            throw new MissingArgumentException("missing argument videoId or videoId is invalid");
        }
        if (TextUtils.isEmpty(fileName)) {
            throw new MissingArgumentException("missing argument fileName or fileName is null");
        }
        if (TextUtils.isEmpty(parentId)) {
            throw new MissingArgumentException("missing argument parentId or parentId is null");
        }
        if (TextUtils.isEmpty(uid)) {
            throw new MissingArgumentException("missing argument uid or uid is null");
        }
        if (TextUtils.isEmpty(token)) {
            throw new MissingArgumentException("missing argument token or token is null");
        }
        DownloadItem item = saveToRealm();
        SingleRealmTracker tracker = null;
        if (owner != null && downloadListener != null) {
            tracker = new SingleRealmTracker(item.getKey(), downloadListener, autoFinish);
            updateProcessor.beginTracker(tracker, owner);
        }
        if (type == DownloadManager.DownloadType.TYPE_PLAY_BACK) {
            if (videoDefinitions != null) {
                for (VideoDefinition videoDefinition : videoDefinitions) {

                }
            }
            videoDownloadManager.downloadPlayBack(fileName, videoId, token, getExtraInfo(), item, updateProcessor,videoDefinitions);
        } else if (type == DownloadManager.DownloadType.TYPE_VIDEO) {
            videoDownloadManager.downloadVideo(fileName, videoId, token, getExtraInfo(), item, updateProcessor,videoDefinitions);
        }
        return tracker;
    }


    @NonNull
    private String getExtraInfo() {
        return ExtraInfoHelper.makeExtraInfos(uid,
                parentId,
                parentCover,
                parentName,
                chapterId,
                chapterName,
                itemId,
                fileName);
    }
}
