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
package org.lineageos.backgrounds_old.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.lineageos.backgrounds_old.R
import org.lineageos.backgrounds_old.bundle.WallpaperBundle
import org.lineageos.backgrounds_old.ui.SelectionInterface
import org.lineageos.backgrounds_old.util.ColorUtils.extractContrastColor
import org.lineageos.backgrounds_old.util.ColorUtils.extractPaletteFromBottom

open class WallpaperHolder(
    itemView: View,
    val callback: SelectionInterface
) : RecyclerView.ViewHolder(itemView) {
    var previewView: ImageView? = null
    open fun bind(bundle: WallpaperBundle) {
        previewView = itemView.findViewById(R.id.item_wallpaper_preview)
        val nameView = itemView.findViewById<TextView>(R.id.item_wallpaper_name)

        // We can't set this in xml layout: https://issuetracker.google.com/issues/37036728
        itemView.clipToOutline = true
        val drawable = bundle.contentDrawable
        if (drawable != null) {
            previewView!!.setImageDrawable(drawable)

            // Tint title for contrast
            val color = extractContrastColor(extractPaletteFromBottom(drawable))
            nameView.setTextColor(color)
        }
        val name = bundle.name
        nameView.text = name
        itemView.setOnClickListener { v: View? ->
            callback.onWallpaperSelected(
                previewView!!,
                bundle
            )
        }
    }
}