package com.nj.baijiayun.downloader.request;

import android.arch.lifecycle.LifecycleOwner;
import android.text.TextUtils;

import com.baijiayun.constant.VideoDefinition;
import com.nj.baijiayun.downloader.ListenerTracker;
import com.nj.baijiayun.downloader.listener.DownloadListener;
import com.nj.baijiayun.downloader.DownloadManager;
import com.nj.baijiayun.downloader.RealmManager;
import com.nj.baijiayun.downloader.realmbean.Chapter;
import com.nj.baijiayun.downloader.realmbean.DownloadItem;
import com.nj.baijiayun.downloader.realmbean.DownloadParent;


import io.realm.Realm;

import java.util.List;

/**
 * @project zywx_android
 * @class name：com.baijiayun.common_down.core
 * @describe
 * @anthor houyi QQ:1007362137
 * @time 2019-06-05 17:09
 * @change
 * @time
 * @describe
 */
public abstract class DownloadRequest {
    protected String uid;
    protected DownloadManager.DownloadType type;
    protected String parentId;
    protected String parentName;
    protected String parentCover;
    protected int parentType;
    protected String chapterId;
    protected String chapterName;
    protected long videoId;
    protected String fileName;
    protected String url;
    protected String itemId;
    protected String fileGenre;
    protected String token;
    protected List<VideoDefinition> videoDefinitions;


    public DownloadRequest(DownloadManager.DownloadType type, String uid) {
        this.type = type;
        this.uid = uid;
    }

    /**
     * @see #start(LifecycleOwner, DownloadListener, boolean)
     */
    public abstract void start();

    /**
     * @see #start(LifecycleOwner, DownloadListener, boolean)
     */
    public abstract ListenerTracker start(LifecycleOwner owner, DownloadListener downloadListener);

    /**
     * 开始下载，同时可以设置下载监听.
     *
     * @param autoFinish 是否需要自动销毁监听当文件下载完成，默认为true
     * @return 下载监听的追踪器，用于结束监听或者检查监听是否存活
     */
    public abstract ListenerTracker start(LifecycleOwner owner, DownloadListener downloadListener, boolean autoFinish);

    /**
     * 保存到realm数据库
     */
    public DownloadItem saveToRealm() {
        final Realm realmInstance = RealmManager.getRealmInstance();
        DownloadItem item = new DownloadItem();
        item.setFileType(type.value());
        item.setUid(uid);
        item.setItemId(itemId);
        item.setFileName(fileName);
        item.setVideoId(videoId);
        item.setFileUrl(url);
        item.setFileGenre(fileGenre);
        item.setStartTime(System.currentTimeMillis() / 1000);
        setCustomParams(item);
        if (!TextUtils.isEmpty(parentId)) {
            DownloadParent parent = new DownloadParent();
            parent.setParentId(parentId);
            parent.setType(parentType);
            parent.setParentName(parentName);
            parent.setParentCover(parentCover);
            item.setParent(parent);
            if (chapterId != null) {
                Chapter chapter = new Chapter();
                chapter.setChapterId(chapterId);
                chapter.setChapterName(chapterName);
                chapter.setParent(parent);
                item.setChapter(chapter);
            }
        }
        item.generateKey();
        DownloadItem key = realmInstance.where(DownloadItem.class).equalTo("key", item.getKey()).findFirst();
        if (key == null) {
            realmInstance.beginTransaction();
            realmInstance.insertOrUpdate(item);
            realmInstance.commitTransaction();
        } else {
            item = realmInstance.copyFromRealm(key);
        }
        realmInstance.close();
        return item;

    }

    protected void setCustomParams(DownloadItem item) {

    }

    /**
     * 下载Item的上一级，文库下载时为文库Id
     * 课件、课程下载时为课程Id
     */
    public DownloadRequest parentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    /**
     * 下载Item的上一级，文库下载时为文库名称
     * 课件、课程下载时为课程名称
     */
    public DownloadRequest parentName(String parentName) {
        this.parentName = parentName;
        return this;
    }

    /**
     * 下载Item的上一级，文库下载时为文库封面
     * 课件、课程下载时为课程封面
     */
    public DownloadRequest parentCover(String parentCover) {
        this.parentCover = parentCover;
        return this;
    }

    /**
     * 课程或文库的类型
     */
    public DownloadRequest parentType(int parentType) {
        this.parentType = parentType;
        return this;
    }

    /**
     * 课程章Id
     */
    public DownloadRequest chapterId(String chapterId) {
        this.chapterId = chapterId;
        return this;
    }

    /**
     * 课程章名称
     */
    public DownloadRequest chapterName(String chapterName) {
        this.chapterName = chapterName;
        return this;
    }

    /**
     * 下载item对应的id，视频为节id，文库为文库Id
     */
    public DownloadRequest itemId(String itemId) {
        this.itemId = itemId;
        return this;
    }

    /**
     * 文件下载Url
     */
    public DownloadRequest url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 文件名称，视频为节名称，文件为文件名
     */
    public DownloadRequest fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * 点播为videoId，回放为roomId
     */
    public DownloadRequest videoId(long videoId) {
        this.videoId = videoId;
        return this;
    }

    /**
     * 文件下载后缀名
     */
    public DownloadRequest fileGenre(String fileGenre) {
        this.fileGenre = fileGenre;
        return this;
    }

    /**
     * 视频下载后token
     */
    public DownloadRequest token(String token) {
        this.token = token;
        return this;
    }

    /**
     * 设置下载优先级
     * @param videoDefinitions
     * @return
     */
    public DownloadRequest setVideoDefinitions(List<VideoDefinition> videoDefinitions) {
        this.videoDefinitions = videoDefinitions;
        return this;
    }
}
