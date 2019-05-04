package com.foglotus.server.event

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import org.greenrobot.eventbus.EventBus
import android.net.ConnectivityManager


/**
 *
 * @author foglotus
 * @since 2019/2/20
 */
class NetWorkStateReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?) {
        val mWifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val mWifiInfo = mWifiManager.connectionInfo
        val wifi = mWifiInfo.rssi//获取wifi信号强度
        if (wifi > -50 && wifi < 0) {//最强
            EventBus.getDefault().post(NetworkChangeEvent(4))
        } else if (wifi > -70 && wifi < -50) {//较强
            EventBus.getDefault().post(NetworkChangeEvent(3))
        } else if (wifi > -80 && wifi < -70) {//较弱
            EventBus.getDefault().post(NetworkChangeEvent(2))
        } else if (wifi > -100 && wifi < -80) {//微弱
            EventBus.getDefault().post(NetworkChangeEvent(1))

        } else {
            EventBus.getDefault().post(NetworkChangeEvent(0))
        }
    }
}