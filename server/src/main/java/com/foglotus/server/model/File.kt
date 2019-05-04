package com.foglotus.server.model

import org.litepal.crud.LitePalSupport

/**
 *
 * @author foglotus
 * @since 2019/2/22
 */
class File (var fileName:String,var fileExtension:String,var fileSize:String,var uploadTime:String,var path:String): LitePalSupport(){
    var id:Long = 0
}