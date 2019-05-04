package com.foglotus.network.util

import com.foglotus.core.LanShare
import com.foglotus.core.callback.PendingRunnable
import com.foglotus.core.util.FileUtil
import com.foglotus.core.util.SharedUtil
import com.foglotus.core.extention.logInfo
import com.foglotus.network.request.LoggingInterceptor
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

/**
 *
 * @author foglotus
 * @since 2019/4/18
 */
class DownloadUtil private constructor() {
    init {
        thread {
            while(true){
                monitor()
                Thread.sleep(2000)
            }
        }
    }
    private val okHttpClient by lazy {
        OkHttpClient.Builder().addNetworkInterceptor(LoggingInterceptor()).build()
    }

    private var count:Int = 0

    private val pendingRunnable = ArrayList<PendingRunnable>()

    fun download(url:String,saveDir:String,name:String,id:Long,listener:OnDownloadListener){
        if(count > 0){
            pendingRunnable.add(object:PendingRunnable{
                override fun run(index: Int) {
                    execute(url,listener,id,saveDir,name)
                }

            })
        }else{
            execute(url,listener,id,saveDir,name)
        }
    }

    private fun execute(url:String,listener:OnDownloadListener,id:Long,saveDir: String,name: String){
        count = 1
        val requestBuilder = Request.Builder()
        requestBuilder.url(url)
        requestBuilder.addHeader("uuid",SharedUtil.read("uuid",""))
        requestBuilder.addHeader(NetworkConst.HEADER_USER_AGENT,NetworkConst.HEADER_USER_AGENT_VALUE)
        val call = okHttpClient.newCall(requestBuilder.build())
        call.enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                count = 0
                listener.onFailed(id,e.message.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                if(response.code() == 200){
                    val savePath = FileUtil.isExistDir(saveDir)
                    try {
                        val input = response.body()!!.byteStream()
                        val total = response.body()!!.contentLength()
                        logInfo("total:$total")
                        val file = File(savePath,name)
                        val output =  FileOutputStream(file)
                        var sum:Long = 0
                        val buf = ByteArray(2048)
                        var length  = input.read(buf)
                        while(length!=-1){
                            output.write(buf,0,length)
                            sum += length
                            listener.onDownloading(id,(sum * 1.0f / total * 100).toInt())
                            length = input.read(buf)
                        }
                        listener.onSuccess(id)
                        count = 0
                    }catch (e:Exception){
                        listener.onFailed(id,"下载失败")
                        count = 0
                    }
                }else{
                    listener.onFailed(id,"下载失败")
                    count = 0
                }
            }

        })
        callMap[id] = call
    }

    private fun monitor(){
        LanShare.getHandler().post {
            executePendingRunnable()
        }
    }

    private fun executePendingRunnable(){
        if(count == 0 && pendingRunnable.size > 0){
            logInfo("执行请求连接池")
            val pending = pendingRunnable.removeAt(0)
            pending.run(0)
            logInfo("删除执行连接池")
        }
    }

    fun cancel(id:Long){
        callMap[id]?.cancel()
        return
    }

    interface OnDownloadListener{
        fun onSuccess(id:Long)
        fun onDownloading(id:Long,progress:Int)
        fun onFailed(id:Long,message:String)
    }
    companion object{
        const val INTENT_FILTER_PROGRESS = "com.foglotus.lanshare.update.progress"
        private var downloadUtil:DownloadUtil ?= null
        private var callMap:MutableMap<Long,Call> = HashMap()

        fun get():DownloadUtil{
            if(downloadUtil == null){
                downloadUtil = DownloadUtil()
            }
            return downloadUtil!!
        }
    }
}