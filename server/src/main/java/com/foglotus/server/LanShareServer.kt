package com.foglotus.server

import com.foglotus.core.Const
import com.foglotus.core.LanShare
import com.foglotus.lanshare.core.extension.showToastOnUiThread
import com.foglotus.server.base.HttpServer
import com.foglotus.server.event.ServerRunListener
import com.foglotus.server.event.ServerRunningEvent
import org.greenrobot.eventbus.EventBus
import java.io.IOException

/**
 *
 * @author foglotus
 * @since 2019/3/15
 */
class LanShareServer private constructor(var host:String,var port:Int,var timeout:Int,var listener: ServerRunListener ?=null ){
    private lateinit var httpServer: HttpServer
    var isRunning = false
    object Builder{
        var host:String = Const.Server.HOST
        var port:Int = Const.Server.PORT
        var timeout:Int = Const.Server.TIMEOUT
        var listener:ServerRunListener ?= null
        var uploadPath:String = Const.Server.PATH
        fun build():LanShareServer{
            return LanShareServer(host, port, timeout, listener)
        }
    }

    fun start(){
        httpServer = HttpServer(host,port)
        try{
            httpServer.start(timeout)
            isRunning = true
            setServerMsg()
            listener?.onStart()
            EventBus.getDefault().post(ServerRunningEvent(true))
        }catch (e:IOException){
            showToastOnUiThread("服务器启动失败")
            EventBus.getDefault().post(ServerRunningEvent(false))
            isRunning = false
            setServerMsg()
            listener?.onStop()
        }
    }

    fun stop(){
        if(isRunning){
            httpServer.stop()
            isRunning = httpServer.isAlive
            if(isRunning){
                showToastOnUiThread("服务器关闭失败")
                EventBus.getDefault().post(ServerRunningEvent(false))
            }else{
                setServerMsg()
                listener?.onStop()
                EventBus.getDefault().post(ServerRunningEvent(true))
            }

        }
    }

    private fun setServerMsg(){
        LanShare.getServer().host = host
        LanShare.getServer().port = port
        LanShare.getServer().timeout = timeout
        LanShare.getServer().isRunning = isRunning
    }
}