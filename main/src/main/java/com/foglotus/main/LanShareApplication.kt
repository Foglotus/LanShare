package com.foglotus.main

import android.app.Application
import android.content.Context
import com.foglotus.core.LanShare
import com.foglotus.server.base.ControllerRun
import org.litepal.LitePal

/**
 *
 * @author foglotus
 * @since 2019/3/15
 */
class LanShareApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        LanShare.initialize(this)
        LitePal.initialize(this)
        ControllerRun
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}