package com.nj.baijiayun.downloader.core;

import android.support.annotation.Keep;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadReceiver;
import com.arialyy.aria.core.download.DownloadTask;
import com.nj.baijiayun.downloader.utils.MD5Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @project zywx_android
 * @class name：com.baijiayun.zywx.module_library
 * @describe
 * @author houyi QQ:1007362137
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
        return filePath +   parentId + "/" + MD5Util.encrypt(url) + "/" + fileName + "." + genre;
    }


    @Keep
    @Download.onTaskStart
    void taskComplete(DownloadTask task) {
        FileOpenCallBack callBack = callBackMap.get(task.getKey());
        if (callBack != null) {
            callBack.onTaskStart(task);
        }
    }

    public List<DownloadEntity> getDownloadingTasks() {
        return downloadingTasks;
    }

    public void delete(DownloadEntity remove) {
        getDownload(this).load(remove.getUrl()).cancel(true);
    }

    public void pauseDownload(DownloadEntity item) {
        getDownload(this).load(item.getUrl()).stop();
    }

    public void resumeDownload(DownloadEntity item) {
        String url = item.getUrl();
        getDownload(this).load(url).setFilePath(item.getDownloadPath()).start();
    }

    public abstract static class FileOpenCallBack<T> {

        public abstract void onTaskStart(DownloadTask task);
    }
}
