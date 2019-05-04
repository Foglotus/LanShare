package com.foglotus.main.download.event

import com.foglotus.core.event.MessageEvent
import com.foglotus.network.model.DownloadFile

/**
 *
 * @author foglotus
 * @since 2019/4/19
 */
class AddDownloadEvent(var downloadFile: DownloadFile):MessageEvent()