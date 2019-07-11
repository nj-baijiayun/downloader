package com.nj.baijiayun.downloader.config;

import android.text.TextUtils;

import com.nj.baijiayun.downloader.DownloadManager;
import com.nj.baijiayun.downloader.RealmManager;
import com.nj.baijiayun.downloader.listener.UpdateListener;
import com.nj.baijiayun.downloader.realmbean.DownloadItem;

import java.util.List;

import io.reactivex.Flowable;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * @project zywx_android
 * @class name：com.baijiayun.common_down.bean
 * @describe
 * @author houyi QQ:1007362137
 * @time 2019-06-04 17:32
 * @change
 * @time
 * @describe
 */
public class DownloadRealmWrapper implements UpdateListener {
    private Realm realm;
    private RealmResults<DownloadItem> results;
    private UpdateListener onUpdateListener;

    public DownloadRealmWrapper(String currentUid, String courseId, DownloadManager.DownloadType[] downloadTypes, Integer[] downloadStatus) {
        realm = RealmManager.getRealmInstance();
        RealmQuery<DownloadItem> query = realm.where(DownloadItem.class).equalTo("uid", currentUid);
        if (!TextUtils.isEmpty(courseId)) {
            query = query
                    .and()
                    .equalTo("parent.parentId", courseId);
        }
        if (downloadTypes != null && downloadTypes.length > 0) {
            query = query
                    .and()
                    .in("fileType", DownloadManager.DownloadType.toIntArray(downloadTypes));
        }
        if (downloadStatus != null && downloadStatus.length > 0) {
            query = query
                    .and()
                    .in("downloadStatus", downloadStatus);
        }
        results = query.findAllAsync();
    }

    public List<DownloadItem> getResults() {
        return results;
    }

    public Flowable<? extends List<DownloadItem>> getAsFlow() {
        return results.asFlowable();
    }

    public List<DownloadItem> getOriginResults(){
        return realm.copyFromRealm(results);
    }

    @Override
    public void destroy() {
        realm.close();
    }

    @Override
    public void notifyUpdate() {
        for (DownloadItem result : results) {
            if (result.getLastStatus() != DownloadItem.DOWNLOAD_STATUS_COMPLETE) {
                result.update();
            }
        }
        if (onUpdateListener != null) {
            onUpdateListener.updated();
        }
    }

    public void setOnUpdateListener(UpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public interface UpdateListener {
        void updated();
    }
}
