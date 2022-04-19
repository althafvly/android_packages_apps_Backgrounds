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

import android.app.WallpaperManager
import android.graphics.drawable.Drawable
import android.os.AsyncTask

class ApplyWallpaperTask(private val mCallbacks: Callback) : AsyncTask<Drawable?, Void?, Boolean>(),
    ApplyWallpaperImpl.Callback {
    override fun doInBackground(vararg drawables: Drawable?): Boolean {
        val drawable = drawables[0]!!
        return ApplyWallpaperImpl(this).apply(drawable)
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)
        mCallbacks.onCompleted(result)
    }

    override val wallpaperManager: WallpaperManager
        get() = mCallbacks.getWallpaperManager()
    override val flags: Int
        get() = mCallbacks.getFlags()

    interface Callback {
        fun onCompleted(result: Boolean)
        fun getWallpaperManager(): WallpaperManager
        fun getFlags(): Int
    }
}