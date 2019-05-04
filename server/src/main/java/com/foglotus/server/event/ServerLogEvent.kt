package com.foglotus.server.event

import com.foglotus.core.event.MessageEvent
import com.foglotus.server.model.Log

/**
 *
 * @author foglotus
 * @since 2019/4/5
 */
class ServerLogEvent(var log:Log) : MessageEvent()