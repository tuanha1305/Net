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

class UrlRequest : BasicRequest() {

    override fun param(name: String, value: String?) {
        httpUrl.setQueryParameter(name, value)
    }

    override fun paramEncoded(name: String, value: String?) {
        httpUrl.setEncodedQueryParameter(name, value)
    }

    override fun param(name: String, value: Number?) {
        httpUrl.setQueryParameter(name, value.toString())
    }

    override fun param(name: String, value: Boolean?) {
        httpUrl.setQueryParameter(name, value.toString())
    }
}