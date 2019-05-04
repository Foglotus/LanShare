package com.foglotus.network.event

import com.foglotus.core.event.MessageEvent
import com.foglotus.network.model.DownloadFile

/**
 *
 * @author foglotus
 * @since 2019/4/18
 */
class DownloadFinishEvent(var file:DownloadFile):MessageEvent()