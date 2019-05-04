package com.foglotus.server.response

import com.foglotus.server.util.MimeUtil
import fi.iki.elonen.NanoHTTPD

/**
 *
 * @author foglotus
 * @since 2019/3/21
 */
object JsonResponse {
    fun build(data:String,code: NanoHTTPD.Response.IStatus ? = NanoHTTPD.Response.Status.OK):NanoHTTPD.Response{
        return NanoHTTPD.newFixedLengthResponse(code,MimeUtil.APPLICATION_JSON,data)
    }
}