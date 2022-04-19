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
import android.util.Log
import org.lineageos.backgrounds_old.util.TypeConverter.drawableToBitmap
import java.io.IOException

internal class ApplyWallpaperImpl(private val mCallback: Callback) {
    fun apply(drawable: Drawable): Boolean {
        val bm = drawableToBitmap(drawable)
        val manager = mCallback.wallpaperManager
        return try {
            manager.setBitmap(bm, null, true, mCallback.flags)
            true
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
            false
        }
    }

    internal interface Callback {
        val wallpaperManager: WallpaperManager
        val flags: Int
    }

    companion object {
        private const val TAG = "ApplyWallpaperImpl"
    }
}