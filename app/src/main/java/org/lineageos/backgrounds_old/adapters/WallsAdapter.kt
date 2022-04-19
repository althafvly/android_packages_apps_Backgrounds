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
package org.lineageos.backgrounds_old.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.lineageos.backgrounds_old.R
import org.lineageos.backgrounds_old.bundle.WallpaperBundle
import org.lineageos.backgrounds_old.bundle.WallpaperType
import org.lineageos.backgrounds_old.holders.UserHolder
import org.lineageos.backgrounds_old.holders.WallpaperHolder
import org.lineageos.backgrounds_old.ui.SelectionInterface
import org.lineageos.backgrounds_old.util.TypeConverter.intToWallpaperType
import org.lineageos.backgrounds_old.util.TypeConverter.wallpaperTypeToInt

class WallsAdapter(private val mCallback: SelectionInterface) :
    RecyclerView.Adapter<WallpaperHolder>() {
    private var mData: List<WallpaperBundle> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WallpaperHolder {
        val type = intToWallpaperType(viewType)
        return when (type) {
            WallpaperType.BUILT_IN, WallpaperType.DEFAULT, WallpaperType.GRADIENT, WallpaperType.MONO -> buildDefaultHolder(
                parent
            )
            else -> buildUserHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: WallpaperHolder, position: Int) {
        val bundle = mData[position]
        holder.bind(bundle)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getItemViewType(position: Int): Int {
        return wallpaperTypeToInt(mData[position].type)
    }

    fun setData(data: List<WallpaperBundle>) {
        val callback = WallsDiffCallback(mData, data)
        DiffUtil.calculateDiff(callback).dispatchUpdatesTo(this)
        mData = data
    }

    // Reified generics when java?
    private fun buildDefaultHolder(parent: ViewGroup): WallpaperHolder {
        val inflater = LayoutInflater.from(parent.context)
        return WallpaperHolder(
            inflater.inflate(
                R.layout.item_wallpaper,
                parent, false
            ), mCallback
        )
    }

    private fun buildUserHolder(parent: ViewGroup): UserHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UserHolder(
            inflater.inflate(
                R.layout.item_wallpaper,
                parent, false
            ), mCallback
        )
    }
}