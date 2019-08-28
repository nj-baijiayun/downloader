package com.nj.baijiayun.downloader.core;

import android.arch.lifecycle.LifecycleOwner;

import com.nj.baijiayun.downloader.DownloadManager;
import com.nj.baijiayun.downloader.ListenerTracker;
import com.nj.baijiayun.downloader.config.DownloadRealmWrapper;
import com.nj.baijiayun.downloader.config.SingleRealmTracker;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @project zywx_android
 * @class name：com.baijiayun.common_down.core
 * @describe
 * @author houyi QQ:1007362137
 * @time 2019-06-05 10:06
 * @change
 * @time
 * @describe
 */
public class UpdateDispatcher {

    private ConcurrentHashMap<LifecycleOwner, LifecycleTracker> lifeBindTracker = new ConcurrentHashMap<>();

    /**
     * 分发集合变化比如新增下载，删除下载等
     */
    public void dispatchSetDifference() {
        //not used , update by realm it self,wo don't need to do this our self
    }

    /**
     * 分发下载状态改变比如状态变化、下载进度变化等
     */
    public void dispatchDownloadStatusChanged() {
        //update by realm it self,wo don't need to do this our self
        for (LifecycleTracker value : lifeBindTracker.values()) {
            value.notifyChanged();
        }
    }

    /**
     * 通过参数生成获取
     *
     * @param lifecycleOwner
     * @param courseId
     * @param downloadTypes
     * @param downloadStatus
     * @return
     */
    public DownloadRealmWrapper registerDispatcher(LifecycleOwner lifecycleOwner, String courseId, DownloadManager.DownloadType[] downloadTypes, Integer[] downloadStatus) {
        LifecycleTracker lifecycleTracker = lifeBindTracker.get(lifecycleOwner);
        if (lifecycleTracker == null) {
            lifecycleTracker = new LifecycleTracker(lifecycleOwner, lifeBindTracker);
            lifeBindTracker.put(lifecycleOwner, lifecycleTracker);
        }
        DownloadRealmWrapper listener = new DownloadRealmWrapper(DownloadManager.getCurrentUid(),courseId, downloadTypes,downloadStatus);
        lifecycleTracker.add(listener);
        return listener;
    }

    public void registerSingleListener(LifecycleOwner lifecycleOwner, SingleRealmTracker tracker) {
        LifecycleTracker lifecycleTracker = lifeBindTracker.get(lifecycleOwner);
        if (lifecycleTracker == null) {
            lifecycleTracker = new LifecycleTracker(lifecycleOwner, lifeBindTracker);
            lifeBindTracker.put(lifecycleOwner, lifecycleTracker);
        }
        tracker.begin(lifecycleTracker);
        lifecycleTracker.add(tracker);
    }

}
