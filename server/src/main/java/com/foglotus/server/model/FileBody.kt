package com.foglotus.server.model

import com.foglotus.core.util.FileUtil
import com.foglotus.core.util.GlobalUtil
import java.net.URLEncoder

/**
 *
 * @author foglotus
 * @since 2019/2/25
 */
class FileBody{
    var id:Int = 1
    lateinit var path:String
    lateinit var name:String
    lateinit var type:String
    var status:Boolean = false
    var size:Long = 0
    var option:Boolean = false

    constructor(id: Int, name: String,path: String, type: String, status: Boolean, size: Long, option: Boolean) {
        this.id = id
        this.path = path
        this.name = name
        this.type = type
        this.status = status
        this.size = size
        this.option = option
    }


    fun getHtml():String{
        var status = if(this.status) "可获得" else "不存在"
        if(type.isEmpty())type="未知"
        if(type == "文件夹")
            return """<tr class="text-c">
                    <td>$id</td>
                    <td><u style="cursor:pointer" class="text-primary" onclick="javascript:location.href='/file/list.html?path=${URLEncoder.encode(path, "UTF-8")}';">$name</u></td>
                    <td><span class="label label-success radius">$type</span></td>
                    <td>${FileUtil.getConvertSize(size)}</td>
                    <td class="text-l">$path</td>
                    <td class="td-status"><span class="label label-success radius">$status</span></td>
                    <td class="td-manage"><a style="text-decoration:none" onClick="member_stop(this,'10001')" href="javascript:;" title="停用"><i class="Hui-iconfont"><img class="imgDownload" src="/web/image/download.png"/></i></a></td>
                </tr>"""
        else
            return """<tr class="text-c">
                    <td>$id</td>
                    <td><u style="cursor:pointer" class="text-primary" onclick="window.open('/file/download.action?path=${getEncodeUri()}')">$name</u></td>
                    <td><span class="label label-success radius">$type</span></td>
                    <td>${FileUtil.getConvertSize(size)}</td>
                    <td class="text-l">$path</td>
                    <td class="td-status"><span class="label label-success radius">$status</span></td>
                    <td class="td-manage"><a style="text-decoration:none" onClick="member_stop(this,'10001')" href="javascript:;" title="停用"><i class="Hui-iconfont"><img class="imgDownload" src="/web/image/download.png"/></i></a></td>
                </tr>"""

    }

    fun getMobileHtml():String{
        if(type.isEmpty())type="未知"
        if(type == "文件夹")
            return """<tr class="text-c">
                    <td><u style="cursor:pointer" class="text-primary" onclick="javascript:location.href='/mobile-file/list.html?path=${URLEncoder.encode(path, "UTF-8")}';">$name</u></td>
                    <td><span class="label label-success radius">$type</span></td>
                </tr>"""
        else
            return """<tr class="text-c">
                    <td><u style="cursor:pointer" class="text-primary" onclick="window.open('/mobile-file/download.action?path=${getEncodeUri()}')">$name</u></td>
                    <td><span class="label label-success radius">$type</span></td>
                </tr>"""
    }

    fun getEncodeUri():String{
        return URLEncoder.encode(path, "UTF-8")
    }
}