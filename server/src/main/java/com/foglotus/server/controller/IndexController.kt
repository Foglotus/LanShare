package com.foglotus.server.controller

import com.foglotus.server.annotation.Controller
import com.foglotus.server.annotation.Method
import com.foglotus.server.model.Redirect
import com.foglotus.server.response.HtmlResponse
import com.foglotus.server.response.RedirectResponse
import com.foglotus.server.util.UserUtil
import fi.iki.elonen.NanoHTTPD

/**
 *
 * @author foglotus
 * @since 2019/2/19
 */
@Controller("index")
class IndexController {
    @Method("index.html")
    fun index(session: NanoHTTPD.IHTTPSession):NanoHTTPD.Response{
        val user = UserUtil.getUserByUUID(session.cookies.read("uuid"))
        return if(user != null && user.status){
            RedirectResponse().build(Redirect("301","您已登陆，即将跳转","/file/list.html"))
        }else if(user != null && !user.status){
            var param = mapOf<String,String>(
                "title" to "禁止访问"
            )
            HtmlResponse("web/forbidden.html",param).build()
        }else{
            var param = mapOf<String,String>(
                "title" to "首页"
            )
            HtmlResponse("web/index.html",param).build()
        }
    }
}