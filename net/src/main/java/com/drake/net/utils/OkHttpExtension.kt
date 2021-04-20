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

package com.drake.net.utils

import android.app.Dialog
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.drake.net.NetConfig
import com.drake.net.convert.NetConverter
import com.drake.net.request.BasicRequest
import com.drake.net.scope.DialogCoroutineScope
import okhttp3.OkHttpClient


/**
 * 开启日志
 */
var OkHttpClient.Builder.logEnabled: Boolean
    get() = NetConfig.logEnabled
    set(value) {
        NetConfig.logEnabled = value
    }

/**
 * 转换器
 */
var OkHttpClient.Builder.converter: NetConverter
    get() = NetConfig.converter
    set(value) {
        NetConfig.converter = value
    }

/**
 * 轻量级请求拦截器, 用于添加请求参数
 */
fun OkHttpClient.Builder.onRequest(block: BasicRequest.() -> Unit) {
    NetConfig.onRequest = block
}

/**
 * 全局错误捕获
 */
fun OkHttpClient.Builder.onError(block: Throwable.() -> Unit): OkHttpClient.Builder {
    NetConfig.onError = block
    return this
}

/**
 * 全局缺省页错误捕获
 */
fun OkHttpClient.Builder.onStateError(block: Throwable.(view: View) -> Unit): OkHttpClient.Builder {
    NetConfig.onStateError = block
    return this
}

/**
 * 全局加载对话框设置
 * 设置在使用scopeDialog自动弹出的加载对话框
 */
fun OkHttpClient.Builder.onDialog(block: DialogCoroutineScope.(FragmentActivity) -> Dialog): OkHttpClient.Builder {
    NetConfig.onDialog = block
    return this
}
