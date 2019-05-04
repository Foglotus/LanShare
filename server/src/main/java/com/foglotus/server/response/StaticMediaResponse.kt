package com.foglotus.server.response


import com.foglotus.core.LanShare
import com.foglotus.core.extention.logDebug
import com.foglotus.server.controller.ErrorController
import fi.iki.elonen.NanoHTTPD
import java.io.IOException

/**
 *
 * @author foglotus
 * @since 2019/2/19
 */
object StaticMediaResponse{
    fun build(mimeType:String,path:String):NanoHTTPD.Response{
        try {
            val data = LanShare.getContext().assets.open(path.substring(1))
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK,mimeType,data,data.available().toLong())
        }catch (e:IOException){
            logDebug(e.message)
        }
        return ErrorController.run()
    }
}