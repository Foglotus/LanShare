package com.foglotus.server.controller

import android.os.Environment
import com.foglotus.core.util.DateUtil
import com.foglotus.core.extention.logDebug
import com.foglotus.server.response.FileResponse
import com.foglotus.server.annotation.Controller
import com.foglotus.server.annotation.Method
import com.foglotus.server.event.ServerLogEvent
import com.foglotus.server.model.FileBody
import com.foglotus.server.model.Log
import com.foglotus.server.response.HtmlResponse

import fi.iki.elonen.NanoHTTPD
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.net.URLDecoder
import kotlin.text.StringBuilder


/**
 *
 * @author foglotus
 * @since 2019/2/21
 */
@Controller("file")
class FileController {
    @Method("list.html")
    fun browser(session: NanoHTTPD.IHTTPSession):NanoHTTPD.Response{
        var path:String = if(session.parms.containsKey("path")) URLDecoder.decode(session.parms.getValue("path"),"UTF-8") else getSdCardPath()
        logDebug(path)

        val log = Log(session.headers["http-client-ip"],"访问 $path",DateUtil.getDateAndTime(System.currentTimeMillis()))
        log.save()
        EventBus.getDefault().post(ServerLogEvent(log))

        var fileList = ArrayList<FileBody>()
        if(path != "不适用"){
            logDebug("地址：$path")
            val rootFile = File(path)
            if(rootFile.exists()){
                var number = 1;
                rootFile.listFiles().forEach {
                    if(!it.isHidden){
                        if(it.isFile)
                            fileList.add(FileBody(number++,it.name.substring(it.name.lastIndexOf("/")+1),it.path,it.extension,it.exists(),it.length(),it.isFile))
                        else
                            fileList.add(FileBody(number++,it.name.substring(it.name.lastIndexOf("/")+1),it.path,"文件夹",it.exists(),0,it.isFile))
                    }
                }
            }
        }

        var sb = StringBuilder()

        fileList.forEach{
            sb.append(it.getHtml())
        }
        var param = mapOf<String,String>(
            "title" to "文件列表",
            "content" to sb.toString()
        )
        return HtmlResponse("web/file-list.html",param).build()
    }
    @Method("download.action")
    fun download(session: NanoHTTPD.IHTTPSession):NanoHTTPD.Response{

        var path:String = URLDecoder.decode(session.parms.getValue("path"),"UTF-8")
        if(path.isNullOrEmpty()){
            return ErrorController.run()
        }
        logDebug(path)
        Log(session.headers["http-client-ip"],"下载 $path",DateUtil.getDateAndTime(System.currentTimeMillis())).save()
        return FileResponse.build(session.headers,path)
    }
    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    private fun isSdCardExist(): Boolean {
        return Environment.getExternalStorageState().equals(
            Environment.MEDIA_MOUNTED
        )
    }

    /**
     * 获取SD卡根目录路径
     *
     * @return
     */
    private fun getSdCardPath(): String {
        val exist = isSdCardExist()
        var sdpath = ""
        if (exist) {
            sdpath = Environment.getExternalStorageDirectory()
                .absolutePath
        } else {
            sdpath = "不适用"
        }
        return sdpath

    }

    /**
     * 获取默认的文件路径
     *
     * @return
     */
    private fun getDefaultFilePath(): String {
        var filepath = ""
        val file = File(
            Environment.getExternalStorageDirectory(),
            "abc.txt"
        )
        if (file.exists()) {
            filepath = file.getAbsolutePath()
        } else {
            filepath = "不适用"
        }
        return filepath
    }
}