package com.foglotus.network.util

import com.foglotus.network.request.LoggingInterceptor
import okhttp3.*
import java.io.File
import okio.Okio
import okio.BufferedSink
import okhttp3.RequestBody
import okio.Buffer
import okio.Source
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 *
 * @author foglotus
 * @since 2019/4/20
 */
class UploadUtil private constructor(){

    private val okHttpClient by lazy {
        OkHttpClient.Builder().addNetworkInterceptor(LoggingInterceptor()).build()
    }
    private var call:Call? = null

    fun upload(host:String,filePath:String,uuid:String,listener: OnUploadListener){

        if(filePath.isEmpty()){
            listener.onFailed("文件不存在")
        }else{
            val file = File(filePath)
            if(!file.exists() && file.isDirectory){
                listener.onFailed("文件不存在")
            }else{

                //开始正规操作
                val url = "http://$host/mobile-upload/put.action"
                val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                val fileBody = createProgressRequestBody(MediaType.parse("application/octet-stream"), file,listener)
                builder.addFormDataPart("file",file.name,fileBody)
                val body = builder.build()

                val request = Request.Builder().addHeader("uuid",uuid).addHeader(NetworkConst.HEADER_USER_AGENT,NetworkConst.HEADER_USER_AGENT_VALUE).url(url).post(body).build();

                call = okHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request)

                call!!.enqueue(object :Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        listener.onFailed(e.message.toString())
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if(response.code() == 200){
                            listener.onSuccess()
                        }else{
                            listener.onFailed("上传失败")
                        }
                    }
                })
            }
        }



    }

    /**
     * 创建带进度的RequestBody
     * @param contentType MediaType
     * @param file  准备上传的文件
     * @param callBack 回调
     * @param <T>
     * @return
    </T> */
    private fun createProgressRequestBody(contentType: MediaType?, file: File, listener: OnUploadListener): RequestBody {
        return object : RequestBody() {
            override fun contentType(): MediaType? {
                return contentType
            }

            override fun contentLength(): Long {
                return file.length()
            }

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                val source: Source
                try {
                    source = Okio.source(file)
                    val buf = Buffer()
                    val remaining = contentLength()
                    var current: Long = 0
                    var readCount: Long = 0
                    readCount = source.read(buf, 2048)
                    while (readCount!= -1L) {
                        sink.write(buf, readCount)
                        current += readCount
                        listener.onUploading((current * 1.0f / remaining * 100).toInt())
                        readCount = source.read(buf, 2048)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    interface OnUploadListener{
        fun onSuccess()
        fun onFailed(message:String)
        fun onUploading(progress:Int)
    }

    companion object{
        const val INTENT_FILTER_PROGRESS = "com.foglotus.lanshare.update.progress"
        private var uploadUtil:UploadUtil ?= null

        fun get():UploadUtil{
            if(uploadUtil == null){
                uploadUtil = UploadUtil()
            }
            return uploadUtil!!
        }
    }
}