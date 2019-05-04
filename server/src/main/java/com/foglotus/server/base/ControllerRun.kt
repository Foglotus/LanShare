package com.foglotus.server.base

import android.content.Context
import android.util.Log
import com.foglotus.core.LanShare
import com.foglotus.core.extention.logInfo
import com.foglotus.server.annotation.Controller
import com.foglotus.server.annotation.Filter
import com.foglotus.server.annotation.Method
import com.foglotus.server.controller.ErrorController
import com.foglotus.server.response.HtmlResponse
import com.foglotus.server.response.JsonResponse
import com.google.gson.Gson
import dalvik.system.DexFile
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.lang.Exception
import java.util.*

/**
 *
 * @author foglotus
 * @since 2019/3/16
 */
object ControllerRun {
    private const val TAG = "ControllerRun"
    private const val EXTRACTED_SUFFIX = ".zip"
    //存储所有的请求
    private val requestMap:HashMap<String,ArrayList<String>> = HashMap()

    private val controllerMap:HashMap<String,Class<*>> = HashMap()

    private val funMap:HashMap<String,HashMap<String,java.lang.reflect.Method>> = HashMap()

    private val filterMap:HashMap<String,Class<*>> = HashMap()

    init {
        search()
    }

    fun search() {
        val paths = getDexFilePaths(LanShare.getContext())
        for (path in paths) {
            var dexfile: DexFile? = null
            try {
                dexfile = if (path.endsWith(EXTRACTED_SUFFIX)) {
                    DexFile.loadDex(path, "$path.tmp", 0)
                } else {
                    DexFile(path)
                }

                logInfo("FILE",path)

                val dexEntries = dexfile!!.entries()
                while (dexEntries.hasMoreElements()) {
                    var className = dexEntries.nextElement()
                    if(className.startsWith("com.foglotus.server")){
                        val clazz = Class.forName(className)
                        var annotations = clazz.annotations
                        annotations.forEach {
                            if(it is Controller){
                                //是控制器类
                                var controllerPath = it.path
                                controllerMap[controllerPath] = clazz//存储控制器clazz
                                //如果不存在，则初始化
                                if(!requestMap.containsKey(controllerPath)){
                                    requestMap[controllerPath] = ArrayList()
                                    funMap[controllerPath] = HashMap()
                                }

                                //遍历所有方法
                                clazz.declaredMethods.forEach {
                                    var funAnnotation = it.getAnnotation(Method::class.java)
                                    if(funAnnotation != null){
                                        logInfo("Controller:$controllerPath method:${funAnnotation.path}")
                                        //存在被Method注解的方法
                                        requestMap[controllerPath]!!.add(funAnnotation.path)//存入方法

                                        funMap[controllerPath]!!.put(funAnnotation.path,it)
                                    }
                                }
                            }else if(it is Filter){
                                logInfo("Filter ${it.path}")
                                logInfo(className)
                                logInfo(clazz.simpleName)
                                if(it.path.endsWith("*")){
                                    filterMap.put(it.path.replace("*",".*"),clazz)
                                }else{
                                    filterMap.put(it.path,clazz)
                                }
                                return@forEach
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                Log.w(TAG, "An exception occurred while registering components.", e)
            } finally {
                if (dexfile != null) {
                    try {
                        dexfile.close()
                    } catch (ignore: Throwable) {
                    }

                }
            }
        }


    }
    private fun getDexFilePaths(context: Context): List<String> {
        val appInfo = context.applicationInfo
        val sourceApk = File(appInfo.sourceDir)

        logInfo("sourceApk",sourceApk.absolutePath)


        val sourcePaths = ArrayList<String>()
        sourcePaths.add(appInfo.sourceDir)

        Log.i("parent",sourceApk.parent)

        File(sourceApk.parent).listFiles().forEach {
            if(it.isFile && !it.name.contains("split_lib_resources")){
                sourcePaths.add(it.absolutePath)
            }
        }
        Log.i("appInfoSourceDir",appInfo.sourceDir)

        return sourcePaths
    }

    fun run(c:String,m:String,session:NanoHTTPD.IHTTPSession): NanoHTTPD.Response{
        //过滤器调用

        try {
            filterMap.forEach{
                if(session.uri.matches(it.key.toRegex())){
                    logInfo("filter:${it.key}")
                    logInfo("${it.value.`package`}")
                    var method = it.value.getMethod("chain",NanoHTTPD.IHTTPSession::class.java)
                    var obj = it.value.newInstance() as com.foglotus.server.base.Filter
                    var result = method.invoke(obj,session) as Boolean
                    if(!result){
                        if(obj.type.startsWith("redirect")){
                            return HtmlResponse("web/redirect.html",HashMap<String,String>().apply {
                                put("error",obj.error.toString())
                                put("msg",obj.msg)
                                put("url",obj.type.replace("redirect:",""))
                            }).build()
                        }else if(obj.type.startsWith("mobile")){
                            return JsonResponse.build(Gson().toJson(com.foglotus.server.model.mobile.Result(obj.error,obj.msg,null)),NanoHTTPD.Response.Status.FORBIDDEN)
                        }
                        return@forEach
                    }
                    logInfo("Filter执行了${it.key} 结果是 $result")
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }



        //是否存在此方法，不存在则调用ErrorController
        if(requestMap.containsKey(c) && requestMap[c]!!.contains(m)){
            try {
                var controller = controllerMap[c]
                var method = funMap[c]!![m]
                return method!!.invoke(controller!!.newInstance(),session) as NanoHTTPD.Response
            }catch (e:Exception){
                e.printStackTrace()
                return ErrorController.run()
            }

        }else{
            return ErrorController.run()
        }
    }
}