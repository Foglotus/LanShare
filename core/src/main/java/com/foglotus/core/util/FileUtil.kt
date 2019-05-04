package com.foglotus.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File
import android.support.v4.content.FileProvider
import com.foglotus.core.LanShare
import java.io.IOException


/**
 *
 * @author foglotus
 * @since 2019/2/25
 */
object FileUtil{
    /***
     * 获取转换之后的字节大小，如1024会被转换成1K
     */
    fun getConvertSize(number:Long):String = when{
        number < 1024 -> "$number B"
        number < 1024*1024 -> {
            var converted = number/1024L
            "$converted KB"
        }
        number < 1024*1024*1024 -> {
            var converted = number.div(1024*1024L)
            "$converted MB"
        }
        else -> {
            var converted = number.div(1024*1024L*1024L)
            "$converted GB"
        }
    }

    /***
     * 获取文件类型
     */
    fun getFileType(file:String):String{

        var start = file.lastIndexOf('/')

        var filename = file.substring(start+1)

        val dot = filename.lastIndexOf('.')
        if (dot >= 0) {
            return filename.substring(dot + 1).toLowerCase()
        }else{
            return "未知"
        }
    }

    val MIME_MapTable = arrayOf(
        // {后缀名，MIME类型}
        arrayOf(".3gp", "video/3gpp"),
        arrayOf(".apk", "application/vnd.android.package-archive"),
        arrayOf(".asf", "video/x-ms-asf"),
        arrayOf(".avi", "video/x-msvideo"),
        arrayOf(".bin", "application/octet-stream"),
        arrayOf(".bmp", "image/bmp"),
        arrayOf(".c", "text/plain"),
        arrayOf(".class", "application/octet-stream"),
        arrayOf(".conf", "text/plain"),
        arrayOf(".cpp", "text/plain"),
        arrayOf(".doc", "application/msword"),
        arrayOf(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        arrayOf(".xls", "application/vnd.ms-excel"),
        arrayOf(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        arrayOf(".exe", "application/octet-stream"),
        arrayOf(".gif", "image/gif"),
        arrayOf(".gtar", "application/x-gtar"),
        arrayOf(".gz", "application/x-gzip"),
        arrayOf(".h", "text/plain"),
        arrayOf(".htm", "text/html"),
        arrayOf(".html", "text/html"),
        arrayOf(".jar", "application/java-archive"),
        arrayOf(".java", "text/plain"),
        arrayOf(".jpeg", "image/jpeg"),
        arrayOf(".jpg", "image/jpeg"),
        arrayOf(".js", "application/x-javascript"),
        arrayOf(".log", "text/plain"),
        arrayOf(".m3u", "audio/x-mpegurl"),
        arrayOf(".m4a", "audio/mp4a-latm"),
        arrayOf(".m4b", "audio/mp4a-latm"),
        arrayOf(".m4p", "audio/mp4a-latm"),
        arrayOf(".m4u", "video/vnd.mpegurl"),
        arrayOf(".m4v", "video/x-m4v"),
        arrayOf(".mov", "video/quicktime"),
        arrayOf(".mp2", "audio/x-mpeg"),
        arrayOf(".mp3", "audio/x-mpeg"),
        arrayOf(".mp4", "video/mp4"),
        arrayOf(".mpc", "application/vnd.mpohun.certificate"),
        arrayOf(".mpe", "video/mpeg"),
        arrayOf(".mpeg", "video/mpeg"),
        arrayOf(".mpg", "video/mpeg"),
        arrayOf(".mpg4", "video/mp4"),
        arrayOf(".mpga", "audio/mpeg"),
        arrayOf(".msg", "application/vnd.ms-outlook"),
        arrayOf(".ogg", "audio/ogg"),
        arrayOf(".pdf", "application/pdf"),
        arrayOf(".png", "image/png"),
        arrayOf(".pps", "application/vnd.ms-powerpoint"),
        arrayOf(".ppt", "application/vnd.ms-powerpoint"),
        arrayOf(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
        arrayOf(".prop", "text/plain"),
        arrayOf(".rc", "text/plain"),
        arrayOf(".rmvb", "audio/x-pn-realaudio"),
        arrayOf(".rtf", "application/rtf"),
        arrayOf(".sh", "text/plain"),
        arrayOf(".tar", "application/x-tar"),
        arrayOf(".tgz", "application/x-compressed"),
        arrayOf(".txt", "text/plain"),
        arrayOf(".wav", "audio/x-wav"),
        arrayOf(".wma", "audio/x-ms-wma"),
        arrayOf(".wmv", "audio/x-ms-wmv"),
        arrayOf(".wps", "application/vnd.ms-works"),
        arrayOf(".xml", "text/plain"),
        arrayOf(".z", "application/x-compress"),
        arrayOf(".zip", "application/x-zip-compressed"),
        arrayOf("", "*/*")
    )

    /**
     * 打开文件
     *
     * @param file
     */
    fun openFile(file: File, context: Context) {

        val intent = Intent()
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // 设置 intent 的 Action 属性
        intent.action = Intent.ACTION_VIEW
        // 获取文件 file 的 MIME 类型
        val type = getMIMEType(file)
        // 设置 intent 的 data 和 Type 属性。
        if(AndroidVersion.hasMarshmallow()){
            val authority = LanShare.getPackageName() + ".provider"
            intent.setDataAndType(FileProvider.getUriForFile(context, authority, file),type)
        }else{
            intent.setDataAndType(Uri.fromFile(file), type)
        }

        // 跳转
        context.startActivity(intent)

    }

    /**
     * @param file
     * @return 获得文件后缀名
     */
    private fun getMIMEType(file: File): String {
        var type = "*/*"
        val fName = file.getName()
        // 获取后缀名前的分隔符"."在fName中的位置。
        val dotIndex = fName.lastIndexOf(".")
        if (dotIndex < 0) {
            return type
        }
        /* 获取文件的后缀名 */
        val end = fName.substring(dotIndex, fName.length).toLowerCase()
        if (end === "")
            return type
        // 在 MIME 和文件类型的匹配表中找到对应的 MIME 类型。
        for (i in MIME_MapTable.indices) { // MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end == MIME_MapTable[i][0])
                type = MIME_MapTable[i][1]
        }
        return type
    }

    @Throws(IOException::class)
    fun isExistDir(saveDir: String): String {
        // 下载位置
        val downloadFile = File(saveDir)
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile()
        }
        return downloadFile.absolutePath
    }

}