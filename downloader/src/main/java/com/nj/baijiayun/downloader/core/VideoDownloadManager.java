package com.nj.baijiayun.downloader.core;

import android.content.Context;
import android.widget.Toast;

import com.baijiahulian.common.networkv2.HttpException;
import com.baijiayun.download.DownloadListener;
import com.baijiayun.download.DownloadManager;
import com.baijiayun.download.DownloadModel;
import com.baijiayun.download.DownloadTask;
import com.nj.baijiayun.logger.log.Logger;
import com.nj.baijiayun.downloader.helper.ExtraInfoHelper;
import com.nj.baijiayun.downloader.realmbean.DownloadItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author houyi
 */
public final class VideoDownloadManager {

    public List<DownloadTask> getAllTasks(String uid) {
        List<DownloadTask> allTasks = downloadManager.getAllTasks();
        ArrayList<DownloadTask> tasks = new ArrayList<>();

        for (DownloadTask downloadTask : allTasks) {
            DownloadModel downloadModel = downloadTask.getVideoDownloadInfo();
            if (ExtraInfoHelper.getUid(downloadModel.extraInfo).equals(uid)) {
                tasks.add(downloadTask);
            }
        }
        return tasks;
    }

    public void deleteVideo(DownloadTask remove) {
        downloadManager.deleteTask(remove);
    }

    private final DownloadManager downloadManager;
    private final Context appContext;

    public final DownloadListener DOWNLOAD_LISTENER = new DownloadListener() {
        @Override
        public void onProgress(DownloadTask downloadTask) {
        }

        @Override
        public void onError(DownloadTask downloadTask, HttpException e) {
            Toast.makeText(appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused(DownloadTask downloadTask) {

        }

        @Override
        public void onStarted(DownloadTask downloadTask) {
            Logger.d("onStarted" + downloadTask.getProgress());
            notifyDownloadStart(downloadTask);
        }

        @Override
        public void onFinish(DownloadTask downloadTask) {

        }

        @Override
        public void onDeleted(DownloadTask downloadTask) {

        }
    };

    private void notifyDownloadStart(DownloadTask downloadTask) {
        //TODO xiazai
    }

    public VideoDownloadManager(Context context, String filePath) {
        downloadManager = DownloadManager.getInstance(context);
        appContext = context.getApplicationContext();
        downloadManager.setTargetFolder(filePath);
        downloadManager.loadDownloadInfo();
    }

    public void updateDownloadPath(String filePath) {
        downloadManager.setTargetFolder(filePath);
        downloadManager.loadDownloadInfo(true);
    }

    public void downloadVideo(String fileName, long videoId, String token, String extraInfo, final DownloadItem item, final UpdateProcessor updateProcessor) {
        Disposable d = downloadManager
                .newVideoDownloadTask(fileName,
                        videoId,
                        token,
                        extraInfo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DownloadTask>() {
                    @Override
                    public void accept(DownloadTask downloadTask) throws Exception {
                        //直接开始下载
                        downloadTask.setDownloadListener(DOWNLOAD_LISTENER);
                        startTask(downloadTask);
                        updateProcessor.newTask(item, downloadTask);
                        Logger.d("new task in downloadVideo, item is" + item.toString() + ", downloadTask is " + downloadTask.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(appContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void startTask(DownloadTask downloadTask) {
        downloadTask.start();
    }


    public void downloadPlayBack(String fileName, long videoId, String token, String extraInfo, final DownloadItem item, final UpdateProcessor updateProcessor) {
        Disposable d = downloadManager
                .newPlaybackDownloadTask(fileName,
                        videoId,
                        0,//sessionId
                        token,
                        extraInfo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DownloadTask>() {
                    @Override
                    public void accept(DownloadTask downloadTask) throws Exception {
                        //直接开始下载
                        downloadTask.setDownloadListener(DOWNLOAD_LISTENER);
                        startTask(downloadTask);
                        updateProcessor.newTask(item, downloadTask);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(appContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void pauseDownload(DownloadTask downloadTask) {
        downloadTask.pause();
    }

    public void resumeDownload(DownloadTask downloadTask) {
        downloadTask.start();
    }


}
