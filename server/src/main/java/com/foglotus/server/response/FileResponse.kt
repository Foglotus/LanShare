package com.foglotus.server.response

import com.foglotus.server.controller.ErrorController
import com.foglotus.server.util.MimeUtil
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.newFixedLengthResponse
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

/**
 *
 * @author foglotus
 * @since 2019/2/21
 */
object FileResponse {
    fun build(header: MutableMap<String, String>,filePath:String): NanoHTTPD.Response{
        val file = File(filePath)
        return if(file.exists() && file.isFile)
            return serveFile(header,file)
        else ErrorController.run()
    }

    private fun serveFile(header: MutableMap<String, String>, file:File):NanoHTTPD.Response{
        var res: NanoHTTPD.Response
        try {
            // Calculate etag
            val etag =
                Integer.toHexString((file.getAbsolutePath() + file.lastModified() + "" + file.length()).hashCode())

            // Support (simple) skipping:
            var startFrom: Long = 0
            var endAt: Long = -1
            var range = header.get("range")
            if (range != null) {
                if (range!!.startsWith("bytes=")) {
                    range = range!!.substring("bytes=".length)
                    val minus = range!!.indexOf('-')
                    try {
                        if (minus > 0) {
                            startFrom = java.lang.Long.parseLong(range!!.substring(0, minus))
                            endAt = java.lang.Long.parseLong(range!!.substring(minus + 1))
                        }
                    } catch (ignored: NumberFormatException) {
                    }

                }
            }

            // get if-range header. If present, it must match etag or else we
            // should ignore the range request
            val ifRange = header.get("if-range")
            val headerIfRangeMissingOrMatching = ifRange == null || etag == ifRange

            val ifNoneMatch = header.get("if-none-match")
            val headerIfNoneMatchPresentAndMatching = ifNoneMatch != null && ("*" == ifNoneMatch || ifNoneMatch == etag)

            // Change return code and add Content-Range header when skipping is
            // requested
            val fileLen = file.length()

            if (headerIfRangeMissingOrMatching && range != null && startFrom >= 0 && startFrom < fileLen) {
                // range request that matches current etag
                // and the startFrom of the range is satisfiable
                if (headerIfNoneMatchPresentAndMatching) {
                    // range request that matches current etag
                    // and the startFrom of the range is satisfiable
                    // would return range from file
                    // respond with not-modified
                    res = newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_MODIFIED, MimeUtil.FILE_ALL, "")
                    res.addHeader("ETag", etag)
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1
                    }
                    var newLen = endAt - startFrom + 1
                    if (newLen < 0) {
                        newLen = 0
                    }

                    val fis = FileInputStream(file)
                    fis.skip(startFrom)

                    res = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.PARTIAL_CONTENT, MimeUtil.FILE_ALL, fis, newLen)
                    res.addHeader("Accept-Ranges", "bytes")
                    res.addHeader("Content-Length", "" + newLen)
                    res.addHeader("Content-Range", "bytes $startFrom-$endAt/$fileLen")
                    res.addHeader("ETag", etag)
                }
            } else {

                if (headerIfRangeMissingOrMatching && range != null && startFrom >= fileLen) {
                    // return the size of the file
                    // 4xx responses are not trumped by if-none-match
                    res = newFixedLengthResponse(NanoHTTPD.Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "")
                    res.addHeader("Content-Range", "bytes */$fileLen")
                    res.addHeader("ETag", etag)
                } else if (range == null && headerIfNoneMatchPresentAndMatching) {
                    // full-file-fetch request
                    // would return entire file
                    // respond with not-modified
                    res = newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_MODIFIED, MimeUtil.FILE_ALL, "")
                    res.addHeader("ETag", etag)
                } else if (!headerIfRangeMissingOrMatching && headerIfNoneMatchPresentAndMatching) {
                    // range request that doesn't match current etag
                    // would return entire (different) file
                    // respond with not-modified

                    res = newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_MODIFIED, MimeUtil.FILE_ALL, "")
                    res.addHeader("ETag", etag)
                } else {
                    // supply the file
                    res = newFixedFileResponse(file, MimeUtil.FILE_ALL)
                    res.addHeader("Content-Length", "" + fileLen)
                    res.addHeader("ETag", etag)
                }
            }
        } catch (ioe: IOException) {
            res = ErrorController.run()
        }


        return res
    }

    @Throws(FileNotFoundException::class)
    private fun newFixedFileResponse(file: File, mime: String): NanoHTTPD.Response {
        val res = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, mime, FileInputStream(file), file.length())
        res.addHeader("Accept-Ranges", "bytes")
        res.addHeader("Content-Disposition","attachment;filename=${file.name}")
        return res
    }
}