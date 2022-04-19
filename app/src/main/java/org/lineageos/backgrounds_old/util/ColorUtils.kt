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
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette

object ColorUtils {
    @JvmStatic
    fun extractPalette(drawable: Drawable): Palette {
        val bm = TypeConverter.drawableToBitmap(drawable)
        return Palette.from(bm).generate()
    }

    @JvmStatic
    fun extractPaletteFromBottom(drawable: Drawable): Palette {
        val originalBm = TypeConverter.drawableToBitmap(drawable)
        // Crop bottom 20%
        val cropY = (originalBm.height * 0.8f).toInt()
        val bottomPart = Bitmap.createBitmap(
            originalBm, 0, cropY,
            originalBm.width, originalBm.height - cropY
        )
        return Palette.from(bottomPart).generate()
    }

    @JvmStatic
    @ColorInt
    fun extractColor(palette: Palette): Int {
        val dominant = palette.getDominantColor(Color.WHITE)
        if (dominant != Color.WHITE) {
            return dominant
        }
        val vibrant = palette.getVibrantColor(Color.WHITE)
        return if (vibrant != Color.WHITE) {
            vibrant
        } else palette.getMutedColor(Color.WHITE)
    }

    @JvmStatic
    @ColorInt
    fun extractContrastColor(palette: Palette): Int {
        var color = Color.BLACK
        val dominant = palette.dominantSwatch
        if (dominant != null) {
            color = dominant.rgb
        } else {
            val vibrant = palette.vibrantSwatch
            if (vibrant != null) {
                color = vibrant.rgb
            }
        }
        return if (isColorLight(color)) Color.BLACK else Color.WHITE
    }

    fun isColorLight(@ColorInt color: Int): Boolean {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val hsl = FloatArray(3)
        ColorUtils.RGBToHSL(red, green, blue, hsl)
        return hsl[2] > 0.76f
    }
}