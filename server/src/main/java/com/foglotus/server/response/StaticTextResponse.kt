package com.foglotus.server.response

import com.foglotus.core.LanShare
import com.foglotus.core.extention.logDebug
import com.foglotus.core.extention.logWarn
import com.foglotus.server.controller.ErrorController
import fi.iki.elonen.NanoHTTPD
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder

/**
 *
 * @author foglotus
 * @since 2019/2/19
 */
object StaticTextResponse{
    fun build(mimeType:String,path:String):NanoHTTPD.Response{
        try {
            var data = StringBuilder()

            logWarn(path.substring(1))

            BufferedReader(InputStreamReader(LanShare.getContext().assets.open(path.substring(1)))).forEachLine {
                data.append(it)
                data.appendln()
            }
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK,mimeType,data.toString())
        }catch (e:IOException){
            logDebug(e.message)
        }
        return ErrorController.run()
    }
}