package com.nj.baijiayun.lib_downloader

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nj.baijiayun.downloader.DownloadManager
import com.nj.baijiayun.downloader.config.DownConfig
import com.nj.baijiayun.downloader.listener.DownloadListener
import com.nj.baijiayun.downloader.realmbean.DownloadItem
import com.nj.baijiayun.logger.log.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.functions.Consumer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = DownConfig.Builder(this).setUid("1").builder()
        DownloadManager.init(config)
        setContentView(R.layout.activity_main)
        Logger.setTag("[downloader]");
        Logger.setEnable(true)
        Logger.setPriority(Logger.MIN_LOG_PRIORITY)
        Logger.init(this)
    }

    fun download(v: View) {
        Logger.d("start")

        RxPermissions(this).request(android.Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(Consumer {
            if(it){
                download()
            }
        })
    }

    private fun download() {
        DownloadManager.downloadFile(DownloadManager.DownloadType.TYPE_FILE_VIDEO)
            .itemId("33")
            .fileName("2019xCjepicYeB1565939987")
            .fileGenre("xlsx")
            .url("https://baijiayun-wangxiao.oss-cn-beijing.aliyuncs.com/uploads/file/2019xCjepicYeB1565939987.xlsx")
            .start(this, object : DownloadListener {
                override fun onProgressUpdate(downloadItem: DownloadItem?) {
                    Logger.d("onProgressUpdate " + downloadItem?.downloadRate)
                }

                override fun onComplete(downloadItem: DownloadItem?) {
                    Logger.d("onComplete")
                }

                override fun onPause(downloadItem: DownloadItem?) {
                    Logger.d("onPause")
                }

                override fun onError(downloadItem: DownloadItem?) {
                    Logger.d("onError")
                }

                override fun onStart(downloadItem: DownloadItem?) {
                    Logger.d("onStart")
                }

            })
    }
}
