package com.foglotus.network.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.foglotus.core.LanShare
import com.foglotus.core.extention.logInfo
import com.foglotus.network.event.DownloadFailedEvent
import com.foglotus.network.event.DownloadFinishEvent
import com.foglotus.network.model.DownloadFile
import com.foglotus.network.util.DownloadUtil
import com.foglotus.network.util.DownloadUtil.Companion.INTENT_FILTER_PROGRESS
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal

/**
 *
 * @author foglotus
 * @since 2019/4/18
 */
class DownloadService:Service() {
    private lateinit var download:DownloadUtil

    override fun onCreate() {
        super.onCreate()
        download = DownloadUtil.get()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logInfo("服务事件运行")
        if(intent != null){

            val option = intent.getIntExtra("option",0)

            if(option == 0){
                val id:Long = intent.getLongExtra("id",0)
                val path:String = intent.getStringExtra("path")
                val name:String = intent.getStringExtra("name")
                val url:String = intent.getStringExtra("url")
                logInfo("添加下载，下载id$id 下载url $url 下载path $path")
                download.download(url,path,name,id,object:DownloadUtil.OnDownloadListener{
                    override fun onSuccess(id:Long) {
                        val file = LitePal.find(DownloadFile::class.java,id)
                        logInfo("完成下载$file")
                        if(file != null){
                            EventBus.getDefault().post(DownloadFinishEvent(file))
                        }
                    }

                    override fun onDownloading(id:Long,progress: Int) {
                        val intent = Intent(INTENT_FILTER_PROGRESS)
                        val file = LitePal.find(DownloadFile::class.java,id);
                        if(file != null){
                            file.status = progress
                            file.save()
                            intent.putExtra("id",file.id)
                            intent.putExtra("progress",progress)
                            LanShare.getContext().sendBroadcast(intent)
                        }
                    }

                    override fun onFailed(id:Long,message: String) {
                        val intent = Intent(INTENT_FILTER_PROGRESS)
                        val file = LitePal.find(DownloadFile::class.java,id);
                        if(file != null){
                            file.model = DownloadFile.RETRY
                            file.save()
                            intent.putExtra("id",id)
                            logInfo("下载失败，发送广播$file")
                            EventBus.getDefault().post(DownloadFailedEvent(id))
                        }
                    }

                })
            }else if(option == 1){
                logInfo("取消下载")
                val id:Long = intent.getLongExtra("id",0)
                download.cancel(id)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}