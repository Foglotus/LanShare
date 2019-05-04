package com.foglotus.server.filter

import com.foglotus.core.Const
import com.foglotus.server.annotation.Filter
import fi.iki.elonen.NanoHTTPD
import org.litepal.LitePal
import com.foglotus.core.util.SharedUtil
import com.foglotus.core.extention.logInfo
import com.foglotus.server.model.User


/**
 *
 * @author foglotus
 * @since 2019/3/20
 */
@Filter("/file/*")
class PrivilegeFilter :com.foglotus.server.base.Filter(){
    init {
        error = Const.ServerResponse.FORBID_LOGIN
        msg = "您无权访问"
        type ="redirect:/index/index.html"
    }
    override fun chain(session: NanoHTTPD.IHTTPSession):Boolean{
        if(SharedUtil.read(Const.Server.DYNAMIC_SAFE_CODE,true)){
            val uuid = session.cookies.read("uuid") ?: return false
            logInfo("cookie $uuid")
            logInfo("uuid: $uuid")
            val user = LitePal.where("uuid = ?", uuid).limit(1).find(User::class.java)
            logInfo(user.toString())
            return if(user.isEmpty()){
                msg = "您无权访问"
                false
            }else if(!user[0].status){
                msg = "您已被禁止访问"
                type = "redirect:/privilege/forbidden.html"
                false
            }else{
                true
            }
        }
        return true
    }
}