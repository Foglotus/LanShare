package com.foglotus.server.base

/*
 * #%L
 * NanoHttpd-apache file upload integration
 * %%
 * Copyright (C) 2012 - 2016 nanohttpd
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the nanohttpd nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import fi.iki.elonen.NanoHTTPD
import org.apache.commons.fileupload.*

import java.io.IOException
import java.io.InputStream

/**
 * @author victor & ritchieGitHub
 */
class NanoFileUpload(fileItemFactory: FileItemFactory) : FileUpload(fileItemFactory) {

    class NanoHttpdContext(private val session: NanoHTTPD.IHTTPSession) : UploadContext {

        override fun contentLength(): Long {
            var size: Long
            try {
                val cl1 = session.headers["content-length"]
                size = java.lang.Long.parseLong(cl1!!)
            } catch (var4: NumberFormatException) {
                size = -1L
            }

            return size
        }

        override fun getCharacterEncoding(): String {
            return "UTF-8"
        }

        override fun getContentType(): String? {
            return this.session.headers["content-type"]
        }

        override fun getContentLength(): Int {
            return contentLength().toInt()
        }

        @Throws(IOException::class)
        override fun getInputStream(): InputStream {
            return session.inputStream
        }
    }

    @Throws(FileUploadException::class)
    fun parseRequest(session: NanoHTTPD.IHTTPSession): List<FileItem> {
        return this.parseRequest(NanoHttpdContext(session))
    }

    @Throws(FileUploadException::class)
    fun parseParameterMap(session: NanoHTTPD.IHTTPSession): Map<String, List<FileItem>> {
        return this.parseParameterMap(NanoHttpdContext(session))
    }

    @Throws(FileUploadException::class, IOException::class)
    fun getItemIterator(session: NanoHTTPD.IHTTPSession): FileItemIterator {
        return super.getItemIterator(NanoHttpdContext(session))
    }

    companion object {

        fun isMultipartContent(session: NanoHTTPD.IHTTPSession): Boolean {
            return session.method == NanoHTTPD.Method.POST && FileUploadBase.isMultipartContent(NanoHttpdContext(session))
        }
    }

}
