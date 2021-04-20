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

package com.drake.net

import android.app.Application
import android.app.Dialog
import android.app.ProgressDialog
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.drake.net.NetConfig.app
import com.drake.net.NetConfig.host
import com.drake.net.NetConfig.onDialog
import com.drake.net.NetConfig.onError
import com.drake.net.NetConfig.onStateError
import com.drake.net.convert.NetConverter
import com.drake.net.exception.RequestParamsException
import com.drake.net.exception.ResponseException
import com.drake.net.exception.ServerResponseException
import com.drake.net.request.BasicRequest
import com.drake.net.scope.DialogCoroutineScope
import com.drake.net.utils.lazyField
import com.drake.tooltip.toast
import com.yanzhenjie.kalle.exception.*
import okhttp3.OkHttpClient
import java.util.concurrent.ExecutionException


/**
 * Net的全局配置
 *
 * @property host 全局的域名或者ip(baseUrl)
 * @property app 全局上下文, 一般执行[initNet]即可, 无需手动赋值
 * @property onDialog 全局加载框
 * @property onError 全局错误处理
 * @property onStateError 全局缺省页错误处理
 */
object NetConfig {

    lateinit var app: Application
    var okHttpClient: OkHttpClient by lazyField { OkHttpClient.Builder().build() }

    var converter: NetConverter = NetConverter.DEFAULT
    var host: String = ""
    var logEnabled = true
    var onRequest: BasicRequest.() -> Unit = { }

    var onDialog: DialogCoroutineScope.(FragmentActivity) -> Dialog = {
        val progress = ProgressDialog(activity)
        progress.setMessage(activity.getString(R.string.net_dialog_msg))
        progress
    }

    var onError: Throwable.() -> Unit = onError@{
        if (logEnabled) printStackTrace()
        if (!this@NetConfig::app.isInitialized) return@onError

        val message = when (this) {
            is NetworkError -> app.getString(R.string.net_network_error)
            is URLError -> app.getString(R.string.net_url_error)
            is HostError -> app.getString(R.string.net_host_error)
            is ConnectTimeoutError -> app.getString(R.string.net_connect_timeout_error)
            is ReadTimeoutError -> app.getString(R.string.net_read_timeout_error)
            is DownloadError -> app.getString(R.string.net_download_error)
            is NoCacheError -> app.getString(R.string.net_no_cache_error)
            is ParseError -> app.getString(R.string.net_parse_error)
            is RequestParamsException -> app.getString(R.string.net_request_error)
            is ServerResponseException -> app.getString(R.string.net_server_error)
            is ExecutionException -> app.getString(R.string.net_image_error)
            is NullPointerException -> app.getString(R.string.net_null_error)
            is ConnectException -> app.getString(R.string.net_connect_exception)
            is WriteException -> app.getString(R.string.net_write_exception)
            is ReadException -> app.getString(R.string.net_read_exception)
            is ResponseException -> msg
            is NetException -> app.getString(R.string.net_error)
            else -> app.getString(R.string.net_other_error)
        }

        app.toast(message)
    }

    var onStateError: Throwable.(view: View) -> Unit = {
        when (this) {
            is ParseError,
            is RequestParamsException,
            is ResponseException,
            is NullPointerException -> onError(this)
            else -> if (logEnabled) printStackTrace()
        }
    }


    /**
     * 取消全部网络请求
     */
    fun cancelAll() {
        NetConfig.okHttpClient.dispatcher.cancelAll()
    }

    /**
     * 取消指定的网络请求
     */
    fun cancel(uid: Any?) {
        if (uid == null) return
        val dispatcher = NetConfig.okHttpClient.dispatcher
        dispatcher.runningCalls().forEach {
            if (it.request().tag() == uid) it.cancel()
        }
        dispatcher.queuedCalls().forEach {
            if (it.request().tag() == uid) it.cancel()
        }
    }
}

