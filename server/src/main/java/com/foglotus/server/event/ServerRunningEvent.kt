package com.foglotus.server.event

import com.foglotus.core.event.MessageEvent

/**
 *
 * @author foglotus
 * @since 2019/2/19
 */
class ServerRunningEvent(val status:Boolean = false):MessageEvent()