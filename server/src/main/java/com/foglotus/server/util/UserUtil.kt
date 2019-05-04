package com.foglotus.server.util

import com.foglotus.server.model.User
import org.litepal.LitePal

/**
 *
 * @author foglotus
 * @since 2019/3/21
 */
object UserUtil {
    fun getUserByUUID(uuid:String?=null): User?{
        if(uuid == null)return null
        val user = LitePal.where("uuid = ?",uuid).limit(1).find(User::class.java)
        return if(user.isEmpty()){
            null
        }else{
            user.first()
        }
    }
}