package com.nj.baijiayun.lib_downloader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nj.baijiayun.downloader.DownloadManager
import com.nj.baijiayun.downloader.config.DownConfig

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = DownConfig.Builder(this)
            .setUid(uid)
            .setFilePath(filePath)
            .setVideoPath(videoPath)
            .setVideoCustomDomain(customdomain)
            .builder()
        DownloadManager.init(config)
        setContentView(R.layout.activity_main)
    }
}
