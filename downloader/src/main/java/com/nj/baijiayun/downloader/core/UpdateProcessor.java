package com.nj.baijiayun.downloader.core;

import android.arch.lifecycle.LifecycleOwner;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.arialyy.aria.core.download.DownloadEntity;
import com.baijiayun.download.DownloadModel;
import com.baijiayun.download.DownloadTask;
import com.baijiayun.download.constant.DownloadType;
import com.nj.baijiayun.downloader.RealmManager;
import com.nj.baijiayun.downloader.adapter.FileDownloadAdapter;
import com.nj.baijiayun.downloader.adapter.VideoDownloadAdapter;
import com.nj.baijiayun.downloader.config.SingleRealmTracker;
import com.nj.baijiayun.downloader.realmbean.DownloadItem;
import com.nj.baijiayun.downloader.utils.VideoDownloadUtils;
import com.nj.baijiayun.logger.log.Logger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @author houyi QQ:1007362137
 * @project zywx_android
 * @class name：com.baijiayun.common_down.core
 * @describe 更新同步处理器，用于同步文件和视频下载进度到数据库以及相应页面。
 * @time 2019-06-04 16:58
 */
public class UpdateProcessor implements UpdateController {
    private static final int MSG_VIDEO_INFO_UPDATE = 1;
    //文件下载恢复速度比较慢，心跳次数要增加
    private static final int DEFAULT_LIVE = 6;
    public final Realm realm;
    private final FileDownloadManager fileDownloadManager;
    private final UpdateDispatcher updateDispatcher;
    private final FileDownloadAdapter fileDownloadAdapter;
    private final VideoDownloadAdapter videoDownloadAdapter;
    private final VideoDownloadManager videoDownloadManager;
    private String uid;
    private ConcurrentHashMap<DownloadItem, Object> downloadMap;
    private InfoUpdateHandler infoUpdateHandler;
    private int liveBeats = DEFAULT_LIVE;


    public UpdateProcessor(FileDownloadManager fileDownloadManager, VideoDownloadManager videoDownloadManager,
                           UpdateDispatcher updateDispatcher, String uid) {
        this.fileDownloadManager = fileDownloadManager;
        this.videoDownloadManager = videoDownloadManager;
        this.updateDispatcher = updateDispatcher;
        fileDownloadAdapter = new FileDownloadAdapter();
        videoDownloadAdapter = new VideoDownloadAdapter();
        this.uid = uid;
        realm = RealmManager.getRealmInstance();
        process();
    }

    private void process() {
        mappingDownloadItem();
        infoUpdateHandler = new InfoUpdateHandler(this);
        infoUpdateHandler.sendEmptyMessage(MSG_VIDEO_INFO_UPDATE);

    }

    /**
     * 将数据库的DownloadItem和实际的下载Item进行映射
     * 以便在更新时获取最新的数据
     */
    private void mappingDownloadItem() {
        synchronized (this) {
            List<DownloadItem> videoItems = realm.copyFromRealm(getAllVideoDownloadItems(uid));
            List<DownloadItem> fileItems = realm.copyFromRealm(getAllFileDownloadItems(uid));

            List<DownloadTask> videoTasks = videoDownloadManager.getAllTasks(uid);
            List<DownloadEntity> fileTasks = fileDownloadManager.getAllTasks(uid);
            if (checkNeedSync(videoItems, videoTasks, fileItems, fileTasks)) {
                syncDownloadItem();
            }
            downloadMap = new ConcurrentHashMap<>();
            for (DownloadItem videoItem : videoItems) {
                for (DownloadTask videoTask : videoTasks) {
                    //判断类型是否相同
                    boolean isPlayBack = (videoItem.getFileType() == DownloadItem.FILE_TYPE_PLAY_BACK ||
                            videoItem.getFileType() == DownloadItem.FILE_TYPE_PLAY_BACK_SMALL)
                            && videoTask.getDownloadType() == DownloadType.Playback;
                    boolean isFileType = (videoItem.getFileType() == DownloadItem.FILE_TYPE_VIDEO ||
                            videoItem.getFileType() == DownloadItem.FILE_TYPE_VIDEO_AUDIO)
                            && videoTask.getDownloadType() == DownloadType.Video;
                    if (isPlayBack || isFileType) {
                        if (videoItem.getVideoId() == videoTask.getVideoDownloadInfo().roomId) {
                            downloadMap.put(videoItem, videoTask);
                            break;
                        }
                    }
                }
            }
            for (DownloadItem fileItem : fileItems) {
                for (DownloadEntity fileTask : fileTasks) {
                    if (fileItem.getFileUrl().equals(fileTask.getKey())
                            && fileItem.getFilePath().equals(fileTask.getDownloadPath())) {
                        // TODO 检查判断条件是否有效
                        downloadMap.put(fileItem, fileTask);
                    }
                }
            }
        }
    }

    private void syncDownloadItem() {
        //TODO 信息全量同步
    }

