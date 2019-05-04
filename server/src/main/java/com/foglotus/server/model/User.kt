package com.foglotus.server.model

import com.foglotus.core.util.DateUtil
import org.litepal.crud.LitePalSupport
import java.util.*

/**
 *
 * @author foglotus
 * @since 2019/3/21
 */
class User:LitePalSupport{
    var id:Long = 0
    var time:String = DateUtil.getDateAndTime(System.currentTimeMillis())
    var create:Long = System.currentTimeMillis()
    var uuid:String = ""
    var ip:String = ""
    var des:String = ""
    var status:Boolean = false

    constructor(){

    }
    constructor(uuid: String,ip:String,des:String,status:Boolean){
        this.uuid = uuid
        this.ip = ip
        this.des = des
        this.status = status
    }

    override fun toString(): String {
        return "User(uuid='$uuid', ip='$ip', des='$des', status=$status, id=$id, time='$time', create=$create)"
    }

}