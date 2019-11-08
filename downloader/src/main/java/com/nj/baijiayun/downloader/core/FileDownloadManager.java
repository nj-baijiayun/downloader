package com.nj.baijiayun.downloader.core;

import android.support.annotation.Keep;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadReceiver;
import com.arialyy.aria.core.download.DownloadTask;
import com.nj.baijiayun.downloader.utils.MD5Util;
import com.nj.baijiayun.logger.log.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author houyi QQ:1007362137
 * @project zywx_android
 * @class name：com.baijiayun.zywx.module_library
 * @describe
 * @time 2019/01/02 10:20
 * @change
 * @timed
 * @describe
 */
public class FileDownloadManager {
    private String filePath;
    private List<DownloadEntity> allTasks;
    private List<DownloadEntity> downloadingTasks;

    private HashMap<String, FileOpenCallBack> callBackMap;

    public FileDownloadManager(String filePath) {
        this.filePath = filePath;
        callBackMap = new HashMap<>();
        getDownload(this).register();
        allTasks = getDownload(this).getTaskList();
        downloadingTasks = getDownload(this).getAllNotCompleteTask();
    }

    public void updateFilePath(String filePath) {
        this.filePath = filePath;
    }

    private DownloadReceiver getDownload(Object o) {
        return Aria.download(o);
    }

    public void unregisterCallback() {
        getDownload(this).unRegister();
    }

    public List<DownloadEntity> getAllTasks(String uid) {
        List<DownloadEntity> list = new ArrayList<>();
        if (allTasks == null) {
            return new ArrayList<>();
        }
        for (DownloadEntity downloadEntity : allTasks) {
            if (downloadEntity.getDownloadPath().contains("/" + uid + "/")) {
                list.add(downloadEntity);
            }
        }
        return list;
    }

    public void downloadFile(String parentId, final String url, final String fileName, final String genre, final FileOpenCallBack callBack) {
        String filePath = getFilePath(parentId, url, fileName, genre);
        callBackMap.put(url, callBack);
        getDownload(this)
                .load(url)     //读取下载地址
                .setFilePath(filePath) //设置文件保存的完整路径
                .start();
    }

    public String getFilePath(String parentId, String url, String fileName, String genre) {
        return filePath + parentId + "/" + MD5Util.encrypt(url) + "/" + fileName + "." + genre;
    }


    @Keep
    @Download.onPre
    void taskPre(DownloadTask task) {
        FileOpenCallBack callBack = callBackMap.get(task.getKey());
        if (callBack != null) {
            callBack.onTaskStart(task);
        }
    }

    @Keep
    @Download.onTaskRunning
    void running(DownloadTask task) {
        updateEntity(task);
    }

    private void updateEntity(DownloadTask task) {
        if (allTasks == null) {
            return;
        }
        DownloadEntity downloadEntity = task.getDownloadEntity();
        for (DownloadEntity current : allTasks) {
            if (current.getKey().equals(downloadEntity.getKey())) {
                current.setSpeed(downloadEntity.getSpeed());
                current.setCurrentProgress(downloadEntity.getCurrentProgress());
                current.setFileSize(downloadEntity.getFileSize());
                current.setState(downloadEntity.getState());
                Logger.d("update DownloadEntity" + current.toString());
                return;
            }
        }
    }

    @Keep
    @Download.onTaskComplete
    protected void taskComplete(DownloadTask task) {
        updateEntity(task);
    }

    @Keep
    @Download.onTaskResume
    protected void taskResume(DownloadTask task) {
        updateEntity(task);
    }


    @Keep
    @Download.onTaskStop
    protected void taskStop(DownloadTask task) {
        updateEntity(task);
    }

    @Keep
    @Download.onWait
    protected void onWait(DownloadTask task) {
        updateEntity(task);
    }

    @Keep
    @Download.onTaskFail
    protected void taskFail(DownloadTask task) {
        updateEntity(task);
    }

    public List<DownloadEntity> getDownloadingTasks() {
        return downloadingTasks;
    }

    public DownloadEntity getDownloadingTask(DownloadEntity entity) {
        return getDownload(this).getDownloadEntity(entity.getUrl());
    }

    public void delete(DownloadEntity remove) {
        getDownload(this).load(remove.getUrl()).cancel(true);
    }

    public void pauseDownload(DownloadEntity item) {
        getDownload(this).load(item.getUrl()).stop();
    }

    public void resumeDownload(DownloadEntity item) {
        getDownload(this).load(item).start();
    }

    public abstract static class FileOpenCallBack<T> {

        public abstract void onTaskStart(DownloadTask task);

    }
}
