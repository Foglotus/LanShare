package com.foglotus.server.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.foglotus.core.Const
import com.foglotus.core.LanShare
import com.foglotus.server.LanShareServer
import com.foglotus.server.R
import com.foglotus.server.event.ServerRunListener
import com.foglotus.server.event.ServerStatusChangeEvent
import org.greenrobot.eventbus.EventBus

/**
 *
 * @author foglotus
 * @since 2019/2/19
 */
class ServerService:Service(){
    private var server:LanShareServer ?= null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(server == null && intent!= null){
            server = LanShareServer.Builder.apply {
                host = intent.getStringExtra("host")
                port = intent.getIntExtra("port",Const.Server.PORT)
                timeout = intent.getIntExtra("timeout",Const.Server.TIMEOUT)
                listener = object :ServerRunListener{
                    override fun onStart() {
                        EventBus.getDefault().post(ServerStatusChangeEvent(true))
                    }

                    override fun onStop() {
                        EventBus.getDefault().post(ServerStatusChangeEvent(false))
                    }

                }
            }.build()
            LanShare.getServer().uploadPath = intent.getStringExtra("uploadPath")
        }else{
            server?.host = intent!!.getStringExtra("host")
            server?.port = intent.getIntExtra("port",Const.Server.PORT)
            server?.timeout = intent.getIntExtra("timeout",Const.Server.TIMEOUT)
            server?.listener = object :ServerRunListener{
                override fun onStart() {
                    EventBus.getDefault().post(ServerStatusChangeEvent(true))
                }

                override fun onStop() {
                    EventBus.getDefault().post(ServerStatusChangeEvent(false))
                }

            }
            LanShare.getServer().uploadPath = intent.getStringExtra("uploadPath")
        }
        server?.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        server?.stop()
        server = null
    }

    private fun showNotification(){
        var notification = NotificationCompat.Builder(this,"receive").apply {
            setSmallIcon(R.drawable.message)
            setContentTitle("收到新上传文件")
            setAutoCancel(false)
            setOngoing(true)
            var pending = PendingIntent.getActivity(LanShare.getContext(),0,null, PendingIntent.FLAG_NO_CREATE);
            setContentIntent(pending)
        }.build()

    }


}