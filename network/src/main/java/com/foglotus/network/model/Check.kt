package com.foglotus.network.model

import com.foglotus.network.request.CheckRequest

/**
 *
 * @author foglotus
 * @since 2019/4/12
 */
class Check():Response() {
    companion object {
        fun getResponse(callback: Callback,host:String){
            CheckRequest().listen(callback,host)
        }
    }
}