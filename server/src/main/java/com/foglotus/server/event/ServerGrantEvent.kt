package com.foglotus.server.event

import com.foglotus.core.event.MessageEvent
import com.foglotus.server.model.User

/**
 *
 * @author foglotus
 * @since 2019/4/5
 */
class ServerGrantEvent(var grant:User):MessageEvent()