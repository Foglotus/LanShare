package com.foglotus.network.request

import com.foglotus.network.model.Callback
import com.foglotus.network.model.Privilege
import com.foglotus.network.util.NetworkConst

/**
 *
 * @author foglotus
 * @since 2019/4/12
 */
class PrivilegeRequest:Request() {

    private var uuid:String = ""
    private var code:String = ""

    override fun url(): String {
        return "/mobile-privilege/check.action"
    }

    override fun method(): Int {
        return Request.POST
    }

    override fun listen(callback: Callback?,host:String) {
        setListener(callback,host)
        inFlight(Privilege::class.java)
    }

    override fun params(): Map<String, String>? {
        val params =  HashMap<String,String>()
        params[NetworkConst.UUID] = uuid
        params["code"] = code
        return params
    }

    fun uuid(uuid:String):PrivilegeRequest{
        this.uuid = uuid
        return this
    }

    fun code(code:String):PrivilegeRequest{
        this.code = code
        return this
    }

}