package com.foglotus.server.base

import com.foglotus.core.extention.logDebug
import com.foglotus.core.extention.logWarn
import com.foglotus.server.controller.ErrorController
import com.foglotus.server.response.StaticMediaResponse
import com.foglotus.server.response.StaticTextResponse
import com.foglotus.server.util.MimeUtil
import fi.iki.elonen.NanoHTTPD

/**
 *
 * @author foglotus
 * @since 2019/3/15
 */
class HttpServer(hostname: String?, port: Int) : NanoHTTPD(hostname, port) {
    override fun serve(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val uri = session.uri

        logDebug("NanoHTTPD","信息${session.method} $uri")


        val decodedQueryParameters = decodeParameters(session.queryParameterString)

        val sb = StringBuilder()
        sb.append("URI: ").append(session.uri)
        sb.appendln()
        sb.append("Method: ").append(session.method)
        sb.appendln()
        sb.append("Headers: ").append(toString(session.headers))
        sb.appendln()
        sb.append("Parms: ").append(toString(session.parms))
        sb.appendln()
        sb.append("Parms (multi values?): ").append(toString(decodedQueryParameters))
        sb.appendln()
        logDebug(sb.toString())



        //首页
        if(uri == "/"){
            return ControllerRun.run("index","index.html",session)
        }
        if(uri.endsWith(".action") or uri.endsWith(".html")){
            //走控制器
            //路由分发
            var dispatch = uri.split("/").filter { !it.isEmpty() }

            logWarn("dispatch：${dispatch[0]} ${dispatch[1]}")

            if(dispatch.size >= 2 ){
                return ControllerRun.run(dispatch[0],dispatch[1],session)
            }
            else{
                return ErrorController.run()
            }

        }
        return if(uri.endsWith(".js")){
            StaticTextResponse.build(MimeUtil.TEXT_JAVASCRIPT,uri)
        }else if(uri.endsWith(".css")){
            StaticTextResponse.build(MimeUtil.TEXT_CSS,uri)
        }else if(uri.endsWith(".png")){
            StaticMediaResponse.build(MimeUtil.IMAGE_PNG,uri)
        }else if(uri.endsWith("jpg")){
            StaticMediaResponse.build(MimeUtil.IMAGE_JPG,uri)
        }else if(uri.endsWith("gif")){
            StaticMediaResponse.build(MimeUtil.IMAGE_GIF,uri)
        }else if(uri.endsWith("ico")){
            StaticMediaResponse.build(MimeUtil.IMAGE_ICON,uri)
        }else{
            ErrorController.run()
        }
    }

    private fun toString(map: Map<String, Any>): String {
        return if (map.size == 0) {
            ""
        } else unsortedList(map)
    }

    private fun unsortedList(map: Map<String, Any>): String {
        val sb = StringBuilder()
        for (entry in map.entries) {
            listItem(sb, entry)
        }
        sb.appendln()
        return sb.toString()
    }

    private fun listItem(sb: StringBuilder, entry:Map.Entry<String,Any>) {
        sb.append(entry.key).append(": ").append(entry.value).appendln()
    }
}