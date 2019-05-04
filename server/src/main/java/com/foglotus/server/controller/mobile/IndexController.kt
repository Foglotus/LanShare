package com.foglotus.server.controller.mobile

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
@Controller("mobile-index")
class IndexController {
    @Method("index.html")
    fun index(session: NanoHTTPD.IHTTPSession):NanoHTTPD.Response{
        val user = UserUtil.getUserByUUID(session.cookies.read("uuid"))
        if(user != null && user.status){
            var param = mapOf<String,String>(
                "title" to "禁止访问"
            )
            return RedirectResponse().build(Redirect("301","您已登陆，即将跳转","/mobile-file/list.html"))
        }else if(user != null && !user.status){
            return HtmlResponse("web/forbidden.html",HashMap<String,String>()).build()
        }else{
            var param = mapOf<String,String>(
                "title" to "首页"
            )
            return HtmlResponse("web/index.html",param).build()
        }
    }
}