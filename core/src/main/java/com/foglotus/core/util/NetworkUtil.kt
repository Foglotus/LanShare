package com.foglotus.core.util

import android.content.Context
import android.net.wifi.WifiManager
import com.foglotus.core.LanShare
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

/**
 *
 * @author foglotus
 * @since 2019/2/15
 */
object NetworkUtil{
    /**
     * 获取设备IP地址
     * @return ip:String or 127.0.0.1
     */
    fun getDeviceIP(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.getInetAddresses()
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress() && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress().toString()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return "127.0.0.1"
    }

    fun getWifiLevel():Int{
        val mWifiManager = LanShare.getContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        val mWifiInfo = mWifiManager.connectionInfo
        val wifi = mWifiInfo.rssi//获取wifi信号强度
        if (wifi > -50 && wifi < 0) {//最强
            return 4
        } else if (wifi > -70 && wifi < -50) {//较强
            return 3
        } else if (wifi > -80 && wifi < -70) {//较弱
            return 2
        } else if (wifi > -100 && wifi < -80) {//微弱
            return 1

        } else {
            return 0
        }
    }
}