    private boolean checkNeedSync(List<DownloadItem> videoItems, List<DownloadTask> videoTasks,
                                  List<DownloadItem> fileItems, List<DownloadEntity> fileTasks) {
        return checkCountNotEqual(fileItems, fileTasks) || checkCountNotEqual(videoItems, videoTasks);
    }

    private boolean checkCountNotEqual(List<DownloadItem> fileItems, List fileTasks) {
        if ((fileTasks == null || fileTasks.size() == 0) && fileItems.size() != 0) {
            return true;
        }
        return fileTasks != null && fileTasks.size() != fileItems.size();
    }

    private RealmResults<DownloadItem> getAllItems(String uid) {
        return realm
                .where(DownloadItem.class)
                .equalTo("uid", uid)
                .findAll()
                .sort("downloadStatus", Sort.ASCENDING);
    }

    private RealmResults<DownloadItem> getAllFileDownloadItems(String uid) {
        return realm
                .where(DownloadItem.class)
                .equalTo("uid", uid)
                .and()
                .in("fileType", new Integer[]{DownloadItem.FILE_TYPE_COURSE_WAVE,
                        DownloadItem.FILE_TYPE_LIBRARY, DownloadItem.FILE_TYPE_FILE_COURSE,
                        DownloadItem.FILE_TYPE_FILE_AUDIO, DownloadItem.FILE_TYPE_FILE_VIDEO})
                .findAll()
                .sort("downloadStatus", Sort.ASCENDING);
    }

    private RealmResults<DownloadItem> getAllDownloadingItems(String uid, Realm realm) {
        return realm
                .where(DownloadItem.class)
                .equalTo("uid", uid)
                .and()
                .in("downloadStatus", new Integer[]{DownloadItem.DOWNLOAD_STATUS_DOWNLOADING
                        , DownloadItem.DOWNLOAD_STATUS_STOP, DownloadItem.DOWNLOAD_STATUS_WAITING})
                .findAll();
    }

    private RealmResults<DownloadItem> getAllDirtyOrDownloadingItems(String uid, Realm realm) {
        return realm
                .where(DownloadItem.class)
                .equalTo("uid", uid)
                .and()
                .beginGroup()
                .equalTo("dirty", true)
                .endGroup()
                .findAll();
    }


    private RealmResults<DownloadItem> getAllVideoDownloadItems(String uid) {
        return realm
                .where(DownloadItem.class)
                .equalTo("uid", uid)
                .and()
                .in("fileType", new Integer[]{DownloadItem.FILE_TYPE_PLAY_BACK
                        , DownloadItem.FILE_TYPE_VIDEO, DownloadItem.FILE_TYPE_PLAY_BACK_SMALL,
                        DownloadItem.FILE_TYPE_VIDEO_AUDIO})

                .findAll()
                .sort("downloadStatus", Sort.ASCENDING);

    }

