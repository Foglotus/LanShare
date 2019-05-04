package com.foglotus.main.home.util

import com.foglotus.core.Const
import com.foglotus.core.util.GlobalUtil
import com.foglotus.core.util.SharedUtil
import com.foglotus.main.R

/**
 *
 * @author foglotus
 * @since 2019/4/8
 */
object ServerUtil {
    fun getServerDefaultPort():Int{
        return SharedUtil.read(GlobalUtil.getString(R.string.key_server_port_default), Const.Server.PORT)
    }

    fun getServerDefaultTimeout():Int{
        return SharedUtil.read(GlobalUtil.getString(R.string.key_server_timeout_default), Const.Server.TIMEOUT)
    }

    fun getServerDefaultUploadPath():String{
        return SharedUtil.read(GlobalUtil.getString(R.string.key_server_upload_path_default),Const.Server.PATH)
    }

}