package com.foglotus.server.controller.mobile

import com.foglotus.core.Const
import com.foglotus.server.annotation.Controller
import com.foglotus.server.annotation.Method
import com.foglotus.server.model.mobile.Result
import com.foglotus.server.response.JsonResponse
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD

/**
 *
 * @author foglotus
 * @since 2019/4/12
 */
@Controller("mobile-check")
class CheckController {
    @Method("check.action")
    fun check(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response{
        if(session.headers.containsKey("user-agent")){
            if(session.headers["user-agent"] == "LanShare Android")   {
                return JsonResponse.build(Gson().toJson(Result(Const.ServerResponse.CHECK_SUCCESS,"Success!","")))
            }
        }
        return JsonResponse.build(Gson().toJson(Result(Const.ServerResponse.CHECK_FAILED,"禁止访问!","")))
    }
}