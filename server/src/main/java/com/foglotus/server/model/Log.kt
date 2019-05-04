package com.foglotus.server.model

import org.litepal.crud.LitePalSupport

/**
 *
 * @author foglotus
 * @since 2019/3/21
 */
class Log:LitePalSupport{

    var id:Long = 0
    var ip:String?="未知"
    var des:String = ""
    var time:String = ""
    constructor()
    constructor(ip:String?="未知",des:String,time:String) {
        this.ip = ip
        this.des = des
        this.time = time
    }

}