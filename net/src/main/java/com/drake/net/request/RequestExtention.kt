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

package com.drake.net.request

import com.drake.net.NetConfig
import com.drake.net.body.OnProgressListener
import com.drake.net.tag.Label
import okhttp3.Request
import java.io.File
import java.net.SocketException
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

val Request.uid: Any?
    get() = tag(Label.Id::class.java)?.id

val Request.fileDir: String?
    get() = tag(Label.FileDir::class.java)?.dir

val Request.fileName: String?
    get() = tag(Label.FileName::class.java)?.name

val Request.onDownloadListener: OnProgressListener?
    get() = tag(OnProgressListener::class.java)

fun Request.tag(name: String): Any? {
    return tag(Label.Map::class.java)?.get(name)
}

/**
 * 执行请求
 */
inline fun <T : BasicRequest, reified R> T.execute(block: T.() -> Unit): R {
    if (R::class.java == File::class.java) client {
        connectTimeout(1, TimeUnit.MINUTES)
        readTimeout(1, TimeUnit.MINUTES)
    }
    block()
    NetConfig.onRequest.invoke(this)
    val newCall = okHttpClient.newCall(buildRequest())
    return try {
        newCall.execute().use {
            var convert = converter
            convert = convert ?: NetConfig.converter
            convert.onConvert<R>(R::class.java, it) as R
        }
    } catch (e: SocketException) {
        throw CancellationException()
    }
}
