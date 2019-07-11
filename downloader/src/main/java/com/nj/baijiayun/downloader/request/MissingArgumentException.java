package com.nj.baijiayun.downloader.request;

/**
 * @project zywx_android
 * @class name：com.baijiayun.common_down.request
 * @describe
 * @anthor houyi QQ:1007362137
 * @time 2019-06-06 10:49
 * @change
 * @time
 * @describe
 */
public class MissingArgumentException extends RuntimeException {
    public MissingArgumentException() {
    }

    public MissingArgumentException(String msg) {
        super(msg);
    }
}
