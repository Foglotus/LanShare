package com.foglotus.core.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Environment
import android.text.TextUtils
import com.foglotus.core.Const
import com.foglotus.core.LanShare
import com.foglotus.core.extention.logWarn
import java.text.SimpleDateFormat
import java.util.*
import com.foglotus.core.extention.logDebug
import java.io.File


/**
 *
 * @author foglotus
 * @since 2019/2/15
 */
object GlobalUtil {

    private var TAG = "GlobalUtil"
    /**
     * 获取当前应用程序的包名。
     *
     * @return 当前应用程序的包名。
     */
    val appPackage: String
        get() = LanShare.getContext().packageName

    /**
     * 获取当前应用程序的名称。
     * @return 当前应用程序的名称。
     */
    val appName: String
        get() = LanShare.getContext().resources.getString(LanShare.getContext().applicationInfo.labelRes)

    /**
     * 获取当前应用程序的版本名。
     * @return 当前应用程序的版本名。
     */
    val appVersionName: String
        get() = LanShare.getContext().packageManager.getPackageInfo(appPackage, 0).versionName

    /**
     * 获取当前应用程序的版本号。
     * @return 当前应用程序的版本号。
     */
    val appVersionCode: Int
        get() = LanShare.getContext().packageManager.getPackageInfo(appPackage, 0).versionCode

    /**
     * 获取当前时间的字符串，格式为yyyyMMddHHmmss。
     * @return 当前时间的字符串。
     */
    val currentDateString: String
        get() {
            val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
            return sdf.format(Date())
        }

    /**
     * 将当前线程睡眠指定毫秒数。
     *
     * @param millis
     * 睡眠的时长，单位毫秒。
     */
    fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    /**
     * 获取资源文件中定义的字符串。
     *
     * @param resId
     * 字符串资源id
     * @return 字符串资源id对应的字符串内容。
     */
    fun getString(resId: Int): String {
        return LanShare.getContext().resources.getString(resId)
    }

    /**
     * 获取指定资源名的资源id。
     *
     * @param name
     * 资源名
     * @param type
     * 资源类型
     * @return 指定资源名的资源id。
     */
    fun getResourceId(name: String, type: String): Int {
        return LanShare.getContext().resources.getIdentifier(name, type, appPackage)
    }

    /**
     * 获取AndroidManifest.xml文件中，<application>标签下的meta-data值。
     *
     * @param key
     *  <application>标签下的meta-data健
     */
    fun getApplicationMetaData(key: String): String? {
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = LanShare.getContext().packageManager.getApplicationInfo(appPackage, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            logWarn(TAG, e.message, e)
        }
        if (applicationInfo == null) return ""
        return applicationInfo.metaData.getString(key)
    }

    /**
     * 获取请求结果的线索，即将状态码和简单描述组合成一段调试信息。
     *
     * @param status
     * 请求结果的状态码
     * @param msg
     * 请求结果的简单描述
     * @return 请求结果的调试线索。
     */
    fun getResponseClue(status: Int, msg: String): String {
        return "code: $status , msg: $msg"
    }

    /**
     * 获取传入通用资源标识符的后缀名。
     *
     * @param uri
     * 通用资源标识符
     * @return 通用资源标识符的后缀名。
     */
    fun getUriSuffix(uri: String): String {
        if (!TextUtils.isEmpty(uri)) {
            val doubleSlashIndex = uri.indexOf("//")
            val slashIndex = uri.lastIndexOf("/")
            if (doubleSlashIndex != -1 && slashIndex != -1) {
                if (doubleSlashIndex + 1 == slashIndex) {
                    return ""
                }
            }
            val dotIndex = uri.lastIndexOf(".")
            if (dotIndex != -1 && dotIndex > slashIndex) {
                return uri.substring(dotIndex + 1)
            }
        }
        return ""
    }

    /**
     * 生成资源对应的key。
     *
     * @param uri
     * 通用资源标识符
     * @param dir
     * 生成带有指定目录结构的key
     * @return 资源对应的key。
     */
    fun generateKey(uri: String, dir: String): String {
        val suffix = getUriSuffix(uri)
        val uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase()
        return if (TextUtils.isEmpty(dir)) {
            var key = "$currentDateString-$uuid"
            if (!TextUtils.isEmpty(suffix)) {
                key = "$key.$suffix"
            }
            key
        } else {
            var key = "$dir/$currentDateString-$uuid"
            if (!TextUtils.isEmpty(suffix)) {
                key = "$key.$suffix"
            }
            key.replace("//", "/")
        }
    }

    /**
     * 获取转换之后的数字显示，如123456会被转换成12.3万。
     * @param number
     * 原始数字
     * @return 转换之后的数字。
     */
    fun getConvertedNumber(number: Int) = when {
        number < 10000 -> number.toString()
        number < 1000000 -> {
            var converted = String.format(Locale.ENGLISH, "%.1f", number / 10000.0)
            if (converted.endsWith(".0")) {
                converted = converted.replace(".0", "")
            }
            converted + "万"
        }
        number < 100100100 -> {
            val converted = number / 10000
            converted.toString() + "万"
        }
        else -> {
            var converted = String.format(Locale.ENGLISH, "%.1f", number / 100_000_000.0)
            if (converted.endsWith(".00")) {
                converted = converted.replace(".00", "")
            }
            converted + "亿"
        }
    }

    fun getDefaultUploadPath(p:String ?=null):String{
        var path = if(p == null){
            Environment.getExternalStorageDirectory().path +"/lanshare/upload/"
        }else{
           p
        }
        var file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        logDebug(file.exists().toString());
        logDebug(file.absolutePath);
        return path
    }

    fun getDefaultDownloadPath():String{
        val path = Environment.getExternalStorageDirectory().path +"/lanshare/download/"
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return path
    }

    /**
     * 判断某个应用是否安装。
     * @param packageName
     * 要检查是否安装的应用包名
     * @return 安装返回true，否则返回false。
     */
    fun isInstalled(packageName: String): Boolean {
        val packageInfo: PackageInfo? = try {
            LanShare.getContext().packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return packageInfo != null
    }

    /**
     * 获取当前应用程序的图标。
     */
    fun getAppIcon(): Drawable {
        val packageManager = LanShare.getContext().packageManager
        val applicationInfo = packageManager.getApplicationInfo(appPackage, 0)
        return packageManager.getApplicationIcon(applicationInfo)
    }

    /***
     *
     */
    fun getSafeCode(model:Boolean = false):String{
        return if(model){
            var value = "${Random().nextInt(9)}${Random().nextInt(9)}${Random().nextInt(9)}${Random().nextInt(9)}${Random().nextInt(9)}"
            SharedUtil.save(Const.Server.SAFE_CODE,value)
            value
        }else{
            SharedUtil.read(Const.Server.SAFE_CODE,"10086")
        }
    }

    /**
     * 获取SD卡根目录路径
     *
     * @return
     */
    fun getSdCardPath(): String {
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
     * 判断手机是否安装了QQ。
     */
    fun isQQInstalled() = isInstalled("com.tencent.mobileqq")

    /**
     * 判断手机是否安装了微信。
     */
    fun isWechatInstalled() = isInstalled("com.tencent.mm")

    /**
     * 判断手机是否安装了微博。
     * */
    fun isWeiboInstalled() = isInstalled("com.sina.weibo")
}