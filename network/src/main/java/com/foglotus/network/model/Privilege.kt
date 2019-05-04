package com.foglotus.network.model

import com.foglotus.network.request.PrivilegeRequest
import com.google.gson.annotations.SerializedName

/**
 *
 * @author foglotus
 * @since 2019/4/12
 */
class Privilege:Response(){
    @SerializedName("data")
    var uuid:String = ""
    companion object {
        fun getResponse(callback: Callback,host:String,uuid:String,code:String){
            PrivilegeRequest().uuid(uuid).code(code).listen(callback,host)
        }
    }
}