package com.nj.baijiayun.downloader.config;

import com.nj.baijiayun.downloader.ListenerTracker;
import com.nj.baijiayun.downloader.RealmManager;
import com.nj.baijiayun.downloader.core.LifecycleTracker;
import com.nj.baijiayun.downloader.listener.DownloadListener;
import com.nj.baijiayun.downloader.listener.UpdateListener;
import com.nj.baijiayun.downloader.realmbean.DownloadItem;
import com.nj.baijiayun.logger.log.Logger;
import io.realm.Realm;

/**
 * @author houyi QQ:1007362137
 * @project android_lib_downloader
 * @class name：com.nj.baijiayun.downloader.config
 * @time 2019-08-08 16:25
 * @describe
 */
public class SingleRealmTracker implements ListenerTracker, UpdateListener {
    private final boolean autoFinish;
    private Realm realm;
    private final String queryKey;
    private final DownloadListener listener;
    private boolean isAlive = true;
    private boolean isStart = false;
    private DownloadItem item;

    public SingleRealmTracker(String key, DownloadListener downloadListener, boolean autoFinish) {
        queryKey = key;
        listener = downloadListener;
        this.autoFinish = autoFinish;
    }

    @Override
    public void destroy() {
        synchronized (this) {
            if (isAlive) {
                if (realm != null) {
                    realm.close();
                }
                isAlive = false;
            }
        }
    }

    @Override
    public void notifyUpdate() {
        if (isAlive && listener != null) {
            boolean isComplete = item.update();
            if (isComplete && autoFinish) {
                destroy();
            }
        }
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    public void begin(LifecycleTracker lifecycleTracker) {
        synchronized (this) {
            if (isStart || !isAlive) {
                Logger.w("try to begin when the tracker is started or not alive");
                return;
            }
            isStart = true;
            realm = RealmManager.getRealmInstance();
            item = realm.where(DownloadItem.class).equalTo("key", queryKey).findFirst();
            item.setDownloadListener(listener);
        }
    }
}
