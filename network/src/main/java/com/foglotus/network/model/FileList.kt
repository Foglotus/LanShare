package com.foglotus.network.model

import com.foglotus.network.request.FileRequest

/**
 *
 * @author foglotus
 * @since 2019/4/13
 */
class FileList:Response(){
    var data:MutableList<File> = ArrayList()
    companion object {
        fun getResponse(callback: Callback,host:String,uuid:String,path:String){
            FileRequest().path(path).uuid(uuid).listen(callback,host)
        }
    }
}