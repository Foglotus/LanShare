package com.foglotus.server.controller.mobile

import com.foglotus.core.Const
import com.foglotus.core.util.GlobalUtil
import com.foglotus.core.util.SharedUtil
import com.foglotus.core.extention.logInfo
import com.foglotus.server.annotation.Controller
import com.foglotus.server.annotation.Method
import com.foglotus.server.event.ServerGrantEvent
import com.foglotus.server.model.User
import com.foglotus.server.response.JsonResponse
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import java.io.IOException
import java.util.*

/**
 *
 * @author foglotus
 * @since 2019/3/21
 */
@Controller("mobile-privilege")
class PrivilegeController {
    @Method("check.action")
    fun check(session:NanoHTTPD.IHTTPSession):NanoHTTPD.Response{
        if(SharedUtil.read(Const.Server.DYNAMIC_SAFE_CODE,true)){
            val method = session.method
            if(method != NanoHTTPD.Method.POST){
                return JsonResponse.build(Gson().toJson(com.foglotus.server.model.mobile.Result(Const.ServerResponse.NOT_FOUND,"接口不存在","")))
            }
            //val map = session.
            if(session.method == NanoHTTPD.Method.POST){
                try {
                    val file = HashMap<String, String>()
                    session.parseBody(file)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NanoHTTPD.ResponseException) {
                    e.printStackTrace()
                }
            }
            val keyFromClient = session.parms.getValue("code")
            logInfo("走这里---$keyFromClient")
            var uuid = session.parms.getValue("uuid")
            val keyFromServer = GlobalUtil.getSafeCode()
            return if(keyFromClient != null){
                return if(keyFromClient == keyFromServer){
                    //存储用户的IP记录
                    if(uuid == null || uuid.isEmpty()) uuid = UUID.randomUUID().toString()

                    val u = LitePal.where("uuid = ?", uuid).limit(1).find(User::class.java)
                    if(u.isNullOrEmpty()) {
                        val ip = if (session.headers["http-client-ip"] != null) session.headers["http-client-ip"] else "未知"
                        val user = User(uuid, ip!!, "安全码登录", true)
                        user.save()
                        EventBus.getDefault().post(ServerGrantEvent(user))
                    }
                    JsonResponse.build(Gson().toJson(com.foglotus.server.model.mobile.Result(Const.ServerResponse.CHECK_SUCCESS,"验证成功",uuid)))
                }else{
                    JsonResponse.build(Gson().toJson(com.foglotus.server.model.mobile.Result(Const.ServerResponse.ERROR_DYNAMIC_CODE,"安全码错误","")))
                }
            }else{
                JsonResponse.build(Gson().toJson(com.foglotus.server.model.mobile.Result(Const.ServerResponse.EMPTY_DYNAMIC_CODE,"验证码为空","")))
            }
        }else{
            return JsonResponse.build(Gson().toJson(com.foglotus.server.model.mobile.Result(Const.ServerResponse.CHECK_SUCCESS,"服务器未开启安全码","")))
        }
    }
}