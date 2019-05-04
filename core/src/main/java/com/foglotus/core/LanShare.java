package com.foglotus.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.foglotus.core.model.Server;

/**
 * @author foglotus
 * @since 2019/2/25
 */
public class LanShare {
    /**
     * 全局上下文对面
     */
    private static Context context;

    /**
     * handler
     */
    private static Handler handler;


    public static void initialize(Context c) {
        context = c;
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * server实体
     */
    private final static Server server = new Server("",0,0,false,"");

    /**
     * 获取全局Context，在代码的任意位置都可以调用，随时都能获取到全局Context对象。
     *
     * @return 全局Context对象。
     */
    public static Context getContext() {
        return context;
    }

    /**
     * 获取包名
     */
    public static String getPackageName(){return context.getPackageName();}

    /**
     * 获取创建在主线程上的Handler对象。
     *
     * @return 创建在主线程上的Handler对象。
     */
    public static Handler getHandler() {
        return handler;
    }

    public static Server getServer() {
        return server;
    }

}
