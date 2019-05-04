package com.foglotus.network.request

import com.foglotus.network.model.Callback
import com.foglotus.network.model.FileList
import com.foglotus.network.util.NetworkConst
import okhttp3.Headers

/**
 *
 * @author foglotus
 * @since 2019/4/13
 */
class FileRequest:Request() {
    private var path:String = ""
    private var uuid:String = ""
    override fun url(): String {
        return "/mobile-file/list.action"
    }

    override fun method(): Int {
        return Request.GET
    }

    override fun listen(callback: Callback?, host: String) {
        setListener(callback,host)
        inFlight(FileList::class.java)
    }

    override fun params(): Map<String, String>? {
        return HashMap<String,String>().apply {
            put("path",path)
        }
    }

    override fun headers(builder: Headers.Builder): Headers.Builder {
        return super.headers(builder).add(NetworkConst.UUID,uuid)
    }

    fun path(path:String):FileRequest{
        this.path = path
        return this
    }
    fun uuid(uuid:String):FileRequest{
        this.uuid = uuid
        return this
    }
}