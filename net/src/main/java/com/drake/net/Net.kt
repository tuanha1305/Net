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

@file:Suppress("unused", "FunctionName") @file:JvmName("NetKt")

package com.drake.net

import android.content.Context
import com.bumptech.glide.Glide
import com.drake.net.request.BodyRequest
import com.drake.net.request.Method
import com.drake.net.request.UrlRequest
import com.drake.net.request.execute
import kotlinx.coroutines.*
import java.io.File

// <editor-fold desc="异步请求">


/**
 * 异步网络请求
 *
 * @param path String 请求路径, 非绝对路径会加上[NetConfig.host]为前缀
 * @param tag 可以传递对象给Request请求, 一般用于在拦截器/转换器中进行针对某个接口行为判断
 * @param block 函数中可以配置请求参数
 */
inline fun <reified M> CoroutineScope.Get(
    path: String,
    tag: Any? = null,
    noinline block: UrlRequest.() -> Unit = {}
): Deferred<M> = async(Dispatchers.IO + SupervisorJob()) {
    if (!isActive) throw CancellationException()
    UrlRequest().execute {
        path(path)
        method = Method.GET
        uid(coroutineContext[CoroutineExceptionHandler])
        tag(tag)
        block()
    }
}

inline fun <reified M> CoroutineScope.Post(
    path: String,
    tag: Any? = null,
    noinline block: BodyRequest.() -> Unit = {}
): Deferred<M> = async(Dispatchers.IO + SupervisorJob()) {
    if (!isActive) throw CancellationException()
    BodyRequest().execute {
        path(path)
        method = Method.POST
        uid(coroutineContext[CoroutineExceptionHandler])
        tag(tag)
        block()
    }
}

inline fun <reified M> CoroutineScope.Head(
    path: String,
    tag: Any? = null,
    noinline block: UrlRequest.() -> Unit = {}
): Deferred<M> = async(Dispatchers.IO + SupervisorJob()) {
    if (!isActive) throw CancellationException()
    UrlRequest().execute {
        path(path)
        method = Method.HEAD
        uid(coroutineContext[CoroutineExceptionHandler])
        tag(tag)
        block()
    }
}

inline fun <reified M> CoroutineScope.Options(
    path: String,
    tag: Any? = null,
    noinline block: UrlRequest.() -> Unit = {}
): Deferred<M> = async(Dispatchers.IO + SupervisorJob()) {
    if (!isActive) throw CancellationException()
    UrlRequest().execute {
        path(path)
        method = Method.OPTIONS
        uid(coroutineContext[CoroutineExceptionHandler])
        tag(tag)
        block()
    }
}

inline fun <reified M> CoroutineScope.Trace(
    path: String,
    tag: Any? = null,
    noinline block: UrlRequest.() -> Unit = {}
): Deferred<M> = async(Dispatchers.IO + SupervisorJob()) {
    if (!isActive) throw CancellationException()
    UrlRequest().execute {
        path(path)
        method = Method.TRACE
        uid(coroutineContext[CoroutineExceptionHandler])
        tag(tag)
        block()
    }
}

inline fun <reified M> CoroutineScope.Delete(
    path: String,
    tag: Any? = null,
    noinline block: BodyRequest.() -> Unit = {}
): Deferred<M> = async(Dispatchers.IO + SupervisorJob()) {
    if (!isActive) throw CancellationException()
    BodyRequest().execute {
        path(path)
        method = Method.DELETE
        uid(coroutineContext[CoroutineExceptionHandler])
        tag(tag)
        block()
    }
}

inline fun <reified M> CoroutineScope.Put(
    path: String,
    tag: Any? = null,
    noinline block: BodyRequest.() -> Unit = {}
): Deferred<M> = async(Dispatchers.IO + SupervisorJob()) {
    if (!isActive) throw CancellationException()
    BodyRequest().execute {
        path(path)
        method = Method.PUT
        uid(coroutineContext[CoroutineExceptionHandler])
        tag(tag)
        block()
    }
}

