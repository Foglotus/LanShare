package com.foglotus.main.common.activity

import android.Manifest
import android.view.View
import com.foglotus.core.util.GlobalUtil

import com.foglotus.main.base.activity.BaseActivity
import com.foglotus.main.common.listener.PermissionListener

/**
 *
 * @author foglotus
 * @since 2019/2/16
 */
open class SplashActivity: BaseActivity(){
    /**
     * 记录进入SplashActivity的时间。
     */
    var enterTime: Long = 0

    /**
     * 判断是否正在跳转或已经跳转到下一个界面。
     */
    var isForwarding = false


    override fun setupViews() {
        startInitRequest()
    }

    private fun startInitRequest() {
        if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            delayToForward()
        }else{
            handlePermissions(permissionList,object: PermissionListener {
                override fun onGranted() {
                    delayToForward()
                }

                override fun onDenied(deniedPermissions: List<String>) {
                    PermissionActivity.actionStart(activity!!)
                    finish()
                }
            })
        }
    }

    override fun onBackPressed() {
        // 屏蔽手机的返回键
    }


    /**
     * 设置闪屏界面的最大延迟跳转，让用户不至于在闪屏界面等待太久。
     */
    private fun delayToForward() {
        Thread(Runnable {
            GlobalUtil.sleep(MAX_WAIT_TIME.toLong())
            forwardToNextActivity()
        }).start()
    }

    /**
     * 跳转到下一个Activity。如果在闪屏界面停留的时间还不足规定最短停留时间，则会在这里等待一会，保证闪屏界面不至于一闪而过。
     */
    @Synchronized
    open fun forwardToNextActivity() {
        if(isForwarding)return
        else isForwarding = true
        MainActivity.actionStart(this)
        finish()
    }


    companion object {

        private const val TAG = "SplashActivity"

        /**
         * 应用程序在闪屏界面最短的停留时间。
         */
        const val MIN_WAIT_TIME = 1000

        /**
         * 应用程序在闪屏界面最长的停留时间。
         */
        const val MAX_WAIT_TIME = 2000
    }

}