package com.foglotus.network.request

import com.foglotus.core.extention.logVerbose
import java.io.IOException

import okhttp3.*

/**
 * OkHttp网络请求日志拦截器，通过日志记录OkHttp所有请求以及响应的细节。
 *
 * @author guolin
 * @since 17/2/25
 */
internal class LoggingInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val t1 = System.nanoTime()
        logVerbose(TAG, "Sending request: " + request.url() + "\n" + request.headers())

        val response = chain.proceed(request)

        val t2 = System.nanoTime()
        logVerbose(TAG, "Received response for " + response.request().url() + " in "
                + (t2 - t1) / 1e6 + "ms\n" + response.headers())
        return response
    }

    companion object {

        val TAG = "LoggingInterceptor"

    }

}
