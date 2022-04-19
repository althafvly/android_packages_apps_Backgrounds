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
import android.content.res.Resources
import android.graphics.Color
import org.lineageos.backgrounds_old.R
import org.lineageos.backgrounds_old.bundle.WallpaperBundle
import org.lineageos.backgrounds_old.factory.BuiltInWallpaperFactory
import org.lineageos.backgrounds_old.factory.BuiltInWallpaperFactory.buildDefault
import org.lineageos.backgrounds_old.factory.GradientWallpaperFactory
import org.lineageos.backgrounds_old.factory.MonoWallpaperFactory.build
import org.lineageos.backgrounds_old.factory.UserWallpaperFactory.build

internal class FetchDataImpl(private val mCallbacks: Callback) {
    private val mData: MutableList<WallpaperBundle> = ArrayList()
    fun fetchData(): List<WallpaperBundle> {
        addUser()
        addBuiltIn()
        addColors()
        addGradients()
        return mData
    }

    private fun addUser() {
        mData.add(build(mCallbacks.resources))
    }

    private fun addBuiltIn() {
        val res = mCallbacks.resources

        // System wallpaper first
        val manager = mCallbacks.wallpaperManager
        mData.add(buildDefault(res, manager))

        // Other built-in
        val names = res.getStringArray(R.array.wallpaper_built_in_names)
        val drawables = res.obtainTypedArray(R.array.wallpaper_built_in_drawables)
        for (i in 0 until drawables.length()) {
            val resourceId = drawables.getResourceId(i, 0)
            if (resourceId != 0) {
                mData.add(BuiltInWallpaperFactory.build(names[i], res, resourceId))
            }
        }
        drawables.recycle()
    }

    private fun addColors() {
        val res = mCallbacks.resources
        val names = res.getStringArray(R.array.wallpaper_mono_names)
        val colors = res.obtainTypedArray(R.array.wallpaper_mono_colors)
        for (i in 0 until colors.length()) {
            val color = colors.getColor(i, Color.BLACK)
            mData.add(build(names[i], color))
        }
        colors.recycle()
    }

    private fun addGradients() {
        val res = mCallbacks.resources
        val names = res.getStringArray(R.array.wallpaper_gradient_names)
        val gradients = res.obtainTypedArray(R.array.wallpaper_gradient_drawables)
        for (i in 0 until gradients.length()) {
            val resourceId = gradients.getResourceId(i, 0)
            if (resourceId != 0) {
                mData.add(GradientWallpaperFactory.build(names[i], res, resourceId))
            }
        }
        gradients.recycle()
    }

    interface Callback {
        val resources: Resources
        val wallpaperManager: WallpaperManager
    }
}