package com.foglotus.server.controller.mobile

import com.foglotus.core.LanShare
import com.foglotus.core.util.DateUtil
import com.foglotus.core.util.FileUtil
import com.foglotus.core.extention.logDebug
import com.foglotus.server.annotation.Controller
import com.foglotus.server.annotation.Method
import com.foglotus.server.base.NanoFileUpload
import com.foglotus.server.controller.UploadErrorController
import com.foglotus.server.controller.UploadSuccessController
import com.foglotus.server.event.ServerUploadEvent
import com.foglotus.server.model.Log
import com.foglotus.server.model.User
import com.foglotus.server.response.HtmlResponse
import fi.iki.elonen.NanoHTTPD
import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.FileUploadException
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.util.Streams
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.HashMap

/**
 *
 * @author foglotus
 * @since 2019/2/21
 */
@Controller("mobile-upload")
class UploadController {
    @Method("index.html")
    fun index(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response{
        var param = mapOf<String,String>(
            "title" to "文件上传"
        )
        return HtmlResponse("web/file-upload.html",param).build()
    }
    @Method("put.action")
    fun upload(session: NanoHTTPD.IHTTPSession):NanoHTTPD.Response {

        val uuid = session.headers["uuid"]
        if(uuid.isNullOrEmpty()){
            return UploadErrorController.run()
        }
        LitePal.where("uuid = ?", uuid).limit(1).find(User::class.java) ?: return UploadErrorController.run()

        var param = mapOf<String, String>()
        if (NanoFileUpload.isMultipartContent(session)) {
            val uploader = NanoFileUpload(DiskFileItemFactory())
            val files = HashMap<String, List<FileItem>>()
            try {
                val iter = uploader.getItemIterator(session)
                while (iter.hasNext()) {
                    val item = iter.next()
                    if(item.fieldName != "file")return UploadErrorController.run()
                    val fileName = item.name
                    val fileItem = uploader.fileItemFactory.createItem(
                        item.fieldName,
                        item.contentType,
                        item.isFormField,
                        fileName
                    )
                    Streams.copy(item.openStream(), fileItem.outputStream, true)
                    Streams.copy(fileItem.inputStream, FileOutputStream(File(LanShare.getServer().uploadPath + fileItem.name)), true)
                    Log(session.headers["http-client-ip"],"上传 $fileName",DateUtil.getDateAndTime(System.currentTimeMillis())).save()
                    val file = com.foglotus.server.model.File(item.name,FileUtil.getFileType(item.name), FileUtil.getConvertSize(fileItem.size),DateUtil.getDateAndTime(System.currentTimeMillis()),LanShare.getServer().uploadPath + fileItem.name)
                    file.save()
                    EventBus.getDefault().post(ServerUploadEvent(file))
                    logDebug("成功")
                }
                return UploadSuccessController.run()

            } catch (e: FileUploadException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return UploadErrorController.run()
    }
}