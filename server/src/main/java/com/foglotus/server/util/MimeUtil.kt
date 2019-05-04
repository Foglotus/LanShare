package com.foglotus.server.util

/**
 *
 * @author foglotus
 * @since 2019/2/25
 */
interface MimeUtil{
    companion object {
        const val APPLICATION_JSON      = "application/json"
        const val APPLICATION_XML       = "application/xml"
        const val APPLICATION_PDF       = "application/pdf"
        const val IMAGE_GIF             = "image/gif"
        const val IMAGE_JPEG            = "image/jpeg"
        const val IMAGE_JPG            = "image/jpg"
        const val IMAGE_PNG             = "image/png"
        const val MULTIPART_FORM_DATA   = "multipart/form-data"
        const val TEXT_HTML              = "text/html"
        const val TEXT_JAVASCRIPT       = "text/javascript"
        const val TEXT_CSS              = "text/css"
        const val IMAGE_ICON            = "image/x-icon"
        const val TEXT_PLAIN            = "text/plain"
        const val FILE_ALL              = "application/octet-stream"
    }
}