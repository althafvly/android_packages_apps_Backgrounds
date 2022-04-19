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
package org.lineageos.backgrounds_old.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import org.lineageos.backgrounds_old.bundle.WallpaperType

object TypeConverter {
    @JvmStatic
    fun intToWallpaperType(value: Int): WallpaperType {
        return when (value) {
            1 -> WallpaperType.BUILT_IN
            2 -> WallpaperType.DEFAULT
            3 -> WallpaperType.GRADIENT
            4 -> WallpaperType.MONO
            else -> WallpaperType.USER
        }
    }

    @JvmStatic
    fun wallpaperTypeToInt(type: WallpaperType): Int {
        return when (type) {
            WallpaperType.BUILT_IN -> 1
            WallpaperType.DEFAULT -> 2
            WallpaperType.GRADIENT -> 3
            WallpaperType.MONO -> 4
            else -> 0
        }
    }

    @JvmStatic
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            val bm = drawable.bitmap
            if (bm != null) {
                return bm
            }
        }
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        val bm = Bitmap.createBitmap(
            if (drawableWidth > 0) drawableWidth else 1000,
            if (drawableHeight > 0) drawableHeight else 1000,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bm)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bm
    }
}