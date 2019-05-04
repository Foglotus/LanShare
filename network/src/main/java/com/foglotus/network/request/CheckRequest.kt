package com.foglotus.network.request

import com.foglotus.network.model.Callback
import com.foglotus.network.model.Check

/**
 *
 * @author foglotus
 * @since 2019/4/12
 */
class CheckRequest:Request() {
    override fun url(): String {
        return "/mobile-check/check.action"
    }

    override fun method(): Int {
        return GET
    }

    override fun listen(callback: Callback?,host:String) {
        setListener(callback,host)
        inFlight(Check::class.java)
    }
}