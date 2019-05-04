package com.foglotus.core

import android.os.Environment
import com.foglotus.core.util.GlobalUtil

/**
 *
 * @author foglotus
 * @since 2019/3/18
 */
interface Const {
    interface Server{
        companion object {
            const val HOST = "127.0.0.1"
            const val PORT = 8080
            const val TIMEOUT = 5000
            var PATH = GlobalUtil.getDefaultUploadPath()
            const val SAFE_CODE = "safe_code"
            const val DYNAMIC_SAFE_CODE = "dynamic_safe_code"
        }

    }
    interface ServerResponse{
        companion object {
            const val EMPTY_DYNAMIC_CODE = 1000
            const val ERROR_DYNAMIC_CODE = 1001
            const val SUCCESS = 1005
            const val FORBID_LOGIN = 1002
            const val CHECK_SUCCESS = 1003
            const val CHECK_FAILED = 1004
            const val REDIRECT = 403
            const val NOT_FOUND = 404
        }
    }
}