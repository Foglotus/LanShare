package com.foglotus.core.util

import android.os.Build

/**
 *
 * @author foglotus
 * @since 2019/2/15
 */
object AndroidVersion {

    /**
     * 判断当前手机系统版本API是否是16以上。
     * @return 16以上返回true，否则返回false。
     */
    fun hasJellyBean(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
    }

    /**
     * 判断当前手机系统版本API是否是17以上。
     * @return 17以上返回true，否则返回false。
     */
    fun hasJellyBeanMR1(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
    }

    /**
     * 判断当前手机系统版本API是否是18以上。
     * @return 18以上返回true，否则返回false。
     */
    fun hasJellyBeanMR2(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
    }

    /**
     * 判断当前手机系统版本API是否是19以上。
     * @return 19以上返回true，否则返回false。
     */
    fun hasKitkat(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }

    /**
     * 判断当前手机系统版本API是否是21以上。
     * @return 21以上返回true，否则返回false。
     */
    fun hasLollipop(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    /**
     * 判断当前手机系统版本API是否是22以上。
     * @return 22以上返回true，否则返回false。
     */
    fun hasLollipopMR1(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
    }

    /**
     * 判断当前手机系统版本API是否是23以上。
     * @return 23以上返回true，否则返回false。
     */
    fun hasMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    /**
     * 判断当前手机系统版本API是否是24以上。
     * @return 24以上返回true，否则返回false。
     */
    fun hasNougat(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    /**
     * 判断当前手机系统版本API是否是26以上。
     * @return 26以上返回true，否则返回false。
     */
    fun hasOreo():Boolean{
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    /**
     * 判断当前手机系统版本API是否是28以上。
     * @return 28以上返回true，否则返回false。
     */
    fun hasPistachio():Boolean{
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }
}