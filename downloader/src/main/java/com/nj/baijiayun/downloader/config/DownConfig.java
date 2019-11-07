package com.nj.baijiayun.downloader.config;

import android.content.Context;

import com.nj.baijiayun.downloader.helper.FileStorageManager;

import java.io.File;

public class DownConfig {

    private Context context;
    private String filePath;
    private String videoPath;
    private String videoCustomDomain;
    private String uid;

    private DownConfig() {
    }

    public Context getContext() {
        return context;
    }

    public String getFilePath() {
        return new File(filePath, uid).getAbsolutePath() + "/";
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getVideoPath() {
        return new File(videoPath, uid).getAbsolutePath() + "/";
    }

    public String getVideoCustomDomain() {
        return videoCustomDomain;
    }

    public static class Builder {
        private Context context;
        private String filePath;
        private String videoPath;
        private String videoCustomDomain;
        private String uid;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder setVideoPath(String videoPath) {
            this.videoPath = videoPath;
            return this;
        }

        public Builder setVideoCustomDomain(String videoCustomDomain) {
            this.videoCustomDomain = videoCustomDomain;
            return this;
        }

        public Builder setUid(String uid) {
            this.uid = uid;
            return this;
        }

        public DownConfig builder() {
            DownConfig downConfig = new DownConfig();
            downConfig.context = this.context;
            if (filePath == null) {
                filePath = FileStorageManager.getCourseFileDir(context);
            }
            if (videoPath == null) {
                videoPath = FileStorageManager.getVideoDownLoadPath(context);
            }
            downConfig.filePath = this.filePath;
            downConfig.videoPath = this.videoPath;
            downConfig.videoCustomDomain = this.videoCustomDomain;
            downConfig.uid = this.uid;
            return downConfig;
        }
    }
}
