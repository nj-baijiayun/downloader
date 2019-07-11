package com.nj.baijiayun.downloader;

import android.content.Context;

import com.nj.baijiayun.downloader.realmbean.DownloadModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @project zywx_android
 * @class name：com.baijiayun.basic.libwapper.realm
 * @author houyi QQ:1007362137
 * @time 2019-06-04 16:27
 * @describe
 */
public class RealmManager {

    public static void init(Context context) {
        Realm.init(context.getApplicationContext());
        Realm.setDefaultConfiguration(
                new RealmConfiguration
                        .Builder()
                        .schemaVersion(1)
                        .deleteRealmIfMigrationNeeded()
                        .modules(new DownloadModule())
                        .build());
    }

    public static Realm getRealmInstance() {
        return Realm.getDefaultInstance();
    }
}
