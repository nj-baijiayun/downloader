package com.nj.baijiayun.downloader.realmbean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @project zywx_android
 * @class name：com.baijiayun.common_down.realmbean
 * @describe
 * @anthor houyi QQ:1007362137
 * @time 2019-06-18 15:43
 * @change
 * @time
 * @describe
 */
public class Chapter extends RealmObject {
    @PrimaryKey
    public String chapterId = "";
    public DownloadParent parent = null;
    public String chapterName = "";

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public DownloadParent getParent() {
        return parent;
    }

    public void setParent(DownloadParent parent) {
        this.parent = parent;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

}
