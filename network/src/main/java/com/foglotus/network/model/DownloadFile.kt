package com.foglotus.network.model

import com.foglotus.core.util.DateUtil
import org.litepal.crud.LitePalSupport
import java.util.*

/**
 *
 * @author foglotus
 * @since 2019/4/18
 */
class DownloadFile(var name:String,var type:String, var time: Date,var path:String,var status:Int,var size:String,var model:Int = PAUSE,var url:String):LitePalSupport(){
    var id:Long = 0
    fun getDes():String{
        val t = DateUtil.getDateAndTime(time.time)
        return "$size   时间$t"
    }

    override fun toString(): String {
        return "DownloadFile(name='$name', type='$type', time=$time, path='$path', status=$status, size='$size', model=$model, id=$id)"
    }
    companion object {
        const val PAUSE = 0
        const val DOWNLOADING = 1
        const val RETRY = 2
    }
}