package com.foglotus.server.controller

import com.foglotus.core.LanShare
import com.foglotus.server.util.MimeUtil
import fi.iki.elonen.NanoHTTPD
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.text.StringBuilder

/**
 *
 * @author foglotus
 * @since 2019/2/19
 */
object ErrorController {
    fun run():NanoHTTPD.Response{
        var sb = StringBuilder()
        BufferedReader(InputStreamReader(LanShare.getContext().assets.open("web/404.html"))).forEachLine {
            sb.append(it)
        }
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, MimeUtil.TEXT_HTML,sb.toString())
    }
}