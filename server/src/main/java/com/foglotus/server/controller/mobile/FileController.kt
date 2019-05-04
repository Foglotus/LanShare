package com.foglotus.server.controller.mobile

import android.os.Environment
import com.foglotus.core.Const
import com.foglotus.core.util.DateUtil
import com.foglotus.core.util.GlobalUtil
import com.foglotus.core.extention.logDebug
import com.foglotus.server.response.FileResponse
import com.foglotus.server.annotation.Controller
import com.foglotus.server.annotation.Method
import com.foglotus.server.controller.ErrorController
import com.foglotus.server.event.ServerLogEvent
import com.foglotus.server.model.FileBody
import com.foglotus.server.model.Log
import com.foglotus.server.model.mobile.Result
import com.foglotus.server.response.JsonResponse
import com.google.gson.Gson

import fi.iki.elonen.NanoHTTPD
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.net.URLDecoder


/**
 *
 * @author foglotus
 * @since 2019/2/21
 */
@Controller("mobile-file")
class FileController {
    @Method("list.action")
    fun browser(session: NanoHTTPD.IHTTPSession):NanoHTTPD.Response{
        val path:String = if(session.parms.containsKey("path") && !session.parms["path"].isNullOrEmpty()) URLDecoder.decode(session.parms.getValue("path"),"UTF-8") else GlobalUtil.getSdCardPath()
        logDebug("路径$path")

        val log = Log(session.headers["http-client-ip"],"访问 $path",DateUtil.getDateAndTime(System.currentTimeMillis()))
        log.save()
        EventBus.getDefault().post(ServerLogEvent(log))

        val fileList = ArrayList<FileBody>()
        if(path != "不适用"){
            logDebug("地址：$path")
            val rootFile = File(path)
            if(rootFile.exists()){
                var number = 1;
                rootFile.listFiles().forEach {
                    if(!it.isHidden){
                        if(it.isFile)
                            fileList.add(FileBody(number++,it.name.substring(it.name.lastIndexOf("/")+1),"/mobile-file/download.action?path="+it.path,it.extension,it.exists(),it.length(),it.isFile))
                        else
                            fileList.add(FileBody(number++,it.name.substring(it.name.lastIndexOf("/")+1),it.path,"文件夹",it.exists(),0,it.isFile))
                    }
                }
            }
        }
        fileList.sortBy {
            it.name
        }
        return JsonResponse.build(Gson().toJson(Result(Const.ServerResponse.SUCCESS,"获取成功|$path",fileList)))
    }

    @Method("check.action")
    fun check(session: NanoHTTPD.IHTTPSession):NanoHTTPD.Response{
        val path:String = URLDecoder.decode(session.parms.getValue("path"),"UTF-8")
        if(path.isNotEmpty()){
           val file = File(path)
            if(file.exists() && file.isFile){
                return JsonResponse.build(Gson().toJson(Result(Const.ServerResponse.CHECK_SUCCESS,"文件存在",null)))
            }
        }
        return JsonResponse.build(Gson().toJson(Result(Const.ServerResponse.CHECK_FAILED,"文件不存在",null)),NanoHTTPD.Response.Status.NOT_FOUND)
    }

    @Method("download.action")
    fun download(session: NanoHTTPD.IHTTPSession):NanoHTTPD.Response{

        val path:String = URLDecoder.decode(session.parms.getValue("path"),"UTF-8")
        if(path.isEmpty()){
            return ErrorController.run()
        }
        logDebug(path)
        Log(session.headers["http-client-ip"],"下载 $path",DateUtil.getDateAndTime(System.currentTimeMillis())).save()
        return FileResponse.build(session.headers,path)
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