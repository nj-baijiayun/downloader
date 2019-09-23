package com.nj.baijiayun.downloader.realmbean;

import android.support.annotation.Nullable;

import com.baijiayun.download.DownloadTask;
import com.nj.baijiayun.downloader.listener.DownloadListener;
import com.nj.baijiayun.downloader.utils.MD5Util;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * @author houyi QQ1007362137
 * @project zywx_android
 * @class name：com.baijiayun.common_down.realmbean
 * @describe
 * @time 2019-06-18 1655
 * @change
 * @time
 * @describe
 */
public class DownloadItem extends RealmObject {
    /**
     * 1:课件文件，2:文库文件，3:回放视频，4:点播，41:点播视频，42:点播音频,5:图文课程,6文件视频（阿里云或其他途径）
     * 7:音频文件
     */
    public static final int FILE_TYPE_COURSE_WAVE = 1;
    public static final int FILE_TYPE_LIBRARY = 2;
    public static final int FILE_TYPE_PLAY_BACK = 3;
    public static final int FILE_TYPE_PLAY_BACK_SMALL = 31;
    public static final int FILE_TYPE_VIDEO = 4;
    public static final int FILE_TYPE_VIDEO_AUDIO = 41;
    public static final int FILE_TYPE_FILE_COURSE = 5;
    public static final int FILE_TYPE_FILE_VIDEO = 6;
    public static final int FILE_TYPE_FILE_AUDIO = 7;
    public static final int FILE_TYPE_CUSTOM = 8;

    public static final int DOWNLOAD_STATUS_COMPLETE = 1;
    public static final int DOWNLOAD_STATUS_DOWNLOADING = 2;
    public static final int DOWNLOAD_STATUS_WAITING = 3;
    public static final int DOWNLOAD_STATUS_STOP = 4;
    public static final int DOWNLOAD_STATUS_ERROR = 5;
    /**
     * 通过videoId、roomId或者下载url生成的唯一的Key
     */
    @PrimaryKey
    public String key = "";

    /**
     * 根据文件类型变化
     * fileType   itemId
     * 1
     * 2          文件id
     * 3          节Id
     * 4          节Id
     */
    private String itemId = "";

    private String uid = "";

    //下载相关字段
    private long currentSize = 0;
    /**
     * 下载状态
     * 1：下载完成,2：下载中,3：未开始,4：暂停,5：错误
     */
    private int downloadStatus = DOWNLOAD_STATUS_WAITING;

    private long downloadSpeed = 0;

    //文件通用字段
    private String fileName = "";
    /**
     * 1课件文件，2文库文件，3回放视频，4点播视频,5图文课程,6文件视频
     */
    private int fileType = 0;
    private long fileSize = 0;
    private String filePath = "";
    /**
     * 文件后缀名
     */
    private String fileGenre = "";

    //文件类型额外字段
    private String fileUrl = "";

    //Int,根据具体的视频类型表示为RoomId和VideoId
    private long videoId = 0;

    private DownloadParent parent = null;
    private Chapter chapter = null;

    private long duration = 0;
    private String sign;
    private long startTime;

    @Ignore
    int lastStatus = downloadStatus;
    @Ignore
    private DownloadListener downloadListener;
    @Ignore
    private boolean newListener;

    /**
     * @return isComplete
     */
    public boolean update() {
        if (downloadListener != null) {
            if (lastStatus == DOWNLOAD_STATUS_WAITING && getDownloadStatus() != DOWNLOAD_STATUS_DOWNLOADING) {
                downloadListener.onStart(this);
            }
            if (getDownloadStatus() == DOWNLOAD_STATUS_DOWNLOADING) {
                downloadListener.onProgressUpdate(this);
            }
            if (getDownloadStatus() == DOWNLOAD_STATUS_STOP && lastStatus != DOWNLOAD_STATUS_STOP) {
                downloadListener.onPause(this);
            }
            if (getDownloadStatus() == DOWNLOAD_STATUS_ERROR && lastStatus != DOWNLOAD_STATUS_ERROR) {
                downloadListener.onError(this);
            }
            if ((getDownloadStatus() == DOWNLOAD_STATUS_COMPLETE && lastStatus != DOWNLOAD_STATUS_COMPLETE) || newListener) {
                newListener = false;
                downloadListener.onComplete(this);
            }
        }
        if (getDownloadStatus() == DOWNLOAD_STATUS_COMPLETE && lastStatus != DOWNLOAD_STATUS_COMPLETE) {
            return true;
        }
        lastStatus = getDownloadStatus();
        return false;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof DownloadItem) {
            return getKey().equals(((DownloadItem) obj).getKey());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public long getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(long downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileGenre() {
        return fileGenre;
    }

    public void setFileGenre(String fileGenre) {
        this.fileGenre = fileGenre;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public DownloadParent getParent() {
        return parent;
    }

    public void setParent(DownloadParent parent) {
        this.parent = parent;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public int getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(int lastStatus) {
        this.lastStatus = lastStatus;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void generateKey() {
        switch (fileType) {
            case FILE_TYPE_PLAY_BACK:
            case FILE_TYPE_VIDEO:
                key = uid + String.valueOf(fileType) + videoId + parent.parentId + itemId;
                break;
            default:
                key = uid + String.valueOf(fileType) + MD5Util.encrypt(fileUrl) + (parent == null ? "0" : parent.parentId) + itemId;
                break;
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.newListener = true;
        this.downloadListener = downloadListener;
        update();
    }

    @Override
    public String toString() {
        return "DownloadItem{" +
                "key='" + key + '\'' +
                ", itemId='" + itemId + '\'' +
                ", uid='" + uid + '\'' +
                ", currentSize=" + currentSize +
                ", downloadStatus=" + downloadStatus +
                ", downloadSpeed=" + downloadSpeed +
                ", fileName='" + fileName + '\'' +
                ", fileType=" + fileType +
                ", fileSize=" + fileSize +
                ", filePath='" + filePath + '\'' +
                ", fileGenre='" + fileGenre + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", videoId=" + videoId +
                ", parent=" + parent +
                ", chapter=" + chapter +
                ", lastStatus=" + lastStatus +
                ", downloadListener=" +
                '}';
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getDownloadRate() {
        if (getFileSize() == 0) {
            return 0;
        }
        return (int) (getCurrentSize() * 100 / getFileSize());
    }
}