inline fun <reified M> CoroutineScope.Patch(
    path: String,
    tag: Any? = null,
    noinline block: BodyRequest.() -> Unit = {}
): Deferred<M> = async(Dispatchers.IO + SupervisorJob()) {
    if (!isActive) throw CancellationException()
    BodyRequest().execute {
        path(path)
        method = Method.PATCH
        uid(coroutineContext[CoroutineExceptionHandler])
        tag(tag)
        block()
    }
}


/**
 * 异步下载图片, 图片宽高要求要么同时指定要么同时不指定
 * 要求依赖 Glide
 *
 * @param url 请求图片的绝对路径
 * @param with 图片宽度
 * @param height 图片高度
 */
fun CoroutineScope.GetImage(url: String, with: Int = -1, height: Int = -1): Deferred<File> =
        async(Dispatchers.IO + SupervisorJob()) {

            val download = Glide.with(NetConfig.app).download(url)

            val futureTarget = if (with == -1 && height == -1) {
                download.submit()
            } else {
                download.submit(with, height)
            }

            futureTarget.get()
        }

// </editor-fold>

object Net {

    /**
     * 同步网络请求
     *
     * @param path String 请求路径, 非绝对路径会加上[NetConfig.host]为前缀
     * @param tag 可以传递对象给Request请求, 一般用于在拦截器/转换器中进行针对某个接口行为判断
     * @param block 函数中可以配置请求参数
     */
    inline fun <reified M> Get(
        path: String,
        tag: Any? = null,
        noinline block: UrlRequest.() -> Unit = {}
    ) = UrlRequest().execute<UrlRequest, M> {
        path(path)
        method = Method.GET
        tag(tag)
        block()
    }

    inline fun <reified M> Post(
        path: String,
        tag: Any? = null,
        noinline block: BodyRequest.() -> Unit = {}
    ) = BodyRequest().execute<BodyRequest, M> {
        path(path)
        method = Method.POST
        tag(tag)
        block()
    }

    inline fun <reified M> Head(
        path: String,
        tag: Any? = null,
        noinline block: UrlRequest.() -> Unit = {}
    ) = UrlRequest().execute<UrlRequest, M> {
        path(path)
        method = Method.HEAD
        tag(tag)
        block()
    }

    inline fun <reified M> Options(
        path: String,
        tag: Any? = null,
        noinline block: UrlRequest.() -> Unit = {}
    ) = UrlRequest().execute<UrlRequest, M> {
        path(path)
        method = Method.OPTIONS
        tag(tag)
        block()
    }

    inline fun <reified M> Trace(
        path: String,
        tag: Any? = null,
        noinline block: UrlRequest.() -> Unit = {}
    ) = UrlRequest().execute<UrlRequest, M> {
        path(path)
        method = Method.TRACE
        tag(tag)
        block()
    }

    inline fun <reified M> Delete(
        path: String,
        tag: Any? = null,
        noinline block: BodyRequest.() -> Unit = {}
    ) = BodyRequest().execute<BodyRequest, M> {
        path(path)
        method = Method.DELETE
        tag(tag)
        block()
    }

    inline fun <reified M> Put(
        path: String,
        tag: Any? = null,
        noinline block: BodyRequest.() -> Unit = {}
    ) = BodyRequest().execute<BodyRequest, M> {
        path(path)
        method = Method.PUT
        tag(tag)
        block()
    }

    inline fun <reified M> Patch(
        path: String,
        tag: Any? = null,
        noinline block: BodyRequest.() -> Unit = {}
    ) = BodyRequest().execute<BodyRequest, M> {
        path(path)
        method = Method.PATCH
        tag(tag)
        block()
    }

    /**
     * 同步下载图片, 图片宽高要求要么同时指定要么同时不指定
     * 要求依赖 Glide
     *
     * @param url 请求图片的绝对路径
     * @param with 图片宽度
     * @param height 图片高度
     */
    fun Context.GetImage(url: String, with: Int = 0, height: Int = 0): File {

        Glide.with(this).downloadOnly()
        val download = Glide.with(this).download(url)
        val futureTarget = if (with == 0 && height == 0) {
            download.submit()
        } else {
            download.submit(with, height)
        }
        return futureTarget.get()
    }
}


