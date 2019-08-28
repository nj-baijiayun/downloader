package com.nj.baijiayun.downloader;

/**
 * @author houyi QQ:1007362137
 * @project android_lib_downloader
 * @class name：com.nj.baijiayun.downloader
 * @time 2019-08-08 16:26
 * a tracker to a single downloadTask,and will auto destroy when the task is finish,
 * or you can {@link #destroy()} it yourself if you don't need to listen to download anymore.
 */
public interface ListenerTracker {
    /**
     * will remove the listener ,then listener will not notify again.
     */
    void destroy();

    /**
     * to check the listener is alive.
     *
     * @return isAlive
     */
    boolean isAlive();
}
