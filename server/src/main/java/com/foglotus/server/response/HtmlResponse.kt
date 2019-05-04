package com.foglotus.server.response
import com.foglotus.core.LanShare
import com.foglotus.core.extention.logDebug
import com.foglotus.core.extention.logInfo
import com.foglotus.server.controller.ErrorController
import com.foglotus.server.util.MimeUtil
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
class HtmlResponse(var path:String,var param:Map<String,String>){
    fun build():NanoHTTPD.Response{
        try {
            val data = LanShare.getContext().assets.open(path)

            val sb = StringBuilder()

            BufferedReader(InputStreamReader(data)).forEachLine {
                sb.append(it)
                sb.appendln()
            }

            var result = sb.toString()

            param.forEach { k, v ->
                logInfo("$k $v")
                result = result.replace("<!--$k-->",v);
            }

            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK,MimeUtil.TEXT_HTML,result)
        }catch (e: IOException){
            logDebug(e.message)
        }
        return ErrorController.run()
    }
}