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
package org.lineageos.backgrounds_old.factory

import android.content.res.Resources
import androidx.annotation.DrawableRes
import org.lineageos.backgrounds_old.bundle.WallpaperBundle
import org.lineageos.backgrounds_old.bundle.WallpaperType

object GradientWallpaperFactory {
    @JvmStatic
    fun build(
        name: String,
        res: Resources,
        @DrawableRes drawableRes: Int
    ): WallpaperBundle {
        val drawable = res.getDrawable(drawableRes, res.newTheme())
        return WallpaperBundle(name, drawable, drawableRes, WallpaperType.GRADIENT)
    }
}