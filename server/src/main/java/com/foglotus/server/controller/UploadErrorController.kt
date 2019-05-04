package com.foglotus.server.controller


import com.foglotus.server.util.MimeUtil
import fi.iki.elonen.NanoHTTPD

/**
 *
 * @author foglotus
 * @since 2019/2/22
 */
object UploadErrorController{
    fun run(): NanoHTTPD.Response{
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, MimeUtil.TEXT_PLAIN,"failed" );
    }
}