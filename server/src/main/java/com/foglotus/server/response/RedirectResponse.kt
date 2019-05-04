package com.foglotus.server.response

import com.foglotus.core.LanShare
import com.foglotus.core.extention.logDebug
import com.foglotus.core.extention.logInfo
import com.foglotus.server.controller.ErrorController
import com.foglotus.server.model.Redirect
import com.foglotus.server.util.MimeUtil
import fi.iki.elonen.NanoHTTPD
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder

/**
 *
 * @author foglotus
 * @since 2019/3/21
 */
class RedirectResponse {
    fun build(redirect: Redirect):NanoHTTPD.Response{
        try {
            val data = LanShare.getContext().assets.open("web/redirect.html")

            val sb = StringBuilder()

            BufferedReader(InputStreamReader(data)).forEachLine {
                sb.append(it)
                sb.appendln()
            }

            val result = sb.toString().replace("<!--error-->",redirect.error).replace("<!--msg-->",redirect.msg).replace("<!--url-->",redirect.url)
            logInfo(result)
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.REDIRECT,MimeUtil.TEXT_HTML,result)
        }catch (e: IOException){
            logDebug(e.message)
        }
        return ErrorController.run()
    }
}