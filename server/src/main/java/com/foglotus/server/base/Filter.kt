package com.foglotus.server.base

import fi.iki.elonen.NanoHTTPD

/**
 *
 * @author foglotus
 * @since 2019/3/20
 */
abstract class Filter{
    var error:Int = 0
    var msg:String = ""
    var type:String = ""
    abstract fun chain(session:NanoHTTPD.IHTTPSession):Boolean
}