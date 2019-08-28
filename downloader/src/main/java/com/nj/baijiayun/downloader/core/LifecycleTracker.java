package com.nj.baijiayun.downloader.core;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

import com.nj.baijiayun.downloader.listener.UpdateListener;
import org.apache.commons.compress.utils.Sets;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class LifecycleTracker implements LifecycleObserver {
    private LifecycleOwner owner;
    private Map<LifecycleOwner, LifecycleTracker> lifeTracker;
    private Set<UpdateListener> listeners = new CopyOnWriteArraySet<>();
    private boolean isEnable = true;
    private volatile boolean inDestorying;

    public LifecycleTracker(LifecycleOwner owner, Map<LifecycleOwner, LifecycleTracker> lifeTracker) {
        owner.getLifecycle().addObserver(this);
        this.owner = owner;
        this.lifeTracker = lifeTracker;
    }


    public void add(UpdateListener listener) {
        listeners.add(listener);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stop() {
        isEnable = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void start() {
        isEnable = true;
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroy() {
        isEnable = false;
        lifeTracker.remove(owner);
        unbind();
        listeners.clear();
        owner = null;
    }

    private void unbind() {
        for (UpdateListener listener : listeners) {
            listener.destroy();
        }
    }

    public void notifyChanged() {
        if (isEnable) {
            for (UpdateListener listener : listeners) {
                listener.notifyUpdate();
            }
        }
    }

    public void remove(UpdateListener listener) {
        listeners.remove(listener);
    }
}