    @Override
    public void update() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                synchronized (UpdateProcessor.this) {
                    boolean isAlive = false;
                    RealmResults<DownloadItem> allDownloadingItems = getAllDownloadingItems(uid, realm);
                    if (allDownloadingItems.size() == 0) {
                        liveBeatsDecrease();
                        Logger.d("update when there is" + allDownloadingItems.size() + " downloading items  ");
                        return;
                    }
                    for (DownloadItem next : allDownloadingItems) {
                        if (next.getDownloadStatus() == DownloadItem.DOWNLOAD_STATUS_COMPLETE || next.getDownloadStatus() == DownloadItem.DOWNLOAD_STATUS_ERROR) {
                            continue;
                        }
                        if (next.getDownloadStatus() != DownloadItem.DOWNLOAD_STATUS_STOP) {
                            isAlive = true;
                        }
                        updateItem(next);
                    }
                    if (!isAlive) {
                        liveBeatsDecrease();
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                dispatch();
            }
        });
        infoUpdateHandler.sendEmptyMessageDelayed(MSG_VIDEO_INFO_UPDATE, 1000);


    }

    private void updateItem(DownloadItem next) {
        Object o = downloadMap.get(next);
        if (o instanceof DownloadEntity) {
            updateFileTask(next, (DownloadEntity) o);
        } else if (o instanceof DownloadTask) {
            updateBjyVideoTask(next, (DownloadTask) o);

        }
    }

    private void liveBeatsDecrease() {
        liveBeats--;
        if (liveBeats <= 0) {
            checkToPauseProcessor();
        }
    }

    private void updateBjyVideoTask(DownloadItem next, DownloadTask o) {
        DownloadModel videoDownloadInfo = o.getVideoDownloadInfo();
        long speed = videoDownloadInfo.speed;
        long size = videoDownloadInfo.totalLength;
        long currentSize = videoDownloadInfo.downloadLength;
        while (videoDownloadInfo.nextModel != null) {
            videoDownloadInfo = videoDownloadInfo.nextModel;
            speed += videoDownloadInfo.speed;
            size += videoDownloadInfo.totalLength;
            currentSize += videoDownloadInfo.downloadLength;
        }
        next.setCurrentSize(currentSize);
        next.setFilePath(o.getVideoFilePath());
        next.setDownloadSpeed(speed);
        next.setFileSize(size);
        Logger.d("update Video id: " + next.getItemId() + " ,current speed is " + speed + " ,current size is " + currentSize);
        int downloadStatus = videoDownloadAdapter.adapterDownloadStatus(o.getTaskStatus());
        if (downloadStatus == DownloadItem.DOWNLOAD_STATUS_COMPLETE) {
            next.setSign(videoDownloadInfo.targetFolder + "/" + o.getSignalFileName());
            next.setDuration(videoDownloadInfo.videoDuration);
        }
        next.setDownloadStatus(downloadStatus);
    }

    private void updateFileTask(DownloadItem next, DownloadEntity o) {
        next.setCurrentSize(o.getCurrentProgress());
        next.setDownloadSpeed(o.getSpeed());
        next.setFileSize(o.getFileSize());

        next.setDownloadStatus(fileDownloadAdapter.adapterDownloadStatus(o.getState()));
        Logger.d("currentSize:" + VideoDownloadUtils.getFormatSize(next.getCurrentSize())
                + " currentSpeed:" + VideoDownloadUtils.getFormatSize(next.getDownloadSpeed())
                + " fileSize:" + VideoDownloadUtils.getFormatSize(next.getFileSize())
                + " status:" + next.getDownloadStatus());
    }

    /**
     * 检查后暂停更新处理器
     */
    private void checkToPauseProcessor() {
        if (!infoUpdateHandler.isPaused && liveBeats <= 0) {
            synchronized (UpdateProcessor.this) {
                if (!infoUpdateHandler.isPaused) {
                    infoUpdateHandler.pause();
                    liveBeats = DEFAULT_LIVE;
                }
            }
        }
    }

    private void checkToResumeProcessor() {
        synchronized (UpdateProcessor.this) {
            liveBeats = DEFAULT_LIVE;
            if (infoUpdateHandler.isPaused) {
                infoUpdateHandler.resume();
            }
        }
    }

    public void release() {
        realm.close();
    }

    private void dispatch() {
        updateDispatcher.dispatchDownloadStatusChanged();
        updateDispatcher.dispatchSetDifference();
    }

    /**
     * 新增单个下载任务
     */
    public void newTask(DownloadItem downloadItem, Object item) {
        downloadMap.put(downloadItem, item);
        checkToResumeProcessor();
    }

    /**
     * 删除单个下载任务
     */
    public void delete(final DownloadItem deleteItem) {
        Object remove = downloadMap.remove(deleteItem);
        if (remove instanceof DownloadEntity) {
            fileDownloadManager.delete(((DownloadEntity) remove));
        } else if (remove instanceof DownloadTask) {
            videoDownloadManager.deleteVideo(((DownloadTask) remove));
        }
        realm.beginTransaction();
        realm.where(DownloadItem.class).equalTo("key", deleteItem.getKey()).findAll().deleteFirstFromRealm();
        realm.commitTransaction();
    }

    /**
     * 暂停单个下载任务
     */
    public void pauseDownload(DownloadItem item) {
        Object object = downloadMap.get(item);
        if (object instanceof DownloadEntity) {
            fileDownloadManager.pauseDownload(((DownloadEntity) object));
        } else if (object instanceof DownloadTask) {
            videoDownloadManager.pauseDownload((DownloadTask) object);
        }
    }

    /**
     * 恢复单个下载任务
     */
    public void resumeDownload(final DownloadItem item) {
        Object object = downloadMap.get(item);
        if (object instanceof DownloadEntity) {
            fileDownloadManager.resumeDownload(((DownloadEntity) object));
        } else if (object instanceof DownloadTask) {
            videoDownloadManager.resumeDownload((DownloadTask) object);
        }
        checkToResumeProcessor();
    }

    public void beginTracker(SingleRealmTracker tracker, LifecycleOwner owner) {
        updateDispatcher.registerSingleListener(owner, tracker);
    }

    public void updateUid(String uid) {
        this.uid = uid;
    }

    /**
     * 处理器的Handle用于每秒发送更新消息
     */
    private static class InfoUpdateHandler extends Handler {
        private final UpdateController controller;
        private boolean isPaused = false;

        InfoUpdateHandler(UpdateController controller) {
            this.controller = controller;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_VIDEO_INFO_UPDATE) {
                if (!isPaused) {
                    controller.update();
                }
            } else {
                Logger.w("no this case " + msg.what);
            }
            super.handleMessage(msg);
        }

        public void pause() {
            isPaused = true;
            removeMessages(MSG_VIDEO_INFO_UPDATE);
        }

        public void resume() {
            isPaused = false;
            sendEmptyMessage(MSG_VIDEO_INFO_UPDATE);
        }

        public boolean isPaused() {
            return isPaused;
        }
    }
}
