package com.foglotus.main.download.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.foglotus.core.extention.logInfo
import com.foglotus.main.download.activity.DownloadActivity


/**
 *
 * @author foglotus
 * @since 2019/4/18
 */
class UpdateProgressBroadcast(var context: Context?):BroadcastReceiver(){
    private var receiver:UpdateProgressBroadcast = this
    fun registerAction(action: String) {
        val filter = IntentFilter()
        filter.addAction(action)
        context?.registerReceiver(receiver, filter)
    }
    fun unRegisterAction(){
        context?.unregisterReceiver(receiver)
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context is DownloadActivity && intent != null){
            val download = context
            val id = intent.getLongExtra("id",0);
            val process = intent.getIntExtra("progress",0)
            download.updateProgress(id,process)
        }else{
            logInfo("收到广播，但数据为空，不执行")
        }
    }
}