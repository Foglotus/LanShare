package com.foglotus.network.model

/**
 * 网络请求响应的回调接口。
 *
 * @author guolin
 * @since 17/2/12
 */
interface Callback {

    fun onResponse(response: Response)

    fun onFailure(e: Exception)

}
