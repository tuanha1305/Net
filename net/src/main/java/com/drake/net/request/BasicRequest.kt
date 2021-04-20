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
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "NAME_SHADOWING", "RedundantSetter")

package com.drake.net.request

import com.drake.net.NetConfig
import com.drake.net.body.OnProgressListener
import com.drake.net.convert.NetConverter
import com.drake.net.tag.Label
import com.drake.net.utils.lazyField
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.net.URL

abstract class BasicRequest {

    var tags: Label.Map<String, Any?>? = null
    var request: Request.Builder = Request.Builder()
    var httpUrl: HttpUrl.Builder by lazyField { HttpUrl.Builder() }
    var converter: NetConverter? = null
    var method = Method.GET
    var absolutePath = false

    //<editor-fold desc="OkHttpClient">
    var okHttpClient = NetConfig.okHttpClient

    /**
     * 修改当前Request的OkHttpClient配置, 不会影响全局默认的OkHttpClient
     */
    fun client(block: OkHttpClient.Builder.() -> Unit) {
        okHttpClient = okHttpClient.newBuilder().apply(block).build()
    }
    //</editor-fold>

    //<editor-fold desc="URL">
    open fun url(url: HttpUrl) {
        httpUrl = url.newBuilder()
    }

    open fun url(url: String) {
        httpUrl = url.toHttpUrlOrNull()?.newBuilder() ?: return
    }

    open fun url(url: URL) {
        httpUrl = url.toHttpUrlOrNull()?.newBuilder() ?: return
    }

    fun path(path: String) {
        path.toHttpUrlOrNull()
        if (absolutePath) url(path) else {
            url(NetConfig.host)
            httpUrl.addPathSegments(path)
        }
    }

    fun query(name: String, value: String?) {
        httpUrl.setQueryParameter(name, value)
    }

    fun queryEncoded(name: String, value: String?) {
        httpUrl.setEncodedQueryParameter(name, value)
    }

    //</editor-fold>

    //<editor-fold desc="Param">
    abstract fun param(name: String, value: String?)

    abstract fun paramEncoded(name: String, value: String?)

    abstract fun param(name: String, value: Number?)

    abstract fun param(name: String, value: Boolean?)

    //</editor-fold>

    //<editor-fold desc="Tag">
    fun tag(tag: Any?) {
        request.tag(tag)
    }

    fun tag(name: String, tag: Any?) {
        var tags = this.tags
        if (tags == null) tags = Label.Map()
        tags[name] = tag
    }

    //</editor-fold>

    //<editor-fold desc="Header">
    fun addHeader(name: String, value: String) {
        request.addHeader(name, value)
    }

    fun removeHeader(name: String) {
        request.removeHeader(name)
    }

    fun header(name: String, value: String) {
        request.header(name, value)
    }

    fun headers(headers: Headers) {
        request.headers(headers)
    }
    //</editor-fold>

    //<editor-fold desc="Cache">
    fun cacheMode() {

    }

    fun cacheControl(cacheControl: CacheControl) {

    }

    fun cacheKey() {

    }
    //</editor-fold>

    //<editor-fold desc="Download">

    /**
     * 下载文件名
     */
    fun downloadFileName(name: String?) {
        request.tag(Label.FileName::class.java, Label.FileName(name))
    }

    /**
     * 下载文件的保存目录
     */
    fun downloadDir(name: String?) {
        request.tag(Label.FileDir::class.java, Label.FileDir(name))
    }

    /**
     * 下载监听器
     */
    fun setOnDownloadListener(listener: OnProgressListener?) {
        request.tag(OnProgressListener::class.java, listener)
    }

    //</editor-fold>

    fun uid(id: Any?) {
        request.tag(Label.Id::class.java, Label.Id(id))
    }

    open fun buildRequest() = request.method(method.name, null)
        .url(httpUrl.build())
        .tag(Label.Map::class.java, tags)
        .build()
}
