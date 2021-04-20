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

@file:Suppress("MemberVisibilityCanBePrivate")

package com.drake.net.request

import com.drake.net.body.OnProgressListener
import com.drake.net.utils.JSON
import com.drake.net.utils.lazyField
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class BodyRequest : BasicRequest() {

    var body: RequestBody? = null
    var partBody: MultipartBody.Builder by lazyField { MultipartBody.Builder() }
    var formBody: FormBody.Builder by lazyField { FormBody.Builder() }

    //<editor-fold desc="Param">
    override fun param(name: String, value: String?) {
        formBody.add(name, value ?: return)
    }

    override fun paramEncoded(name: String, value: String?) {
        formBody.addEncoded(name, value ?: return)
    }

    override fun param(name: String, value: Number?) {
        formBody.add(name, value.toString())
    }

    override fun param(name: String, value: Boolean?) {
        formBody.add(name, value.toString())
    }

    fun param(name: String, value: RequestBody?) {
        partBody.addFormDataPart(name, null, value ?: return)
    }

    fun param(name: String, value: ByteString?) {
        partBody.addFormDataPart(name, null, value?.toRequestBody() ?: return)
    }

    fun param(name: String, value: ByteArray?) {
        partBody.addFormDataPart(name, null, value?.toRequestBody() ?: return)
    }

    fun param(name: String, value: File?) {
        partBody.addFormDataPart(name, null, value?.asRequestBody() ?: return)
    }

    fun param(name: String, fileName: String?, value: File?) {
        partBody.addFormDataPart(name, fileName, value?.asRequestBody() ?: return)
    }

    fun param(body: RequestBody, header: Headers? = null) {
        partBody.addPart(header, body)
    }

    fun param(body: MultipartBody.Part) {
        partBody.addPart(body)
    }

    //</editor-fold>

    //<editor-fold desc="JSON">
    fun json(body: JSONObject?) {
        body?.let { partBody.addPart(it.toString().toRequestBody(MediaType.Companion.JSON)) }
    }

    fun json(body: JSONArray?) {
        body?.let { partBody.addPart(it.toString().toRequestBody(MediaType.Companion.JSON)) }
    }

    fun json(body: String?) {
        body?.let { partBody.addPart(it.toRequestBody(MediaType.Companion.JSON)) }
    }

    fun json(body: Map<String, Any?>?) {
        body?.let {
            partBody.addPart(JSONObject(it).toString().toRequestBody(MediaType.Companion.JSON))
        }
    }

    fun json(vararg body: Pair<String, Any?>) {
        body.toMap().let {
            partBody.addPart(JSONObject(it).toString().toRequestBody(MediaType.Companion.JSON))
        }
    }
    //</editor-fold>

    private var onUploadListener: OnProgressListener? = null

    /**
     * 上传进度监听器
     */
    fun setUploadListener(listener: OnProgressListener?) {
        onUploadListener = listener
    }

    override fun buildRequest(): Request {
        if (body != null) {
            return request.method(method.name, body).url(httpUrl.build()).build()
        }
        val form = formBody.build()
        return try {
            partBody.build()
            for (i in 0 until form.size) {
                val name = form.encodedName(i)
                val value = form.encodedValue(i)
                partBody.addFormDataPart(name, value)
            }
            request.method(method.name, partBody.build()).url(httpUrl.build()).build()
        } catch (e: IllegalStateException) {
            request.method(method.name, form).url(httpUrl.build()).build()
        }
    }
}