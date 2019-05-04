package com.foglotus.network.request

import com.foglotus.core.LanShare
import com.foglotus.core.extention.logVerbose
import com.foglotus.network.exception.ResponseCodeException
import com.foglotus.network.model.Callback
import com.foglotus.network.model.OriginThreadCallback
import com.foglotus.network.util.NetworkConst
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 *
 * @author foglotus
 * @since 2019/4/12
 */
abstract class Request{
    private lateinit var okHttpClient: OkHttpClient
    private val okHttpBuilder:OkHttpClient.Builder = OkHttpClient.Builder().addNetworkInterceptor(LoggingInterceptor())
    private var callback:Callback? = null
    private var params:Map<String,String>? = null
    var getParamsAlready = false
    var host:String = ""

    init {
        connectTimeout(5)
        writeTimeout(10)
        readTimeout(10)
    }

    private fun build() {
        okHttpClient = okHttpBuilder.build()
    }

    fun connectTimeout(seconds: Int) {
        okHttpBuilder.connectTimeout(seconds.toLong(), TimeUnit.SECONDS)
    }

    fun writeTimeout(seconds: Int) {
        okHttpBuilder.writeTimeout(seconds.toLong(), TimeUnit.SECONDS)
    }

    fun readTimeout(seconds: Int) {
        okHttpBuilder.readTimeout(seconds.toLong(), TimeUnit.SECONDS)
    }

    /**
     * 设置响应回调接口
     * @param callback
     * 回调的实例
     */
    fun setListener(callback: Callback?,host: String) {
        this.callback = callback
        this.host = "http://$host"
    }

    /**
     * 组装网络请求后添加到HTTP发送队列，并监听响应回调。
     * @param requestModel
     * 网络请求对应的实体类
     */
    fun <T : com.foglotus.network.model.Response> inFlight(requestModel: Class<T>) {
        build()
        val requestBuilder = okhttp3.Request.Builder()
        if (method() == GET && getParams() != null) {
            requestBuilder.url(urlWithParam())
        } else {
            requestBuilder.url(host+url())
        }
        requestBuilder.headers(headers(Headers.Builder()).build())
        when {
            method() == POST -> requestBuilder.post(formBody())
            method() == PUT -> requestBuilder.put(formBody())
            method() == DELETE -> requestBuilder.delete(formBody())
        }
        okHttpClient.newCall(requestBuilder.build()).enqueue(object : okhttp3.Callback {

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val result = if (body != null) {
                            body.string()
                        } else {
                            ""
                        }
                        logVerbose(LoggingInterceptor.TAG, result)
                        val gson = GsonBuilder().disableHtmlEscaping().create()
                        val responseModel = gson.fromJson(result, requestModel)
                        response.close()
                        notifyResponse(responseModel)
                    } else {
                        notifyFailure(ResponseCodeException(response.code()))
                    }
                } catch (e: Exception) {
                    notifyFailure(e)
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                notifyFailure(e)
            }

        })
    }

    abstract fun url(): String

    abstract fun method(): Int

    abstract fun listen(callback: Callback?,host: String)


    /**
     * Android客户端的所有请求都需要添加User-Agent: GifFun Android这样一个请求头。每个接口的封装子类可以添加自己的请求头。
     * @param builder
     * 请求头builder
     * @return 添加完请求头后的builder。
     */
    open fun headers(builder: Headers.Builder): Headers.Builder {
        builder.add(NetworkConst.HEADER_USER_AGENT, NetworkConst.HEADER_USER_AGENT_VALUE)
        return builder
    }

    open fun params(): Map<String, String>? {
        return null
    }

    /**
     * 构建POST、PUT、DELETE请求的参数体。
     *
     * @return 组装参数后的FormBody。
     */
    private fun formBody(): FormBody {
        val builder = FormBody.Builder()
        val params = getParams()
        if (params != null) {
            val keys = params.keys
            if (!keys.isEmpty()) {
                for (key in keys) {
                    val value = params[key]
                    if (value != null) {
                        builder.add(key, value)
                    }
                }
            }
        }
        return builder.build()
    }

    /**
     * 当GET请求携带参数的时候，将参数以key=value的形式拼装到GET请求URL的后面，并且中间以?符号隔开。
     * @return 携带参数的URL请求地址。
     */
    private fun urlWithParam(): String {
        val params = getParams()
        if (params != null) {
            val keys = params.keys
            if (!keys.isEmpty()) {
                val paramsBuilder = StringBuilder()
                var needAnd = false
                for (key in keys) {
                    if (needAnd) {
                        paramsBuilder.append("&")
                    }
                    paramsBuilder.append(key).append("=").append(params[key])
                    needAnd = true
                }
                return host+url() + "?" + paramsBuilder.toString()
            }
        }
        return host+url()
    }

    /**
     * 获取本次请求所携带的所有参数。
     *
     * @return 本次请求所携带的所有参数，以Map形式返回。
     */
    private fun getParams(): Map<String, String>? {
        if (!getParamsAlready) {
            params = params()
            getParamsAlready = true
        }
        return params
    }

    /**
     * 当请求响应成功的时候，将服务器响应转换后的实体类进行回调。
     * @param response
     * 服务器响应转换后的实体类
     */
    private fun notifyResponse(response: com.foglotus.network.model.Response) {
        callback?.let {
            if (it is OriginThreadCallback) {
                it.onResponse(response)
                callback = null
            } else {
                LanShare.getHandler().post {
                    it.onResponse(response)
                    callback = null
                }
            }
        }
    }

    /**
     * 当请求响应失败的时候，将具体的异常进行回调。
     * @param e
     * 请求响应的异常
     */
    private fun notifyFailure(e: Exception) {
        callback?.let {
            if (it is OriginThreadCallback) {
                it.onFailure(e)
                callback = null
            } else {
                LanShare.getHandler().post {
                    it.onFailure(e)
                    callback = null
                }
            }
        }
    }

    companion object {

        const val GET = 0

        const val POST = 1

        const val PUT = 2

        const val DELETE = 3
    }


}