package com.foglotus.server.event

/**
 *
 * @author foglotus
 * @since 2019/3/16
 */
interface ServerRunListener {
    fun onStart()
    fun onStop()
}