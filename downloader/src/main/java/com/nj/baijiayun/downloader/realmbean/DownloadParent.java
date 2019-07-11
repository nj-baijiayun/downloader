package com.nj.baijiayun.downloader.realmbean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @project zywx_android
 * @class name：com.baijiayun.common_down.realmbean
 * @describe
 * @anthor houyi QQ1007362137
 * @time 2019-06-18 1703
 * @change
 * @time
 * @describe
 */
public class DownloadParent extends RealmObject {

    @PrimaryKey
    public String parentId = "";
    public String parentName = "";
    public String parentCover = null;
    /**
     * 容器类型1：课程，2：文库
     */
    public int type = 0;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentCover() {
        return parentCover;
    }

    public void setParentCover(String parentCover) {
        this.parentCover = parentCover;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
