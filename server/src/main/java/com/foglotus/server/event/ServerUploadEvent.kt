package com.foglotus.server.event

import com.foglotus.core.event.MessageEvent
import com.foglotus.server.model.File

/**
 *
 * @author foglotus
 * @since 2019/4/1
 */
class ServerUploadEvent(var file:File): MessageEvent()