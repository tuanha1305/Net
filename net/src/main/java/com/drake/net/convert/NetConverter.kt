/*
 * Copyright (C) 2018 Drake, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drake.net.convert

import com.drake.net.exception.ParseException
import com.drake.net.request.fileDir
import com.drake.net.request.fileName
import okhttp3.Response
import okio.ByteString
import okio.buffer
import okio.sink
import java.io.File
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
interface NetConverter {

    @Throws(Exception::class)
    fun <R> onConvert(
        succeed: Type,
        response: Response
    ): R?

    companion object {

        /**
         * 返回数据为字符串内容
         */
        @JvmField
        val DEFAULT = object : NetConverter {

            override fun <R> onConvert(
                succeed: Type,
                response: Response
            ): R? {
                return when (succeed) {
                    String::class.java -> response.body?.string() as R
                    ByteString::class.java -> response.body?.byteString() as R
                    ByteArray::class.java -> response.body?.bytes() as R
                    Response::class.java -> response as R
                    File::class.java -> {
                        val request = response.request
                        val source = response.body?.source() ?: return null
                        val headFileName = response.headers["content-disposition"]
                        val fileName = if (request.fileName == null && headFileName != null) headFileName else request.fileName ?: "temp"
                        val file = File(request.fileDir, fileName)
                        file.createNewFile()
                        file.sink().buffer().writeAll(source)
                        file as R
                    }
                    else -> throw ParseException(response.request, "Only strings are supported by default convert")
                }
            }
        }
    }
}