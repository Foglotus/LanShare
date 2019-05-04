package com.foglotus.network.exception

import java.lang.RuntimeException

/**
 *
 * @author foglotus
 * @since 2019/4/12
 */
class ServerInvalidException(message:String) :RuntimeException(message){
    companion object {
        const val SERVER_INVALID = "The service is unavailable!"
    }
}