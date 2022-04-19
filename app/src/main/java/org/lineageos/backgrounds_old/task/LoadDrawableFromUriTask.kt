/*
 * Copyright (C) 2022 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lineageos.backgrounds_old.task

import android.content.ContentResolver
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.AsyncTask

class LoadDrawableFromUriTask(private val mCallback: Callback) :
    AsyncTask<String?, Void?, Drawable?>(), LoadDrawableFromUriImpl.Callback {
    override fun doInBackground(vararg strings: String?): Drawable? {
        val arg = strings[0]!!
        return LoadDrawableFromUriImpl(this).fetchDrawableFromUri(arg)
    }

    override fun onPostExecute(drawable: Drawable?) {
        super.onPostExecute(drawable)
        mCallback.onCompleted(drawable)
    }

    override val contentResolver: ContentResolver
        get() = mCallback.getContentResolver()
    override val resources: Resources
        get() = mCallback.getResources()

    interface Callback {
        fun onCompleted(drawable: Drawable?)
        fun getContentResolver(): ContentResolver
        fun getResources(): Resources
    }
}