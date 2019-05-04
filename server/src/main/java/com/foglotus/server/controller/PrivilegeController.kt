package com.foglotus.server.controller

import com.foglotus.core.Const
import com.foglotus.core.util.GlobalUtil
import com.foglotus.core.util.SharedUtil
import com.foglotus.server.annotation.Controller
import com.foglotus.server.annotation.Method
import com.foglotus.server.event.ServerGrantEvent
import com.foglotus.server.event.ServerUploadEvent
import com.foglotus.server.model.Result
import com.foglotus.server.model.User
import com.foglotus.server.response.HtmlResponse
import com.foglotus.server.response.JsonResponse
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import fi.iki.elonen.NanoHTTPD
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.HashMap

/**
 *
 * @author foglotus
 * @since 2019/3/21
 */
@Controller("privilege")
class PrivilegeController {
    @Method("check.action")
    fun check(session:NanoHTTPD.IHTTPSession):NanoHTTPD.Response{
        if(SharedUtil.read(Const.Server.DYNAMIC_SAFE_CODE,true)){
            val keyFromClient = session.parms["key"]
            val keyFromServer = GlobalUtil.getSafeCode()
            return if(keyFromClient != null){
                if(keyFromClient == keyFromServer){
                    //存储用户的IP记录
                    val uuid = UUID.randomUUID().toString()
                    session.cookies.set("uuid",uuid,7)
                    val ip = if(session.headers["http-client-ip"] != null) session.headers["http-client-ip"] else "未知"
                    val user = User(uuid,ip!!,"安全码登录",true)
                    user.save()
                    EventBus.getDefault().post(ServerGrantEvent(user))
                    JsonResponse.build(Gson().toJson(Result("ok","验证成功",uuid)))
                }else{
                    JsonResponse.build(Gson().toJson(Result("error","安全码错误","")))
                }
            }else{
                JsonResponse.build(Gson().toJson(Result("error","验证码为空","")))
            }
        }else{
            return JsonResponse.build(Gson().toJson(Result("ok","服务器未开启安全码","")))
        }
    }
    @Method("forbidden.html")
    fun forbidden(session:NanoHTTPD.IHTTPSession):NanoHTTPD.Response{
        return HtmlResponse("web/forbidden.html",HashMap<String,String>()).build()
    }
}