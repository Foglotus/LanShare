package com.foglotus.main.mine.util

import com.foglotus.main.mine.view.TypeImage


/**
 *
 * @author foglotus
 * @since 2019/2/27
 */
object FileImgUtil{
    fun getTypeImg(extension:String):Int{
        return when(extension){
            "txt" ->    TypeImage.TXT
            "pdf" ->    TypeImage.PDF
            "zip" ->    TypeImage.ZIP
            "rar" ->    TypeImage.ZIP
            "apk" ->    TypeImage.APK
            "png" ->    TypeImage.PNG
            "jpg" ->    TypeImage.JPG
            "gif" ->    TypeImage.GIF
            "jpeg" ->   TypeImage.JPEG
            "flv" ->    TypeImage.FLV
            "avi" ->    TypeImage.AVI
            "rmvb" ->   TypeImage.RMVB
            "mp4" ->    TypeImage.MP4
            "flac" ->   TypeImage.FLAC
            "mp3" ->    TypeImage.MP3
            "html" ->   TypeImage.HTML
            "css" ->    TypeImage.HTML
            "js" ->     TypeImage.HTML
            "doc" ->    TypeImage.DOC
            "docx" ->   TypeImage.DOCX
            "ppt"  ->   TypeImage.PPT
            "xls" ->    TypeImage.EXCEL
            else ->     TypeImage.FILE
        }
    }
}